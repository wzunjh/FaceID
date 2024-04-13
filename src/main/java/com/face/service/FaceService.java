package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;


public interface FaceService extends IService<Face> {

    /**
     * 人脸校验
     */
    FaceResult vef(String imageBase);

    /**
     * 身份证二要素校验
     */
    FaceResult authId(Integer fid,String idNo);

    //查验是否认证成功
    FaceResult orAuth(Integer fid);

    //查寻指定fid人物信息
    FaceResult authUser(Integer fid);

    //查询并新建ApiKey
    FaceResult apiKey(Integer fid);

}
