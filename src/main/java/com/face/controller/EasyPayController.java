package com.face.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api("支付接口")
@Slf4j
@AllArgsConstructor
public class EasyPayController {

    private final Config config;


//    支付宝通知
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        log.info("收到支付成功通知");
        String out_trade_no = request.getParameter("out_trade_no");
        System.out.println(request);
        log.info("流水号：{}",out_trade_no);
        return "success";

    }

//  主动查询
   @GetMapping("/notify")
   public String query() throws Exception {
        Factory.setOptions(config);
       AlipayTradeQueryResponse alipayTradeQueryResponse = Factory.Payment.Common().query("973917231923");
       return alipayTradeQueryResponse.getHttpBody();
   }


}
