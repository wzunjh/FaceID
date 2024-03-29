package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;


public interface FaceService extends IService<Face> {

    /**
     * 人脸校验
     * @param imageBase
     * @return
     */
    FaceResult vef(String imageBase);
}
