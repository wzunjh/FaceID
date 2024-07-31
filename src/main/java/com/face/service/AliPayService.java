package com.face.service;

public interface AliPayService {


    String createPaymentQRCode(String subject, String outTradeNo, String totalAmount) throws Exception;


}
