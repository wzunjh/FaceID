package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.MyFaceDb;
import com.face.bean.result.ApiResult;
import com.face.bean.result.MyFaceResult;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MyFaceDbService extends IService<MyFaceDb> {

    MyFaceResult vefOne(String imageBase,String fid) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    ApiResult vefApi(String AuthToken, String imageBase) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
