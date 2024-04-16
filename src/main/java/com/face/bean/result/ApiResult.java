package com.face.bean.result;


import lombok.Data;

@Data
public class ApiResult {
    /**
     * 返回消息
     */
    private String msg;

    /**
     * 状态码
     */
    private int code;

    /**
     * 相似度
     */
    private Float score;

    /*成功*/
    public static final Integer SUCCESS_CODE = 200;
    /*相似匹配度*/
    public static final Integer SATISFY_SCORE = 70;
    /*调用腾讯云接口发生的异常*/
    public static final Integer FACE_ERROR = -1;
    /*数据库没有该人脸*/
    public static final Integer NOT_FOUND_FACE = -2;
    /*人脸数据为空*/
    public static final Integer NULL_ERROR = -3;
    /*当前人脸被禁用*/
    public static final Integer FORBIDDEN_FACE = -4;
    /*初始化人脸*/
    public static final Integer INIT_FACE = 201;

    public ApiResult setCode(int code) {
        this.code = code;
        return this;
    }


    public ApiResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResult setScore(Float score) {
        this.score = score;
        return this;
    }

    public static ApiResult error(int code, String msg){
        return new ApiResult().setCode(code).setMsg(msg);
    }

    public static ApiResult error(int code,String msg,Float score){
        return new ApiResult().setCode(code).setMsg(msg).setScore(score);
    }

    public static ApiResult error(int code){
        return new ApiResult().setCode(code);
    }

    public static ApiResult success(){
        return new ApiResult().setCode(SUCCESS_CODE);
    }

    public static ApiResult success(String msg){
        return new ApiResult().setCode(SUCCESS_CODE).setMsg(msg);
    }

}
