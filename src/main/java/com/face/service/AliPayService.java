package com.face.service;

public interface AliPayService {


    String createPayQRCode(String subject, String outTradeNo, String totalAmount) throws Exception;


}
