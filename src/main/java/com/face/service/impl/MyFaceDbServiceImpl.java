package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.MyFaceDb;
import com.face.bean.result.FaceResult;
import com.face.bean.result.MyFaceResult;
import com.face.mapper.MyFaceDbMapper;
import com.face.server.FaceContrastServer;
import com.face.service.MyFaceDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyFaceDbServiceImpl extends ServiceImpl<MyFaceDbMapper, MyFaceDb>
        implements MyFaceDbService {

    @Autowired
    FaceContrastServer faceContrastServer;

    @Override
    public MyFaceResult vefOne(String imageBase, String fid) {
        List<MyFaceDb> faceList = lambdaQuery().orderByDesc(MyFaceDb::getFaceId).eq(MyFaceDb::getFid, fid).list();
        MyFaceResult myFaceResult = new MyFaceResult();
        // 如果人脸库为空,则第一次登录为录入人脸
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
                        myFaceResult.setMsg("对比成功");
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
                    // 人脸库没有检测到人脸
                    if (faceLength == 1) {
                        // 判断当前人脸是否被禁用，如被禁用，提示被禁用
                        // 禁用优先级大于 没有检测到人脸
                        return MyFaceResult.error(400, "人脸库不存在该人脸");
                    }
                    faceLength--;
                }
            }
        }
        return MyFaceResult.error(400, "识别失败");
    }
}
