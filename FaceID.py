import base64
import re
import cv2
import numpy as np
from flask import Flask, request, jsonify
from flask_cors import CORS
from insightface.app import FaceAnalysis
from concurrent.futures import ThreadPoolExecutor
from functools import lru_cache

app = Flask(__name__)
CORS(app)

# 初始化 FaceAnalysis
face_analysis_app = FaceAnalysis(name='buffalo_l')
face_analysis_app.prepare(ctx_id=0, det_size=(640, 640))

# 创建线程池执行器
executor = ThreadPoolExecutor(max_workers=4)


def resize_image(image, max_size=1024):
    """
    调整图像大小，使最长边不超过 max_size。
    """
    h, w = image.shape[:2]
    scale = min(max_size / h, max_size / w)

    if scale < 1:
        new_size = (int(w * scale), int(h * scale))
        resized_image = cv2.resize(image, new_size, interpolation=cv2.INTER_AREA)
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
    对给定的图像数据进行人脸检测和特征提取，结果使用LRU缓存。
    """
    np_img = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(np_img, cv2.IMREAD_COLOR)
    img = resize_image(img)
    faces = face_analysis_app.get(img)
    if len(faces) == 0:
        raise ValueError("No face found in the image")
    elif len(faces) > 1:
        raise ValueError("Multiple faces found in the image, please provide an image with a single face")
    return faces[0].normed_embedding


def calculate_similarity(embedding1, embedding2):
    """
    计算两个嵌入向量之间的相似度。
    使用余弦相似度进行计算。
    """
    similarity = np.dot(embedding1, embedding2) / (np.linalg.norm(embedding1) * np.linalg.norm(embedding2))
    return float(similarity)


@app.route('/compare_face', methods=['POST'])
def compare_faces():
    try:
        data = request.get_json()
        imageA_base64 = data['imageA']
        imageB_base64 = data['imageB']

        img_data_a = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageA_base64))
        img_data_b = base64.b64decode(re.sub('^data:image/.+;base64,', '', imageB_base64))

        future_embedding1 = executor.submit(get_face_embedding_cached, img_data_a)
        future_embedding2 = executor.submit(get_face_embedding_cached, img_data_b)

        embedding1 = future_embedding1.result()
        embedding2 = future_embedding2.result()

        similarity_score = calculate_similarity(embedding1, embedding2) * 100
        similarity_score_int = int(similarity_score)  # 转换为整数
        return jsonify({"similarity_score": similarity_score_int}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 400


if __name__ == '__main__':
    app.run(port=2024)
