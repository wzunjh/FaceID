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
 * <p>调用身份验证接口</p>
 */
@Component
@Data
@Slf4j
public class IdAuthenticationServer {

    @Value("${idenauth.service.url}")
    private String idenAuthUrl;

    @Value("${idenauth.service.appcode}")
    private String appCode;

    public FaceResult authenticateId(String faceName, String idNo) {
        FaceResult faceResult = new FaceResult();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "APPCODE " + appCode);

            Map<String, String> map = new HashMap<>();
            map.put("name", faceName);
            map.put("idNo", idNo);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(idenAuthUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("respMessage") && responseBody.containsKey("respCode")) {
                faceResult.setMsg(responseBody.get("respMessage").toString());
                faceResult.setCode(Integer.parseInt(responseBody.get("respCode").toString()));
            } else {
                faceResult.setCode(FaceResult.FACE_ERROR);
                faceResult.setMsg("No response code or message found in the response");
            }
        } catch (Exception e) {
            faceResult.setCode(FaceResult.FACE_ERROR);
            faceResult.setMsg(e.getMessage());
            log.error("Authentication request failed, message: {}", e.getMessage());
        }
        return faceResult;
    }
}
