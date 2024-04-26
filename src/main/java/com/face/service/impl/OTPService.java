package com.face.service.impl;

import com.face.bean.result.OtpResult;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    public OtpResult generateOTP(String userId) {
        String otpKey = getOTPKey(userId);
        String storedOTP = redisTemplate.opsForValue().get(otpKey);
        OtpResult otpResult = new OtpResult();
        if (storedOTP != null) {
            long ttl = redisTemplate.getExpire(otpKey, TimeUnit.SECONDS);
                otpResult.setExpirationSeconds(ttl);
                otpResult.setOtp(storedOTP);
                otpResult.setCode(200);
        } else {
            String newOTP = generateRandomOTP();
            long expirationSeconds = OTP_EXPIRATION_MINUTES * 60;
            redisTemplate.opsForValue().set(otpKey, newOTP, expirationSeconds, TimeUnit.SECONDS);
            otpResult.setExpirationSeconds(expirationSeconds);
            otpResult.setOtp(newOTP);
            otpResult.setCode(200);
        }
        return otpResult;
    }

    public boolean verifyOTP(String userId, String otp) {
        String otpKey = getOTPKey(userId);
        String storedOTP = redisTemplate.opsForValue().get(otpKey);
        if (storedOTP != null && storedOTP.equals(otp)) {
            redisTemplate.delete(otpKey);
            return true;
        }
        return false;
    }

    private String generateRandomOTP() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getOTPKey(String userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hash = Hashing.sha256().hashString(userId + timestamp, StandardCharsets.UTF_8).toString();
        return "otp:" + hash;
    }
}