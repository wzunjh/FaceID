package com.face.bean.result;


import lombok.Data;

@Data
public class OtpResult {
    /**
     * 返回消息
     */
    private String otp;

    /**
     * 剩余时长
     */
    private long expirationSeconds;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 状态码
     */
    private int code;

}
