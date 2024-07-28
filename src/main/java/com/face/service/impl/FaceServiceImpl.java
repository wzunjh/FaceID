package com.face.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.ApiLog;
import com.face.bean.Face;
import com.face.bean.result.ApiResult;
import com.face.bean.result.FaceResult;
import com.face.mapper.FaceMapper;
import com.face.mapper.ApiLogMapper;
import com.face.server.FaceContrastServer;
import com.face.server.IdAuthenticationServer;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import com.face.utils.*;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class FaceServiceImpl extends ServiceImpl<FaceMapper, Face>
    implements FaceService {

    @Autowired
    private ApiLogMapper apiLogMapper;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    FaceContrastServer faceContrastServer;

    @Autowired
    private IdAuthenticationServer idAuthenticationServer;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MinioClient minioClient;

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
                            try {
                                return faceState != null?faceState:RegFace(imageBase);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
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

    @Override
    public FaceResult vefOne(String imageBase) {
        imageBase = JSONUtil.parseObj(imageBase).getStr("imageBase");
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
        FaceResult faceState = null;
        // 如果人脸库为空,则第一次登录为录入人脸
        if (faceList.isEmpty()){
            return initFace(imageBase);
        }else {
            int faceLength = faceList.size();
            for (Face face : faceList) {
                FaceResult faceResult = faceContrastServer.faceContrastApi(face.getFaceBase(), imageBase);
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
        if(isGg(imageBase).getCode() != 200){
            faceResult.setCode(400);
            faceResult.setMsg("人脸信息错误");
            return faceResult;
        }
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

    public FaceResult RegFace(String imageBase) throws Exception{
        FaceResult faceResult = new FaceResult();
        Face face = new Face();
        face.setFaceBase(imageBase);
        face.setCreateTime(new Date());
        face.setVefNum(0);
        face.setFaceStatus(0);
        boolean save = save(face);
        faceResult.setCode(FaceResult.INIT_FACE);
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("facedb-"+face.getFid()).build());
        faceResult.setMsg("人脸初始化"+(save?"成功":"失败")+","+(save?"请验证登录":"请稍后再试"));
        return faceResult;
    }


    @Override
    public FaceResult authId(Integer fid, String imageBase) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (face == null) {
            faceResult.setCode(404); // 用户不存在
            faceResult.setMsg("用户不存在,请重新输入");
            return faceResult;
        } else if (face.getFaceName() == null || face.getFaceName().isEmpty()) {
            faceResult.setCode(400); // 用户姓名为空
            faceResult.setMsg("用户姓名为空,请重新输入");
            return faceResult;
        } else if (face.getId2Status() !=null && face.getId2Status().equals("1")) {
            faceResult.setCode(202); // 已认证成功
            faceResult.setMsg("该用户已认证成功");
            return faceResult;
        } else if (face.getPhone() == null || face.getPhone().isEmpty()){
            faceResult.setCode(208);
            faceResult.setMsg("请先完成手机号核验绑定");
            return faceResult;
        }
        faceResult = faceContrastServer.faceContrastApi(face.getFaceBase(),imageBase);
        if (faceResult.getScore()>=FaceResult.SATISFY_SCORE){

            faceResult = faceContrastServer.idVerification(imageBase);
            if (faceResult.getCode() != 200 || ! faceResult.getName().equals(face.getFaceName())){
                faceResult.setMsg("身份证识别失败,请重新上传");
                faceResult.setCode(401);
                return faceResult;
            }
            FaceResult faceResult2 = idAuthenticationServer.authenticateId(faceResult.getName(), faceResult.getIdNo());

            if (faceResult2.getCode() == 200) {
                // 认证成功
                face.setIdNo(faceResult.getIdNo());
                face.setId2Status("1");
                face.setCity(faceResult2.getCity());
                updateById(face);
                SmsUtils.id2Auth(face.getPhone());
                faceResult.setCode(200); // 认证成功
                faceResult.setMsg("认证成功");
            } else {
                faceResult.setCode(401); // 信息不匹配
                faceResult.setMsg("信息不匹配,认证失败");
            }
            return faceResult;

        }
        faceResult.setMsg("人脸信息对比失败");
        faceResult.setCode(401);
        return faceResult;
    }


    @Override
    public FaceResult orAuth(Integer fid) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (face.getId2Status() == null || face.getId2Status().isEmpty()) {
            faceResult.setCode(208);
            faceResult.setMsg("未认证");
        }else if (face.getId2Status().equals("1")) {
            faceResult.setCode(200);
            faceResult.setMsg("已认证");
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
            faceResult.setApiKey("Auth-************************");
            faceResult.setApiNum(face.getApiNum());
            faceResult.setApiTime(face.getApiTime());
            faceResult.setApiMin(face.getApiMin());
            faceResult.setCode(200);
        }

           return faceResult;

    }

    @Override
    public FaceResult updateApiKey(Integer fid){
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        // Check if the API key was updated within the last minute
        if (face.getApiTime() != null && new Date().getTime() - face.getApiTime().getTime() < 60000) {
            faceResult.setCode(429); // HTTP status code for Too Many Requests
            faceResult.setMsg("API Key 更新太频繁，请等待一分钟");
            return faceResult;
        }

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
            faceResult.setMsg("API Key 创建成功: 为了确保信息安全,ApiKey只显示一次");
            faceResult.setCode(200);
            faceResult.setApiKey(generatedKey);
            faceResult.setApiNum(face.getApiNum());
            faceResult.setApiTime(face.getApiTime());
        } else {

            String generatedKey = getString();
            // 这里可以将生成的密钥设置给 face 对象的 apiKey 属性
            face.setApiKey(generatedKey);
            face.setApiTime(new Date());
            SmsUtils.ApiKey(face.getPhone());
            updateById(face);
            Face faceNew = lambdaQuery().eq(Face::getFid, fid).one();
            faceResult.setMsg("API Key 更新成功: 为了确保信息安全,ApiKey只显示一次");
            faceResult.setApiNum(faceNew.getApiNum());
            faceResult.setApiTime(faceNew.getApiTime());
            faceResult.setCode(200);
            faceResult.setApiKey(generatedKey);
        }

        return faceResult;

    }

    @Override
    public FaceResult faceApi(String imageBase1, String imageBase2) {

            FaceResult faceResult = faceContrastServer.faceContrastApi(imageBase1, imageBase2);
            if (faceResult.getCode() == FaceResult.SUCCESS_CODE) {
                if (faceResult.getScore() > FaceResult.SATISFY_SCORE) {
                    // 相似度大于70%，认为是同一个人
                    faceResult.setMsg("匹配成功,相似度为:" + faceResult.getScore()+" 性别:"+ getSex(faceResult.getSex1())+" 图一年龄:" + faceResult.getAge1() + " 图二年龄:" + faceResult.getAge2());
                    faceResult.setCode(200);
                } else {
                    // 相似度小于70%，认为不是同一个人
                    faceResult.setMsg("匹配失败,相似度为:" + faceResult.getScore()+" 图一性别:"+ getSex(faceResult.getSex1())+" 图二性别:"+getSex(faceResult.getSex2())+" 图一年龄:" + faceResult.getAge1() + " 图二年龄:" + faceResult.getAge2());
                    faceResult.setCode(200);
                }
            }
            return faceResult;
        }

    private static @NotNull String getSex(String sex){
        if(Objects.equals(sex, "M")){
            return "男性";
        }
        return "女性";
    }



    @Override
    public FaceResult phoneSms(String phone){
        FaceResult faceResult = new FaceResult();

        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            faceResult.setCode(400);
            faceResult.setMsg("手机号码格式错误");
        }

        String code = RandomUtil.randomNumbers(6);  //六位随机验证码

        stringRedisTemplate.opsForValue().set(phone,code,5L, TimeUnit.MINUTES);  //将验证码存入redis，5分钟有效
        SmsUtils.sendSms(phone,code);   //发送验证码
        System.out.println(code);
        faceResult.setMsg("短信发送成功");
        faceResult.setCode(200);
        return faceResult;

    }

    @Override
    public FaceResult phoneVef(Integer fid,String phone,String code){
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        String codeVef = stringRedisTemplate.opsForValue().get(phone);  //从redis中取出验证码

        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            faceResult.setCode(400);
            faceResult.setMsg("手机号码格式错误");
            return faceResult;
        } else if (code==null){
            faceResult.setCode(400);
            faceResult.setMsg("请先获取验证码");
            return faceResult;
        } else if (Objects.equals(code, codeVef)){
            face.setPhone(phone);
            updateById(face);
            stringRedisTemplate.delete(phone);
            faceResult.setCode(200);
            faceResult.setMsg("手机号绑定成功");
            return faceResult;
        }
        faceResult.setMsg("验证码错误");
        faceResult.setCode(208);
        return faceResult;

    }


    @Override
    public FaceResult SmsC(Integer fid){
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (face.getPhone() == null || face.getPhone().isEmpty()){
            faceResult.setCode(208);
            faceResult.setMsg("未绑定手机号");
        }else {
            faceResult.setPhone(face.getPhone());
            faceResult.setFid(String.valueOf(fid));
            faceResult.setCode(200);
            faceResult.setMsg("绑定成功");
        }

        return faceResult;

    }

    @Override
    public FaceResult token(String AuthToken,String ip) {
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
        FaceResult faceResult = new FaceResult();

        if (AuthToken.isEmpty()){
            return faceResult.setMsg("令牌不能为空");
        } else {
            int faceLength = faceList.size();
            if (faceLength == 0) {
                return FaceResult.error(FaceResult.NULL_ERROR, "空指针异常"); // Use custom error handling for an empty face list
            }

            for (Face face : faceList) {
                if (face.getApiKey() == null || face.getApiKey().isEmpty()) {
                    continue; // 跳过当前 face，进入下一个循环
                }
                if (face.getApiKey().equals(AuthToken)) {

                    if (!face.getIpList().isEmpty() && !IPValidator.isIPInList(ip, face.getIpList())){
                        faceResult.setCode(400);
                        faceResult.setMsg("非法登录IP,请检查白名单");
                        return faceResult;
                    }
                    // 成功并记录登录ip记录
                    lambdaUpdate().set(Face::getVefNum,face.getVefNum()+1).set(Face::getApiTime,new Date()).set(Face::getIp,ip).eq(Face::getFid,face.getFid()).update();
                    faceResult.setMsg(TimeUtils.timeQuantum()+"好,"+face.getFaceName());
                    faceResult.setName(face.getFaceName());
                    faceResult.setFid(String.valueOf(face.getFid()));
                    faceResult.setScore(Float.valueOf("100"));
                    faceResult.setCode(200);
                    Map<String,String> map = new HashMap<>();
                    map.put("score","100");
                    map.put("faceName",faceResult.getName());
                    faceResult.setToken(JwtUtils.genereteToken(map));
                    return faceResult;
                }
            }
            // If no face matched the AuthToken
            return FaceResult.error(FaceResult.NULL_ERROR, "未找到匹配的Auth令牌");
        }
    }


    @Override
    public ApiResult vefApi(String AuthToken, String imageBase1, String imageBase2) {
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getApiNum).list();
        ApiResult faceResult = new ApiResult();
        FaceResult vef;
        int faceLength = faceList.size();
        if (faceLength == 0) {
            return ApiResult.error(FaceResult.NULL_ERROR, "空指针异常"); // Use custom error handling for an empty face list
        }

        for (Face face : faceList) {
            if (face.getApiKey() == null || face.getApiKey().isEmpty()) {
                continue; // 跳过当前 face，进入下一个循环
            }
            if (face.getApiKey().equals(AuthToken)) {
                // Check if the request limit is exceeded
                if (isRequestLimitExceeded(face)) {
                    return ApiResult.error(FaceResult.NULL_ERROR, "请求次数超出限制");
                }

                // 成功
                lambdaUpdate().set(Face::getApiNum, face.getApiNum() + 1).set(Face::getApiTime, new Date()).eq(Face::getFid, face.getFid()).update();
                vef = faceApi(imageBase1, imageBase2);
                ApiLog apiLog = new ApiLog();
                apiLog.setFid(face.getFid());
                apiLog.setApiTime(new Date());
                apiLog.setApiCode(vef.getCode());
                apiLog.setApiMsg(vef.getMsg());
                apiLogMapper.insert(apiLog);
                faceResult.setScore(vef.getScore());
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
    @Override
    public FaceResult SerIP(Integer fid) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        faceResult.setMsg("查询成功");
        faceResult.setCode(200);
        faceResult.setIp(face.getIp());
        faceResult.setIpList(face.getIpList());
        if (face.getIpList() == null || face.getIpList().isEmpty()) {
            faceResult.setApiNum(10086);
        }
        return faceResult;
    }

    @Override
    public FaceResult SetIP(Integer fid, String ipList) {
        FaceResult faceResult = new FaceResult();
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (IPValidator.isValidIPList(ipList)){
            face.setIpList(ipList);
            updateById(face);
            faceResult.setCode(200);
            faceResult.setMsg("IP白名单更新成功");
            faceResult.setIpList(face.getIpList());
            faceResult.setIp(face.getIp());
            faceResult.setFid(String.valueOf(face.getFid()));
            return faceResult;

        }
        faceResult.setMsg("IP格式错误");
        faceResult.setCode(400);
        return faceResult;
    }

    @Override
    public FaceResult isGg(String imageBase) {
        Face face = lambdaQuery().eq(Face::getFid, 1).one();
        FaceResult vef = faceApi(imageBase,face.getFaceBase());
        if (vef.getCode() == 200){
            vef.setMsg("图片合格");
            return vef;
        }
        vef.setMsg("图片不合格");
        vef.setCode(400);
        return vef;
    }


    //随机生成密钥算法
    private static @NotNull String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Auth-");

        Random random = new Random();
        for (int i = 0; i < 30; i++) {
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

    @Override
    public ApiResult vefAuth(String Auth, Integer fid) {

        ApiResult apiResult = new ApiResult();
        if (Auth.isEmpty()){
            apiResult.setMsg("令牌不能为空");
            apiResult.setCode(400);
            return apiResult;
        }
        Face face = lambdaQuery().eq(Face::getFid, fid).one();
        if (Auth.equals(face.getApiKey())){
            apiResult.setCode(200);
            apiResult.setMsg("验证成功");
            return apiResult;
        }
        apiResult.setMsg("令牌不匹配,验证失败");
        apiResult.setCode(400);
        return apiResult;
    }


    @Override
    public FaceResult phoneSend(String phone) {
        List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
        FaceResult faceResult = new FaceResult();

        if (phone.isEmpty()) {
            return faceResult.setMsg("手机号不能为空");
        } else {
            int faceLength = faceList.size();
            if (faceLength == 0) {
                return FaceResult.error(FaceResult.NULL_ERROR, "空指针异常"); // Use custom error handling for an empty face list
            }

            for (Face face : faceList) {
                if (face.getPhone() == null || face.getPhone().isEmpty()) {
                    continue; // 跳过当前 face，进入下一个循环
                }
                if (face.getPhone().equals(phone)) {
                    //校验手机号
                    if (RegexUtils.isPhoneInvalid(phone)) {
                        faceResult.setCode(400);
                        faceResult.setMsg("手机号码格式错误");
                        return faceResult;
                    }

                    String code = RandomUtil.randomNumbers(6);  //六位随机验证码
                    stringRedisTemplate.opsForValue().set(phone, code, 5L, TimeUnit.MINUTES);  //将验证码存入redis，5分钟有效
                    SmsUtils.sendSms(phone, code);   //发送验证码
                    System.out.println(code);
                    faceResult.setMsg("短信发送成功");
                    faceResult.setCode(200);
                    return faceResult;
                }
            }
            // If no face matched the AuthToken
            return FaceResult.error(FaceResult.NULL_ERROR, "手机号未绑定账号");
        }
    }

    @Override
    public FaceResult phoneVefLogin(String phone, String code) {
        FaceResult faceResult = new FaceResult();
        String codeVef = stringRedisTemplate.opsForValue().get(phone);  //从redis中取出验证码

        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            faceResult.setCode(400);
            faceResult.setMsg("手机号码格式错误");
            return faceResult;
        } else if (code == null) {
            faceResult.setCode(400);
            faceResult.setMsg("请先获取验证码");
            return faceResult;
        } else if (Objects.equals(code, codeVef)) {
            List<Face> faceList = lambdaQuery().orderByDesc(Face::getVefNum).list();
            int faceLength = faceList.size();
            if (faceLength == 0) {
                return FaceResult.error(FaceResult.NULL_ERROR, "空指针异常"); // Use custom error handling for an empty face list
            }

            for (Face face : faceList) {
                if (face.getPhone() == null || face.getPhone().isEmpty()) {
                    continue; // 跳过当前 face，进入下一个循环
                }
                if (face.getPhone().equals(phone)) {
                    stringRedisTemplate.delete(phone);
                    faceResult.setMsg(TimeUtils.timeQuantum() + "好," + face.getFaceName());
                    faceResult.setName(face.getFaceName());
                    faceResult.setFid(String.valueOf(face.getFid()));
                    faceResult.setScore(Float.valueOf("100"));
                    faceResult.setCode(200);
                    Map<String, String> map = new HashMap<>();
                    map.put("score", "100");
                    map.put("faceName", faceResult.getName());
                    faceResult.setToken(JwtUtils.genereteToken(map));
                    faceResult.setCode(200);
                    faceResult.setMsg("手机号登录成功");
                    return faceResult;

                }
            }
        }
        faceResult.setMsg("验证码错误");
        faceResult.setCode(208);
        return faceResult;
    }

}




