package com.face.controller;

import com.face.bean.result.ApiResult;
import com.face.bean.result.FaceResult;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import com.face.service.MyFaceDbService;
import com.face.service.impl.OTPService;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@RestController
@RequestMapping("/api")
@Api("人脸对比远程接口")
@Slf4j
public class ApiController {

    @Autowired
    FaceService faceService;

    @Autowired
    OTPService otpservice;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    MyFaceDbService myFaceDbService;


    @PostMapping("/vef")
    @ApiOperation(value="人脸验证接口", notes="根据传入的两个文件进行对比")
    public ApiResult faceApi(@RequestParam String AuthToken, @RequestPart(required = false) MultipartFile image1, @RequestPart(required = false) MultipartFile image2) {
        ApiResult apiResult = new ApiResult();
        if (AuthToken == null || AuthToken.isEmpty()){
            apiResult.setMsg("AuthToken is null");
            apiResult.setCode(400);
            return apiResult;
        }
        if(image1 == null || image2 == null) {
            apiResult.setMsg("两张图片都不能为空");
            apiResult.setCode(400); // Bad Request
            return apiResult;
        }

        // 将 MultipartFile 转换为 base64 编码的字符串
        String imageBase1 = convertMultipartFileToBase64(image1);
        String imageBase2 = convertMultipartFileToBase64(image2);

        return faceService.vefApi(AuthToken, imageBase1, imageBase2);
    }

    private String convertMultipartFileToBase64(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/otp")
    @ApiOperation(value = "OTP认证", notes = "根据id和OPT进行认证")
    public ApiResult authenticate(@RequestParam String fid, @RequestParam String otp) {
        ApiResult apiResult = new ApiResult();
        // 调用接口实现身份验证逻辑
        if (otpservice.verifyOTP(fid, otp)) {
            apiResult.setMsg("授权成功");
            apiResult.setCode(200);
            return apiResult;
        }
        apiResult.setMsg("授权失败,信息不匹配");
        apiResult.setCode(400);
        return apiResult;
    }


    @GetMapping("/sms")
    @ApiOperation(value = "手机短信验证", notes = "根据手机号码查询用户并发送短信")
    public FaceResult phoneSms(@RequestParam String phone) {
        // 调用接口实现身份验证逻辑
        return faceService.phoneSend(phone);
    }

    @GetMapping("/smsvef")
    @ApiOperation(value = "手机核验绑定", notes = "根据验证码进行登录")
    public FaceResult phoneVef(@RequestParam String phone,@RequestParam String code) {
        // 调用接口实现身份验证逻辑
        return faceService.phoneVefLogin(phone, code);
    }

    @PostMapping("/vefone")
    @ApiOperation(value="人脸验证接口", notes="根据传入的两个文件进行对比")
    public ApiResult facedbApi(@RequestParam String AuthToken, @RequestPart(required = false) MultipartFile image1) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ApiResult apiResult = new ApiResult();
        if (AuthToken == null || AuthToken.isEmpty()){
            apiResult.setMsg("AuthToken is null");
            apiResult.setCode(400);
            return apiResult;
        }
        if(image1 == null) {
            apiResult.setMsg("图片都不能为空");
            apiResult.setCode(400); // Bad Request
            return apiResult;
        }

        // 将 MultipartFile 转换为 base64 编码的字符串
        String imageBase1 = convertMultipartFileToBase64(image1);

        return myFaceDbService.vefApi(AuthToken, imageBase1);
    }
}
