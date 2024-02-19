package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;

/**
* @author typsusan
* @description 针对表【face】的数据库操作Service
* @createDate 2022-07-17 03:33:50
*/
public interface FaceService extends IService<Face> {

    /**
     * 人脸校验
     * @param imageBase
     * @return
     */
    FaceResult vef(String imageBase);
}
