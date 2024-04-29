package com.face.controller;

import cn.hutool.json.JSONUtil;
import com.face.bean.Face;
import com.face.bean.OauthClient;
import com.face.bean.result.FaceResult;
import com.face.service.FaceService;
import com.face.service.OauthClientService;
import com.face.service.impl.OTPService;
import com.face.utils.JsonUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/oauth")
@Api("授权登录接口")
@Slf4j
public class AuthorizationController {

    @Autowired
    private OauthClientService oauthClientService;

    @Autowired
    private FaceService faceService;

    @Autowired
    private OTPService otpService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/authorize")
    public RedirectView authorize(@RequestParam("client_id") String clientId,
                                  @RequestParam("redirect_uri") String redirectUri) {
        OauthClient client = oauthClientService.getClientByClientId(clientId);
        if (client != null && client.getWebServerRedirectUrl().equals(redirectUri)) {
            RedirectView redirectView = new RedirectView("http://localhost:8080/otpaouth");
            redirectView.addStaticAttribute("client_id", clientId);
            redirectView.addStaticAttribute("redirect_uri", redirectUri);
            return redirectView;
        }
        return new RedirectView("/");
    }



    @PostMapping("/authorize")
    public String authorizePost(@RequestParam("client_id") String clientId,
                                @RequestParam("redirect_uri") String redirectUri,
                                @RequestParam(value = "fid", required = false) String fid,
                                @RequestParam(value = "otp", required = false) String otp) {
        if (otpService.verifyOTP(fid, otp)) {
            String code = generateAuthorizationCode(clientId);
            return redirectUri + "?code=" + code + "&fid=" + fid;
        } else {
            return redirectUri + "?error=invalid_otp";
        }
    }

    private String generateAuthorizationCode(String clientId) {
        String authorizationCode = generateRandomCode(6);
        storeAuthorizationCode(clientId + ":" + authorizationCode, authorizationCode);
        return authorizationCode;
    }

    private void storeAuthorizationCode(String key, String authorizationCode) {
        stringRedisTemplate.opsForValue().set(key, authorizationCode);
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    @PostMapping("/token")
    public ResponseEntity<OAuth2AccessToken> getToken(@RequestParam("grant_type") String grantType,
                                                      @RequestParam("code") String code,
                                                      @RequestParam("client_id") String clientId,
                                                      @RequestParam("client_secret") String clientSecret,
                                                      @RequestParam("redirect_uri") String redirectUri) throws IOException {
        if ("authorization_code".equals(grantType)) {
            OauthClient client = oauthClientService.getClientByClientId(clientId);
            if (client != null && client.getWebServerRedirectUrl().equals(redirectUri) && client.getClientSecret().equals(clientSecret)) {
                String storedCode = stringRedisTemplate.opsForValue().get(clientId + ":" + code);
                if (storedCode != null) {
                    OAuth2AccessToken accessToken = generateAccessToken(clientId, code);
                    return ResponseEntity.ok(accessToken);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private OAuth2AccessToken generateAccessToken(String clientId, String code) throws IOException {
        // 生成一个随机的 32 位字符串作为 accessToken
        String accessToken = generateRandomAccessToken();

        // 创建一个 DefaultOAuth2AccessToken 对象
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);

        // 设置 accessToken 的过期时间为 1 小时
        token.setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));

        // 将 accessToken 存储到 Redis 中
        storeAccessToken(clientId, code, token);

        return token;
    }

    private String generateRandomAccessToken() {
        // 使用 UUID 生成一个随机的 32 位字符串
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void storeAccessToken(String clientId, String code, OAuth2AccessToken accessToken) throws IOException {
        // 将 accessToken 序列化为 JSON 字符串,并存储到 Redis 中
        // 过期时间为 1 小时
        stringRedisTemplate.opsForValue().set(clientId + ":" + code, JsonUtils.serialize(accessToken), 3600, TimeUnit.SECONDS);
    }

    @GetMapping("/user_info")
    public ResponseEntity<FaceResult> getUserInfo(@RequestParam("client_id") String clientId,
                                                  @RequestParam("code") String code,
                                                  @RequestParam("fid") String fid,
                                                  @RequestHeader("Authorization") String authorization) {
        // 从请求头中获取 accessToken
        String accessToken = authorization.replace("Bearer ", "");

        // 从 Redis 中获取存储的 accessToken 信息
        String storedAccessTokenJson = stringRedisTemplate.opsForValue().get(clientId + ":" + code);
        if (storedAccessTokenJson != null) {
            // 使用 Hutool 提取 accessToken
            String storedAccessToken = JSONUtil.parseObj(storedAccessTokenJson).getStr("access_token");
            if (storedAccessToken != null && storedAccessToken.equals(accessToken)) {
                // 如果 accessToken 有效,则调用 faceService 进行查询操作
                FaceResult faceResult = new FaceResult();
                Face userInfo = faceService.lambdaQuery().eq(Face::getFid, fid).one();
                faceResult.setMsg("授权成功");
                faceResult.setCode(200);
                faceResult.setName(userInfo.getFaceName());
                faceResult.setPhone(userInfo.getPhone());
                faceResult.setIdNo(userInfo.getIdNo());
                faceResult.setFaceBase(userInfo.getFaceBase());
                return ResponseEntity.ok(faceResult);
            }
        }

        // 如果 accessToken 无效,返回 401 Unauthorized 状态码
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}