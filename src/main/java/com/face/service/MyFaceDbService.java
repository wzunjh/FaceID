package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.MyFaceDb;
import com.face.bean.result.ApiResult;
import com.face.bean.result.MyFaceResult;

public interface MyFaceDbService extends IService<MyFaceDb> {

    MyFaceResult vefOne(String imageBase,String fid);

    ApiResult vefApi(String AuthToken, String imageBase);
}
