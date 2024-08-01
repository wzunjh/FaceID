package com.face.service;

public interface AliPayService {


    String createPayQRCode(String subject, Integer outTradeNo, Double totalAmount) throws Exception;

    Boolean isPaid(Integer outTradeNo) throws Exception;

}
