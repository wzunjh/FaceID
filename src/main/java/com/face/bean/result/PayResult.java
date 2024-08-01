package com.face.bean.result;

import lombok.Data;

import java.util.Date;

@Data
public class PayResult {

    /**
     * 订单编号
     */
    private int orderId;


//  支付二维码图像
    private String qrBase64;

//  订单创建时间
    private Date orderDate;

//  支付状态
    private String orderStatus;

    /**
     * 状态码
     */
    private int code;

}
