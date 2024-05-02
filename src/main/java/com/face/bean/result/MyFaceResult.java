package com.face.bean.result;

import lombok.Data;

@Data
public class MyFaceResult {
    /**
     * 返回消息
     */
    private String msg;

    /**
     * 图片数据 base_64编码
     */
    private String faceBase;

    /**
     * 状态码
     */
    private int code;

    /**
     * 人脸姓名
     */
    private String faceName;

    /**
     * 用户主键
     */
    private String fid;

    /**
     * 人脸主键
     */
    private String faceId;

    /**
     * 数据集
     */
    private Object data;


    public MyFaceResult setCode(int code) {
        this.code = code;
        return this;
    }

    public MyFaceResult setData(Object data) {
        this.data = data;
        return this;
    }

    public MyFaceResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }


    public static MyFaceResult error(int code, String msg){
        return new MyFaceResult().setCode(code).setMsg(msg);
    }


    public static MyFaceResult success(){
        return new MyFaceResult().setCode(200);
    }

    public static MyFaceResult success(Object data){
        return new MyFaceResult().setCode(200).setData(data);
    }

    public static MyFaceResult success(String msg){
        return new MyFaceResult().setCode(200).setMsg(msg);
    }

}
