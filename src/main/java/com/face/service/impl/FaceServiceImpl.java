package com.face.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.face.bean.Face;
import com.face.bean.result.FaceResult;
import com.face.mapper.FaceMapper;
import com.face.server.FaceContrastServer;
import com.face.service.FaceService;
import com.face.utils.JwtUtils;
import com.face.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author typsusan
* @description 针对表【face】的数据库操作Service实现
* @createDate 2022-07-17 03:33:50
*/
@Service
public class FaceServiceImpl extends ServiceImpl<FaceMapper, Face>
    implements FaceService {

    @Autowired
    FaceContrastServer faceContrastServer;

    @Override
    public FaceResult vef(String imageBase) {
        imageBase = JSONUtil.parseObj(imageBase).getStr("imageBase");
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
        FaceResult faceState = null;
        // 如果人脸库为空,则第一次登录为录入人脸
        if (faceList.size() == 0){
            return initFace(imageBase);
        }else {
            int faceLength = faceList.size();
            for (Face face : faceList) {
                FaceResult faceResult = faceContrastServer.faceContrast(face.getFaceBase(), imageBase);
                // 是否比对成功
                if (faceResult.getCode() == FaceResult.SUCCESS_CODE ){
                    // 相似度是否大于80
                    if (faceResult.getScore() > FaceResult.SATISFY_SCORE){
                        if (face.getFaceStatus() == 0){
                            // 成功
                            lambdaUpdate().set(Face::getVefNum,face.getVefNum()+1).eq(Face::getFid,face.getFid()).update();
                            faceResult.setMsg(TimeUtils.timeQuantum()+"好,"+face.getFaceName());
                            faceResult.setName(face.getFaceName());
                            Map<String,String> map = new HashMap<>();
                            map.put("score",String.valueOf(faceResult.getScore()));
                            map.put("faceName",faceResult.getName());
                            faceResult.setToken(JwtUtils.genereteToken(map));
                            return faceResult;
                        }else {
                            // 失败 人脸被禁用
                            lambdaUpdate().set(Face::getVefNum,face.getVefNum()+1).eq(Face::getFid,face.getFid()).update();
                            faceResult.setMsg(face.getFaceName()+",当前人脸被禁用");
                            faceResult.setName(face.getFaceName());
                            faceResult.setCode(FaceResult.FORBIDDEN_FACE);
                            faceState = faceResult;
                            // 就算上一张人脸被禁用还得往下执行
                            // 可能当前用户存在多张人脸，
                            if (faceLength == 1){
                                return faceResult;
                            }
                            faceLength --;
                        }
                    }else {
                        // 人脸库没有检测到人脸
                        if (faceLength == 1){
                            // 判断当前人脸是否被禁用，如被禁用，提示被禁用
                            // 禁用优先级大于 没有检测到人脸
                            return faceState != null?faceState:FaceResult.error(FaceResult.NOT_FOUND_FACE,"人脸库不存在该人脸",faceResult.getScore());
                        }
                        faceLength --;
                    }
                }else {
                    // 接口返回异常
                    return faceResult;
                }
            }
        }
        // 空异常
        return FaceResult.error(FaceResult.NULL_ERROR,"空指针异常");
    }


    public FaceResult initFace(String imageBase){
        FaceResult faceResult = new FaceResult();
        Face face = new Face();
        face.setFaceBase(imageBase);
        face.setCreateTime(new Date());
        face.setVefNum(0);
        face.setFaceName("admin");
        face.setFaceStatus(0);
        boolean save = save(face);
        faceResult.setCode(FaceResult.INIT_FACE);
        faceResult.setMsg("人脸初始化"+(save?"成功":"失败")+","+(save?"请验证登录":"请稍后再试"));
        faceResult.setName(face.getFaceName());
        return faceResult;
    }

}




