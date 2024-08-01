package com.face.controller;

import com.face.bean.Goods;
import com.face.bean.Orders;
import com.face.bean.result.PayResult;
import com.face.service.AliPayService;
import com.face.service.GoodsService;
import com.face.service.OrderService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api("支付接口")
@Slf4j
@AllArgsConstructor
public class EasyPayController {

    @Autowired
    OrderService orderService;

    @Autowired
    AliPayService aliPayService;

    @Autowired
    GoodsService goodsService;



//    商品列表查询
    @GetMapping("/goods")
    public List<Goods> getGoodlist() {
        return goodsService.list();
    }

//    订单查询
    @GetMapping("/orders")
    public List<Orders> getOrderlist(@RequestParam Integer fid) {
        if (fid == 1){
            return orderService.list();
        }
        return orderService.lambdaQuery().eq(Orders::getFid,fid).list();
    }

//  下订单请求
    @GetMapping("/payment")
    public PayResult qrcode(@RequestParam Integer fid, @RequestParam Integer GoodsId) throws Exception {

        return orderService.payCode(GoodsId, fid);  //返回响应体
    }


//    支付宝回调通知
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        log.info("收到支付成功通知");
        String out_trade_no = request.getParameter("out_trade_no");
        log.info("流水号：{}",out_trade_no);
        orderService.isPaid(Integer.valueOf(out_trade_no));
        return "payment success";
    }

//  主动查询是否支付成功
   @GetMapping("/notify")
   public PayResult query(@RequestParam Integer outTradeNo) throws Exception {

       PayResult payResult = new PayResult();
       Orders orders = orderService.lambdaQuery().eq(Orders::getOrderId, outTradeNo)
               .eq(Orders::getPayStatus, 1).one();

       if (orders !=null){
           System.out.println("查询到已支付订单");
           payResult.setCode(200);
           payResult.setOrderId(outTradeNo);
           payResult.setOrderStatus("payment success");
           return payResult;
       }
       else if (aliPayService.isPaid(outTradeNo)){
            orderService.isPaid(outTradeNo);
            payResult.setCode(200);
            payResult.setOrderId(outTradeNo);
            payResult.setOrderStatus("payment success");
            return payResult;
        }
       payResult.setCode(201);
       payResult.setOrderId(outTradeNo);
       payResult.setOrderStatus("payment failure");
       return payResult;
   }

}
