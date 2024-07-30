package com.face.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@RestController
@Api("支付接口")
@Slf4j
@AllArgsConstructor
public class EasyPayController {

    private final Config config;

    @GetMapping("/pay")
    public String getEasyPay() throws Exception {
        Factory.setOptions(config);

        AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                .preCreate("iphone 15 128G", "12312312312123", "5999");

        //解析
        String httpBody = response.getHttpBody();

        JSONObject jsonObject = JSONUtil.parseObj(httpBody);

        String qrUrl = jsonObject.getJSONObject("alipay_trade_precreate_response").get("qr_code").toString();

        QrCodeUtil.generate(qrUrl,300,300,new File("C://Users//27877//Desktop//pay.jpg"));

        return httpBody;

    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        log.info("收到支付成功通知");
        String out_trade_no = request.getParameter("out_trade_no");
        log.info("流水号：{}",out_trade_no);
        return "success";

    }


}
