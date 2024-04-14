package com.face.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;
import com.face.mapper.FaceMapper;
import com.face.server.FaceContrastServer;
import com.face.server.IdAuthenticationServer;
import com.face.service.FaceService;
import com.face.utils.JwtUtils;
import com.face.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class FaceServiceImpl extends ServiceImpl<FaceMapper, Face>
    implements FaceService {

    @Autowired
    FaceContrastServer faceContrastServer;

    @Autowired
    private IdAuthenticationServer idAuthenticationServer;

    @Override
    public FaceResult vef(String imageBase) {
        imageBase = JSONUtil.parseObj(imageBase).getStr("imageBase");
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
        FaceResult faceState = null;
        // 如果人脸库为空,则第一次登录为录入人脸
        if (faceList.isEmpty()){
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
                            faceResult.setFid(String.valueOf(face.getFid()));
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


    @Override
    public FaceResult authId(Integer fid, String idNo) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (face == null) {
            faceResult.setCode(404); // 用户不存在
            faceResult.setMsg("用户不存在,请重新输入");
        } else if (face.getFaceName() == null || face.getFaceName().isEmpty()) {
            faceResult.setCode(400); // 用户姓名为空
            faceResult.setMsg("用户姓名为空,请重新输入");
        } else if (face.getId2Status() !=null && face.getId2Status().equals("1")) {
            faceResult.setCode(202); // 已认证成功
            faceResult.setMsg("该用户已认证成功");
        } else if (idNo.length() != 18) {
            faceResult.setCode(400); // 身份证号码格式错误
            faceResult.setMsg("身份证号码格式错误");
        } else {
            faceResult = idAuthenticationServer.authenticateId(face.getFaceName(), idNo);

            if (faceResult.getCode() == 0) {
                // 认证成功
                face.setIdNo(idNo);
                face.setId2Status("1");
                face.setCity(faceResult.getCity());
                updateById(face);
                faceResult.setCode(200); // 认证成功
                faceResult.setMsg("认证成功");
            } else {
                faceResult.setCode(401); // 信息不匹配
                faceResult.setMsg("信息不匹配,认证失败");
            }
        }

        return faceResult;
    }


    @Override
    public FaceResult orAuth(Integer fid) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (face.getId2Status().equals("1")) {
            faceResult.setCode(200);
            faceResult.setMsg("已认证");
        }else {
            faceResult.setCode(208);
            faceResult.setMsg("未认证");
        }
        return faceResult;
    }

    @Override
    public FaceResult authUser(Integer fid) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        faceResult.setFid(String.valueOf(face.getFid()));
        faceResult.setName(face.getFaceName());
        faceResult.setIdNo(face.getIdNo());
        faceResult.setCity(face.getCity());
        faceResult.setCode(200);
        return faceResult;
    }

    @Override
    public FaceResult apiKey(Integer fid){
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if(face.getId2Status() == null || face.getId2Status().isEmpty()){
          faceResult.setCode(202);
          faceResult.setMsg("请先完成身份证核验");
        } else if(face.getApiKey() == null || face.getApiKey().isEmpty()) {
            faceResult.setCode(202);
            faceResult.setMsg("未查询到,请新建或更新 ApiKey");
        }
        else {
            faceResult.setApiKey(face.getApiKey());
            faceResult.setApiNum(face.getApiNum());
            faceResult.setApiTime(face.getApiTime());
            faceResult.setCode(200);
        }

           return faceResult;

    }

    @Override
    public FaceResult updateApiKey(Integer fid){
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if(face.getId2Status() == null || face.getId2Status().isEmpty()){
            faceResult.setCode(202);
            faceResult.setMsg("请先完成身份证核验");
        } else if (face.getApiKey() == null || face.getApiKey().isEmpty()) {

            String generatedKey = getString();
            // 这里可以将生成的密钥设置给 face 对象的 apiKey 属性
            face.setApiKey(generatedKey);
            face.setApiNum(0);
            face.setApiTime(new Date());
            updateById(face);
            faceResult.setMsg("API Key 创建成功");
            faceResult.setCode(200);
            faceResult.setApiKey(generatedKey);
            faceResult.setApiNum(face.getApiNum());
            faceResult.setApiTime(face.getApiTime());
        } else {

            String generatedKey = getString();
            // 这里可以将生成的密钥设置给 face 对象的 apiKey 属性
            face.setApiKey(generatedKey);
            updateById(face);
            faceResult.setMsg("API Key 更新成功");
            faceResult.setApiNum(face.getApiNum());
            faceResult.setApiTime(face.getApiTime());
            faceResult.setCode(200);
            faceResult.setApiKey(generatedKey);
        }

        return faceResult;

    }

    @Override
    public FaceResult faceApi(String imageBase1, String imageBase2) {

            FaceResult faceResult = faceContrastServer.faceContrast(imageBase1, imageBase2);
            if (faceResult.getCode() == FaceResult.SUCCESS_CODE) {
                if (faceResult.getScore() > FaceResult.SATISFY_SCORE) {
                    // 相似度大于70%，认为是同一个人
                    faceResult.setMsg("两张人脸匹配成功，相似度为：" + faceResult.getScore());
                } else {
                    // 相似度小于70%，认为不是同一个人
                    faceResult.setMsg("两张人脸匹配失败，相似度为：" + faceResult.getScore());
                }
            }
            return faceResult;
        }




    //随机生成密钥算法
    private static @NotNull String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sk-");

        Random random = new Random();
        for (int i = 1; i < 36; i++) {
            int type = random.nextInt(3); // 0 for digit, 1 for uppercase letter, 2 for lowercase letter

            switch (type) {
                case 0:
                    sb.append(random.nextInt(10)); // digit
                    break;
                case 1:
                    sb.append((char) (random.nextInt(26) + 'A')); // uppercase letter
                    break;
                case 2:
                    sb.append((char) (random.nextInt(26) + 'a')); // lowercase letter
                    break;
            }
        }

        return sb.toString();
    }

}




