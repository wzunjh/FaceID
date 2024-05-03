import base64
import os
import re
from concurrent.futures import ThreadPoolExecutor
from functools import lru_cache

import cv2
import numpy as np
from flask import Flask, request, jsonify
from flask_cors import CORS
from insightface.app import FaceAnalysis
from paddleocr import PaddleOCR

app = Flask(__name__)
CORS(app)

# 初始化 FaceAnalysis
face_analysis_app = FaceAnalysis(name='buffalo_l', use_age=True, use_gender=True)
face_analysis_app.prepare(ctx_id=0, det_size=(640, 640))

# 创建线程池执行器
executor = ThreadPoolExecutor(max_workers=20)


def resize_image(image, max_size=1024):
    """
    调整图像大小,使最长边不超过 max_size。
    使用 cv2.INTER_CUBIC 插值方法保留更多图像细节。
    """
    h, w = image.shape[:2]
    scale = min(max_size / h, max_size / w)

    if scale < 1:
        new_size = (int(w * scale), int(h * scale))
        resized_image = cv2.resize(image, new_size, interpolation=cv2.INTER_CUBIC)
        return resized_image
    return image


def decode_base64_to_image(base64_str):
    """
    解码base64编码的图像字符串为OpenCV图像。
    """
    base64_str = re.sub('^data:image/.+;base64,', '', base64_str)
    img_data = base64.b64decode(base64_str)
    np_img = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(np_img, cv2.IMREAD_COLOR)
    img = resize_image(img)
    return img


@lru_cache(maxsize=100)
def get_face_embedding_cached(img_data):
    """
    对给定的图像数据进行人脸检测和特征提取,结果使用LRU缓存。
    """
    np_img = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(np_img, cv2.IMREAD_COLOR)
    img = resize_image(img)

    faces = face_analysis_app.get(img)
    if len(faces) == 0:
        raise ValueError("No face found in the image Or is too close to the camera")
    elif len(faces) > 1:
        raise ValueError("Multiple faces found in the image, please provide an image with a single face")
    face = faces[0]
    # 检查人脸是否被遮挡
    if not is_face_unoccluded(face.landmark_3d_68):
        raise ValueError("Face is occluded")
        # 检查人脸是否为活体
    if not is_face_alive(img):
        raise ValueError("Face is not alive")
    print(f"Face bounding box: {face.bbox}")
    print(f"Face sex: {face.sex}, Face age: {face.age}")

    return face.normed_embedding, face.sex, face.age


@lru_cache(maxsize=100)
def get_face_embedding(img_data):
    """
    对给定的图像数据进行人脸检测和特征提取,结果使用LRU缓存。
    """
    np_img = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(np_img, cv2.IMREAD_COLOR)
    img = resize_image(img)

    faces = face_analysis_app.get(img)
    if len(faces) == 0:
        raise ValueError("No face found in the image Or is too close to the camera")
    elif len(faces) > 1:
        raise ValueError("Multiple faces found in the image, please provide an image with a single face")
    face = faces[0]

    return face.normed_embedding, face.sex, face.age


def is_face_unoccluded(landmark_3d):
    """
    检查人脸是否被遮挡。
    使用 3D 人脸关键点检测的结果进行判断。
    """
    # 定义一个阈值,用于判断关键点是否可见
    visibility_threshold = 0.0
    # 检查每个关键点的可见性
    num_visible_points = 0
    for point in landmark_3d:
        if len(point) >= 3 and point[2] >= visibility_threshold:
            num_visible_points += 1

    # 如果可见的关键点数量超过总数的三分之二,则认为人脸未被遮挡
    if num_visible_points >= len(landmark_3d) - 1:
        return True
    else:
        return False


def is_face_alive(img):
    """
    判断人脸是否为活体
    :param img: 输入的人脸图像
    :return: True 表示活体, False 表示非活体
    """
    # 检测图像中的摩尔纹
    moire_score = detect_moire(img)
    print(moire_score)
    if moire_score > 18:
        return False

    # 检测图像中的成像畸形
    distortion_score = detect_distortion(img)
    print(distortion_score)
    if distortion_score > 25:
        return False

    # 综合评分
    alive_score = 100 - moire_score - distortion_score
    alive_threshold = 40
    return alive_score > alive_threshold


def detect_moire(img):
    """
    检测图像中的摩尔纹
    :param img: 输入的人脸图像
    :return: 摩尔纹检测得分
    """
    edges = cv2.Canny(img, 100, 200)
    moire_score = np.mean(edges)
    return moire_score


def detect_distortion(img):
    """
    检测图像中的成像畸形
    :param img: 输入的人脸图像
    :return: 成像畸形检测得分
    """
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    lap = cv2.Laplacian(gray, cv2.CV_64F)
    distortion_score = np.std(lap)
    return distortion_score


