package com.face.controller;

import com.face.bean.result.OtpResult;
import com.face.service.impl.OTPService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/mfa")
@Api("MFA接口")
@Slf4j
public class MfaController {

    @Autowired
    OTPService otpservice;

    @GetMapping("/otp/{fid}")
    @ApiOperation(value = "opt查询",notes = "根据id进行查询")
    public OtpResult orAuth(@PathVariable Integer fid){
        return otpservice.generateOTP(String.valueOf(fid));
    }


}
