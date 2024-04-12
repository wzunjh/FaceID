package com.face.server;

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

    public String authenticateId(String faceName, String idNo) {
        String result = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "APPCODE " + appCode);

            Map<String, String> map = new HashMap<>();
            map.put("name", faceName);
            map.put("idNo", idNo);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(idenAuthUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                result = response.getBody();
            } else {
                log.error("Request failed----" + "返回状态码: " + response.getStatusCode() + ", message: " + response.getStatusCode().getReasonPhrase());
            }
        } catch (Exception e) {
            log.error("Authentication request failed, message: " + e.getMessage());
        }
        return result;
    }
}
