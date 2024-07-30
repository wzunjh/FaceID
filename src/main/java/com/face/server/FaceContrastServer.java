package com.face.server;

import com.face.bean.result.FaceResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>调用py接口</p>
 **/
@Component
@Data
@Slf4j
public class FaceContrastServer {

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Value("${python.service.id}")
    private String serviceSecretId;

    @Value("${python.service.key}")
    private String serviceSecretKey;

    @Autowired
    private RestTemplate restTemplate; // 使用注入的RestTemplate

    public FaceResult faceContrast(String imageA, String imageB) {
        FaceResult faceResult = new FaceResult();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Secret-ID", serviceSecretId);
            headers.set("Secret-Key", serviceSecretKey);

            Map<String, String> map = new HashMap<>();
            map.put("imageA", imageA);
            map.put("imageB", imageB);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl + "/compare_face", request, Map.class);
            Map<String, Integer> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("similarity_score")) {
                faceResult.setScore(Float.valueOf(responseBody.get("similarity_score")));
                faceResult.setCode(FaceResult.SUCCESS_CODE);
            } else {
                faceResult.setCode(FaceResult.FACE_ERROR);
                faceResult.setMsg("No similarity score found in the response");
            }
        } catch (Exception e) {
            faceResult.setCode(FaceResult.FACE_ERROR);
            faceResult.setMsg(e.getMessage());
        }
        return faceResult;
    }

    public FaceResult faceContrastApi(String imageA, String imageB) {
        FaceResult faceResult = new FaceResult();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Secret-ID", serviceSecretId);
            headers.set("Secret-Key", serviceSecretKey);

            Map<String, String> map = new HashMap<>();
            map.put("imageA", imageA);
            map.put("imageB", imageB);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl + "/compare_faceApi", request, Map.class);
            Map<String, Integer> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("similarity_score")) {
                faceResult.setScore(Float.valueOf(responseBody.get("similarity_score")));
                faceResult.setSex1(String.valueOf(responseBody.get("sex1")));
                faceResult.setSex2(String.valueOf(responseBody.get("sex2")));
                faceResult.setAge1(String.valueOf(responseBody.get("age1")));
                faceResult.setAge2(String.valueOf(responseBody.get("age2")));
                faceResult.setCode(FaceResult.SUCCESS_CODE);
            } else {
                faceResult.setCode(FaceResult.FACE_ERROR);
                faceResult.setMsg("No similarity score found in the response");
            }
        } catch (Exception e) {
            faceResult.setCode(FaceResult.FACE_ERROR);
            faceResult.setMsg(e.getMessage());
        }
        return faceResult;
    }

    public FaceResult idVerification(String image) {
        FaceResult result = new FaceResult();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Secret-ID", serviceSecretId);
            headers.set("Secret-Key", serviceSecretKey);

            Map<String, String> map = new HashMap<>();
            map.put("image", image);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl + "/idVef", request, Map.class);
            Map<String, String> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("name") && responseBody.containsKey("idNo")) {
                result.setName(responseBody.get("name"));
                result.setIdNo(responseBody.get("idNo"));
                result.setCode(FaceResult.SUCCESS_CODE);
            } else {
                result.setCode(400);
                result.setMsg("Failed to verify ID");
            }
        } catch (Exception e) {
            result.setCode(500);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
