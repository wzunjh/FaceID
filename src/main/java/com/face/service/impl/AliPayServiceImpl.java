package com.face.service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.face.service.AliPayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@AllArgsConstructor
public class AliPayServiceImpl implements AliPayService {

    private final Config config;

    @Override
    public String createPayQRCode(String subject, Integer outTradeNo, Double totalAmount) throws Exception {

        Factory.setOptions(config);

        AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace()
                .preCreate(subject, outTradeNo.toString(), totalAmount.toString());

        // 解析
        String httpBody = response.getHttpBody();
        JSONObject jsonObject = JSONUtil.parseObj(httpBody);
        String qrUrl = jsonObject.getJSONObject("alipay_trade_precreate_response").get("qr_code").toString();
        //打印模拟器扫码测试
        QrCodeUtil.generate(qrUrl,300,300,new File("C://Users//27877//Documents//MuMu共享文件夹//Pictures//pay.png"));

        // 生成二维码并转换为Base64
        QrConfig qrConfig = new QrConfig(300, 300); // 设置二维码配置
        return QrCodeUtil.generateAsBase64(qrUrl, qrConfig, "png");
    }

    @Override
    public Boolean isPaid(Integer outTradeNo) throws Exception {
        Factory.setOptions(config);
        AlipayTradeQueryResponse alipayTradeQueryResponse = Factory.Payment.Common().query(String.valueOf(outTradeNo));
        String httpBody = alipayTradeQueryResponse.getHttpBody();
        if (httpBody != null){
            JSONObject jsonObject = JSONUtil.parseObj(httpBody);
            System.out.println(jsonObject);
            if (jsonObject.getJSONObject("alipay_trade_query_response").get("code").toString().equals("10000")){
                String status = jsonObject.getJSONObject("alipay_trade_query_response").get("trade_status").toString();
                return status.equals("TRADE_SUCCESS");
            }
            return false;
        }
        return false;
    }

}
