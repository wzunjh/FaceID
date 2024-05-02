package com.face.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.annotation.FaceLog;
import com.face.bean.Face;
import com.face.bean.result.FaceResult;
import com.face.service.FaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;


@RestController
@RequestMapping("/face")
@Api("人脸验证接口")
@Slf4j
public class FaceController {

    @Autowired
    FaceService faceService;

    @PostMapping("/vef")
    @ApiOperation(value="人脸验证", notes="根据传入的base64编码和数据的base64编码进行对比")
    @FaceLog
    public FaceResult faceVef(@RequestBody String imageBase){
        return faceService.vef(imageBase);
    }

    @PostMapping("/vefOne")
    @ApiOperation(value="人脸API", notes="根据传入的base64编码和数据的base64编码进行对比")
    @FaceLog
    public FaceResult faceVefOne(@RequestBody String imageBase){
        return faceService.vefOne(imageBase);
    }


    @GetMapping("/faceList")
    @ApiOperation(value = "人脸列表",notes = "查询所有的人脸信息")
    public FaceResult faceList(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam(required = false) String faceName,
            @RequestParam(required = false) String faceStatus
    ){
        IPage<Face> page = faceService.page(new Page(current, size), new QueryWrapper<Face>()
                .like(StrUtil.isNotBlank(faceName),"face_name",faceName)
                .like(StrUtil.isNotBlank(faceStatus),"face_status",faceStatus));
        return FaceResult.success(page);
    }


    @GetMapping("/faceDelete/{fid}")
    @ApiOperation(value = "删除人脸",notes = "根据id进行删除")
    public FaceResult faceDelete(@PathVariable Integer fid){
        faceService.removeById(fid);
        return FaceResult.success("删除成功");
    }

    @GetMapping("/info/{fid}")
    @ApiOperation(value = "查询方法",notes = "根据id查询人脸信息")
    public FaceResult info(@PathVariable Integer fid){
        return FaceResult.success(faceService.getById(fid));
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加")
    public FaceResult save(@RequestBody Face face){
        FaceResult vef = faceService.isGg(face.getFaceBase());
        if (vef.getCode() == 200){
            face.setVefNum(0);
            face.setCreateTime(new Date());
            faceService.save(face);
            return FaceResult.success("添加成功");
        }

        return FaceResult.error(400,"图片不合格");
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public FaceResult update(@RequestBody Face face){
        FaceResult vef = faceService.isGg(face.getFaceBase());
        if (vef.getCode() == 200){
            faceService.updateById(face);
            return FaceResult.success("修改成功");
        }
        return FaceResult.error(400,"图片不合格");
    }

    @PostMapping("/auth")
    @ApiOperation(value = "身份证认证", notes = "根据id和身份证图片进行认证")
    public FaceResult authenticate(@RequestParam Integer fid, @RequestParam(required = false) String imageBase) {
        // 调用接口实现身份验证逻辑
        return faceService.authId(fid, imageBase);
    }

    @GetMapping("/orAuth/{fid}")
    @ApiOperation(value = "查询是否认证过",notes = "根据id进行查询")
    public FaceResult orAuth(@PathVariable Integer fid){
        return faceService.orAuth(fid);
    }


    @GetMapping("/authUser/{fid}")
    @ApiOperation(value = "查询认证信息",notes = "根据id进行查询")
    public FaceResult authUser(@PathVariable Integer fid){
        return faceService.authUser(fid);
    }

    @GetMapping("/apiKey/{fid}")
    @ApiOperation(value = "查询apikey",notes = "根据id进行查询")
    public FaceResult apiKey(@PathVariable Integer fid){
        return faceService.apiKey(fid);
    }

    @GetMapping("/updateApiKey/{fid}")
    @ApiOperation(value = "更新ApiKey信息",notes = "根据id进行更新")
    public FaceResult UpdateApiKey(@PathVariable Integer fid){
        return faceService.updateApiKey(fid);
    }

    @PostMapping("/api")
    @ApiOperation(value="人脸验证接口", notes="根据传入的两个base64编码进行对比")
    public FaceResult faceApi(@RequestParam(required = false) String imageBase1, @RequestParam(required = false) String imageBase2) {
        FaceResult faceResult = new FaceResult();
        if(imageBase1 == null || imageBase1.isEmpty() ||imageBase2 == null || imageBase2.isEmpty() ) {
            faceResult.setMsg("两张图片都不能为空");
            faceResult.setCode(400); // Bad Request
            return faceResult;
        }

        return faceService.faceApi(imageBase1, imageBase2);
    }

    @GetMapping("/Sms")
    @ApiOperation(value = "手机短信验证", notes = "根据手机号码进行认证")
    public FaceResult phoneSms( @RequestParam String phone) {
        // 调用接口实现身份验证逻辑
        return faceService.phoneSms(phone);
    }

    @GetMapping("/SmsVef")
    @ApiOperation(value = "手机核验绑定", notes = "根据验证码进行认证")
    public FaceResult phoneVef(@RequestParam Integer fid, @RequestParam String phone,@RequestParam String code) {
        // 调用接口实现身份验证逻辑
        return faceService.phoneVef(fid, phone, code);
    }

    @GetMapping("/SmsC/{fid}")
    @ApiOperation(value = "查询绑定信息",notes = "根据id进行查询")
    public FaceResult SmsC(@PathVariable Integer fid){
        return faceService.SmsC(fid);
    }

    @GetMapping("/vef")
    @ApiOperation(value = "Auth认证登录", notes = "根据Auth进行认证登录")
    @FaceLog
    public FaceResult token(@RequestParam String AuthToken,@RequestParam String ip) {
        // 调用接口实现身份验证逻辑
        return faceService.token(AuthToken,ip);
    }

    @GetMapping("/ip/{fid}")
    @ApiOperation(value = "查询ip信息",notes = "根据id进行查询")
    public FaceResult SerIP(@PathVariable Integer fid){
        return faceService.SerIP(fid);
    }

    @GetMapping("/SetIP")
    @ApiOperation(value = "IP白名单", notes = "根据id进行查询更新")
    public FaceResult SetIP(@RequestParam Integer fid, @RequestParam String ipList) {
        // 调用接口实现身份验证逻辑
        return faceService.SetIP(fid,ipList);
    }

}
