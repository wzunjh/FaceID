package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.Goods;
import com.face.bean.Orders;
import com.face.bean.result.PayResult;
import com.face.service.AliPayService;
import com.face.service.GoodsService;
import com.face.service.OrderService;
import com.face.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
* @author 27877
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-07-31 15:11:43
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>
implements OrderService{

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AliPayService aliPayService;

    @Override
    public PayResult payCode(Integer GoodsId, Integer fid) throws Exception {
        Goods good = goodsService.lambdaQuery().eq(Goods::getGoodsId, GoodsId).one();
        Orders orders = new Orders();
        PayResult payResult = new PayResult();
        int randomNum = generateRandomNumber();
        orders.setOrderId(randomNum);
        orders.setOrderAmount(good.getGoodsPrice());
        orders.setOrderSubject(good.getGoodsName());
        orders.setOrderDate(new Date());
        orders.setFid(fid);
        orders.setGoodsId(GoodsId);
        orders.setPayStatus(0);
        save(orders);
        String payQRCode = aliPayService.createPayQRCode(orders.getOrderSubject(), orders.getOrderId(), orders.getOrderAmount());
        payResult.setQrBase64(payQRCode);
        payResult.setCode(200);
        payResult.setOrderId(orders.getOrderId());
        payResult.setOrderDate(orders.getOrderDate());
        return payResult;
    }

    @Override
    public void isPaid(Integer OrderId) {
        lambdaUpdate().set(Orders::getPayStatus,1).eq(Orders::getOrderId,OrderId).update();
    }

    @Override
    public PayResult payCodeAgain(Integer OrderId) throws Exception {
        Orders orders = lambdaQuery().eq(Orders::getOrderId, OrderId).one();
        String payQRCode = aliPayService.createPayQRCode(orders.getOrderSubject(), orders.getOrderId(), orders.getOrderAmount());
        PayResult payResult = new PayResult();
        payResult.setQrBase64(payQRCode);
        payResult.setCode(200);
        payResult.setOrderId(orders.getOrderId());
        payResult.setOrderDate(orders.getOrderDate());
        return payResult;
    }


    // 私有方法，生成一个10位的以9开头的数字
    private int generateRandomNumber() {
        Random random = new Random();
        // 生成8位随机数
        int randomNumber = random.nextInt(100000000); // 0到99999999
        // 拼接9开头
        String result = "9" + String.format("%08d", randomNumber);
        return Integer.parseInt(result); // 将字符串转换为int
    }

}
