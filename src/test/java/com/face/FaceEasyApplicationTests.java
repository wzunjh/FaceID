package com.face;

import com.face.bean.result.ApiResult;
import com.face.bean.result.OtpResult;
import com.face.server.IdAuthenticationServer;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import com.face.service.impl.OTPService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.face.bean.result.FaceResult;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FaceEasyApplicationTests {

    @Autowired
    private IdAuthenticationServer idAuthenticationServer;

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    OTPService otpService;


    @Test
    void contextLoads() {
    }

    @Test
    public void testAuthenticateId() {
        // Mock data for testing
        String faceName = "聂家辉";
        String idNo = "432503200204098770";

        // Call the method to be tested
        FaceResult faceResult = idAuthenticationServer.authenticateId(faceName, idNo);

        // Check if the result is not null
        assertNotNull(faceResult);

        // Check if code and message are properly set
        System.out.println("Code: " + faceResult.getCode());
        System.out.println("Message: " + faceResult.getMsg());

    }

    @Test
    public void testAuthId() {

        // 准备测试数据
        Integer fid = 1;
        String idNo = "432503200204098770";

        // 调用待测试的方法
        FaceResult result = faceService.authId(fid, idNo);

        // 输出结果
        System.out.println("Code: " + result.getCode());
        System.out.println("Msg: " + result.getMsg());
    }

    @Test
    public void testApi(){

        FaceResult faceResult = faceService.apiKey(1);
        System.out.println(faceResult.getApiTime());
        System.out.println("apiKey: " + faceResult.getApiKey());
        System.out.println("Msg: " + faceResult.getMsg());
    }

    @Test
    public void testUpdateApi(){

        FaceResult faceResult = faceService.updateApiKey(1);

        System.out.println("apiKey: " + faceResult.getApiKey());
        System.out.println("Msg: " + faceResult.getMsg());
    }


    @Test
    public void vufAuth(){

        String Auth = "Auth-*****************************";
        Integer fid = 1;

        ApiResult apiResult = faceService.vefAuth(Auth,fid);
        System.out.println(apiResult.getCode()+apiResult.getMsg());
    }

    @Test
    public void Otp(){

        Integer fid = 1;
        OtpResult otpResult = otpService.generateOTP(String.valueOf(1));
        System.out.println(otpResult.getExpirationSeconds()+otpResult.getOtp());
    }



}
