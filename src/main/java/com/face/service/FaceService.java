package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.Face;
import com.face.bean.result.ApiResult;
import com.face.bean.result.FaceResult;


public interface FaceService extends IService<Face> {

    /**
     * 人脸校验
     */
    FaceResult vef(String imageBase);

    /**
     * 身份证二要素校验
     */
    FaceResult authId(Integer fid,String imageBase);

    //查验是否认证成功
    FaceResult orAuth(Integer fid);

    //查寻指定fid人物信息
    FaceResult authUser(Integer fid);

    //查询并新建ApiKey
    FaceResult apiKey(Integer fid);

    //更新ApiKey
    FaceResult updateApiKey(Integer fid);

    //人脸识别用户API
    FaceResult faceApi(String imageBase1, String imageBase2);

    // 手机号请求验证码
    FaceResult phoneSms(String phone);

    // 短信核验并绑定
    FaceResult  phoneVef(Integer fid,String phone,String code);

    // 查询手机绑定信息
    FaceResult  SmsC(Integer fid);

    //令牌一键登录
    FaceResult token(String AuthToken,String ip);

    // 查询单个照片
    FaceResult vefOne(String imageBase);

    // 远程接口调用
    ApiResult vefApi(String AuthToken,String imageBase1, String imageBase2);

    // 登录ip与IP白名单查询
    FaceResult SerIP(Integer fid);

    // IP白名单设置
    FaceResult SetIP(Integer fid,String ipList);

    // 上传人脸图片是否合格
    FaceResult isGg(String imageBase);

    //Auth 验证
    ApiResult vefAuth(String Auth, Integer fid);

    FaceResult phoneSend(String phone);

    FaceResult phoneVefLogin(String phone, String code);
}
