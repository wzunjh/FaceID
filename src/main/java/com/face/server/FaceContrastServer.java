package com.face.server;

import com.face.bean.result.FaceResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

    public FaceResult faceContrast(String imageA, String imageB) {
        FaceResult faceResult = new FaceResult();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Secret-ID", serviceSecretId); // 添加Secret-ID
            headers.set("Secret-Key", serviceSecretKey); // 添加Secret-Key

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
}
