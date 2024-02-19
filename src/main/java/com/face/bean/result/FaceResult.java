package com.face.bean.result;

import com.face.bean.Face;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author tanyongpeng
 * <p>des</p>
 **/
@Data
public class FaceResult {

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 状态码
     */
    private int code;

    /**
     * 验证人姓名
     */
    private String name;

    /**
     * 返回token
     */
    private String token;

    /**
     * 数据集
     */
    private Object data;

    /**
     * 相似度
     */
    private Float score;

    /*成功*/
    public static final Integer SUCCESS_CODE = 200;
    /*相似匹配度*/
    public static final Integer SATISFY_SCORE = 80;
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

    public FaceResult setCode(int code) {
        this.code = code;
        return this;
    }

    public FaceResult setData(Object data) {
        this.data = data;
        return this;
    }

    public FaceResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public FaceResult setScore(Float score) {
        this.score = score;
        return this;
    }

    public static FaceResult error(int code, String msg){
        return new FaceResult().setCode(code).setMsg(msg);
    }

    public static FaceResult error(int code,String msg,Float score){
        return new FaceResult().setCode(code).setMsg(msg).setScore(score);
    }

    public static FaceResult error(int code){
        return new FaceResult().setCode(code);
    }

    public static FaceResult success(){
        return new FaceResult().setCode(SUCCESS_CODE);
    }
    public static FaceResult success(Object data){
        return new FaceResult().setCode(SUCCESS_CODE).setData(data);
    }
    public static FaceResult success(String msg){
        return new FaceResult().setCode(SUCCESS_CODE).setMsg(msg);
    }
}