def calculate_similarity(embedding1, embedding2):
    """
    计算两个嵌入向量之间的相似度。
    使用余弦相似度进行计算,并对特征向量进行 L2 归一化。
    """
    embedding1 = embedding1 / np.linalg.norm(embedding1, ord=2)
    embedding2 = embedding2 / np.linalg.norm(embedding2, ord=2)
    similarity = np.dot(embedding1, embedding2)
    return float(similarity)


SECRET_ID = "wzunjh"
SECRET_KEY = "GcJypclbi1t1lTFzCQ"


@app.route('/compare_face', methods=['POST'])
def compare_faces():
    try:
        # 验证 SecretID 和 SecretKey
        secret_id = request.headers.get('Secret-ID')
        secret_key = request.headers.get('Secret-Key')

        if secret_id != SECRET_ID or secret_key != SECRET_KEY:
            return jsonify({"error": "Unauthorized"}), 401

        data = request.get_json()
        imageA_base64 = data['imageA']
        imageB_base64 = data['imageB']

        img_data_a = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageA_base64))
        img_data_b = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageB_base64))

        future_embedding1, sex1, age1 = executor.submit(get_face_embedding_cached, img_data_a).result()
        future_embedding2, sex2, age2 = executor.submit(get_face_embedding_cached, img_data_b).result()

        similarity_score = calculate_similarity(future_embedding1, future_embedding2) * 100 + 10  # 相似度计算(减低误差)
        if similarity_score > 100:
            similarity_score = 100
        similarity_score_int = int(similarity_score)  # 转换为整数

        return jsonify({
            "similarity_score": similarity_score_int,
            "sex1": sex1,
            "age1": age1,
            "sex2": sex2,
            "age2": age2
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 400


@app.route('/compare_faceApi', methods=['POST'])
def compare_facesApi():
    try:
        # 验证 SecretID 和 SecretKey
        secret_id = request.headers.get('Secret-ID')
        secret_key = request.headers.get('Secret-Key')

        if secret_id != SECRET_ID or secret_key != SECRET_KEY:
            return jsonify({"error": "Unauthorized"}), 401

        data = request.get_json()
        imageA_base64 = data['imageA']
        imageB_base64 = data['imageB']

        img_data_a = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageA_base64))
        img_data_b = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageB_base64))

        future_embedding1, sex1, age1 = executor.submit(get_face_embedding, img_data_a).result()
        future_embedding2, sex2, age2 = executor.submit(get_face_embedding, img_data_b).result()

        similarity_score = calculate_similarity(future_embedding1, future_embedding2) * 100 + 10  # 相似度计算(减低误差)
        if similarity_score > 100:
            similarity_score = 100
        similarity_score_int = int(similarity_score)  # 转换为整数
        print(similarity_score_int)
        return jsonify({
            "similarity_score": similarity_score_int,
            "sex1": sex1,
            "age1": age1,
            "sex2": sex2,
            "age2": age2
        }), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 400


@app.route('/idVef', methods=['POST'])
def id_verification():
    try:
        # Verify SecretID and SecretKey
        secret_id = request.headers.get('Secret-ID')
        secret_key = request.headers.get('Secret-Key')

        if secret_id != SECRET_ID or secret_key != SECRET_KEY:
            return jsonify({"error": "Unauthorized"}), 401

        # Extract and decode image from the request
        data = request.get_json()
        image_base64 = data['image']
        img_data = base64.b64decode(re.sub('^data:image/.+;base64,', '', image_base64))
        img = cv2.imdecode(np.frombuffer(img_data, np.uint8), cv2.IMREAD_COLOR)
        img_path = 'temp_id_card.jpg'  # Temporarily save the image for OCR
        cv2.imwrite(img_path, img)

        # Initialize OCR model
        ocr = PaddleOCR(use_angle_cls=True, lang="ch")

        # Perform OCR on the image
        result = ocr.ocr(img_path, cls=True)

        # Use flags to track and extract name and ID number
        name = ''
        id_number = ''
        capture_next = False

        # Process OCR results to extract name and ID number
        for line in result:
            for element in line:
                text = element[1][0]  # Extracted text
                if "姓名" in text:
                    name_index = text.find("姓名") + len("姓名")
                    name = text[name_index:].strip()
                if "公民身份号码" in text:
                    match = re.search(r'\d{17}[\dX]', text)
                    if match:
                        id_number = match.group(0)
                    else:
                        capture_next = True
                elif capture_next:
                    match = re.search(r'\d{17}[\dX]', text)
                    if match:
                        id_number = match.group(0)
                    capture_next = False

        # Delete the temporary image file
        os.remove(img_path)

        return jsonify({"name": name, "idNo": id_number}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 400


if __name__ == '__main__':
    app.run(port=2024)
