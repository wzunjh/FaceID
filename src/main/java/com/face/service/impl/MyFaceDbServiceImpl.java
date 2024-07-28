package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.ApiLog;
import com.face.bean.Face;
import com.face.bean.MyFaceDb;
import com.face.bean.result.ApiResult;
import com.face.bean.result.FaceResult;
import com.face.bean.result.MyFaceResult;
import com.face.mapper.ApiLogMapper;
import com.face.mapper.MyFaceDbMapper;
import com.face.server.FaceContrastServer;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import com.face.service.MyFaceDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MyFaceDbServiceImpl extends ServiceImpl<MyFaceDbMapper, MyFaceDb>
        implements MyFaceDbService {

    @Autowired
    FaceContrastServer faceContrastServer;

    @Autowired
    FaceService faceService;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    ApiLogMapper apiLogMapper;

    @Override
    public MyFaceResult vefOne(String imageBase, String fid) {
        List<MyFaceDb> faceList = lambdaQuery().orderByDesc(MyFaceDb::getFaceId).eq(MyFaceDb::getFid, fid).list();
        MyFaceResult myFaceResult = new MyFaceResult();
        // 判断人脸库是否为空
        if (faceList.isEmpty()) {
            return MyFaceResult.error(204, "自建人脸库为空");
        } else {
            int faceLength = faceList.size();
            for (MyFaceDb face : faceList) {
                FaceResult faceResult = faceContrastServer.faceContrastApi(face.getFaceBase(), imageBase);
                // 是否比对成功
                if (faceResult.getCode() == FaceResult.SUCCESS_CODE) {
                    // 相似度是否大于60
                    if (faceResult.getScore() > 60) {
                        // 成功
                        myFaceResult.setMsg("对比成功,人脸姓名为: "+ face.getFaceName());
                        myFaceResult.setCode(200);
                        myFaceResult.setFaceName(face.getFaceName());
                        myFaceResult.setFid(String.valueOf(face.getFid()));
                        return myFaceResult;
                    } else {

                        if (faceLength == 1) {
                            return MyFaceResult.error(400, "人脸库不存在该人脸");
                        }
                        faceLength--;
                    }
                } else {
                    // 人脸库没有检测到合适人脸
                    if (faceLength == 1) {
                        return MyFaceResult.error(400, "人脸库不存在该人脸");
                    }
                    faceLength--;
                }
            }
        }
        return MyFaceResult.error(400, "识别失败");
    }


    @Override
    public ApiResult vefApi(String AuthToken, String imageBase) {
        List<Face> faceList = faceService.lambdaQuery().list();
        ApiResult faceResult = new ApiResult();
        int faceLength = faceList.size();
        if (faceLength == 0) {
            return ApiResult.error(FaceResult.NULL_ERROR, "空指针异常"); // Use custom error handling for an empty face list
        }

        for (Face face : faceList) {
            if (face.getApiKey().equals(AuthToken)) {
                // Check if the request limit is exceeded
                if (isRequestLimitExceeded(face)) {
                    return ApiResult.error(FaceResult.NULL_ERROR, "请求次数超出限制");
                }

                MyFaceResult vef = vefOne(imageBase, String.valueOf(face.getFid()));
                ApiLog apiLog = new ApiLog();
                apiLog.setFid(face.getFid());
                apiLog.setApiTime(new Date());
                apiLog.setApiCode(vef.getCode());
                apiLog.setApiMsg(vef.getMsg());
                apiLogMapper.insert(apiLog);
                faceResult.setMsg(vef.getMsg());
                faceResult.setCode(200);
                return faceResult;
            }
        }

        // If no face matched the AuthToken
        return ApiResult.error(FaceResult.NULL_ERROR, "Auth令牌错误或失效");
    }

    private boolean isRequestLimitExceeded(Face face) {
        long currentTime = System.currentTimeMillis();
        long lastMinuteStartTime = currentTime - (currentTime % (60 * 1000));
        int requestCountInLastMinute = apiLogService.lambdaQuery()
                .eq(ApiLog::getFid, face.getFid())
                .ge(ApiLog::getApiTime, new Date(lastMinuteStartTime))
                .lt(ApiLog::getApiTime, new Date(currentTime))
                .count();
        return requestCountInLastMinute >= face.getApiMin();
    }
}
