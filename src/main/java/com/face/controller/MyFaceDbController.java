package com.face.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.bean.MyFaceDb;
import com.face.bean.result.FaceResult;
import com.face.bean.result.MyFaceResult;
import com.face.service.FaceService;
import com.face.service.MyFaceDbService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@RestController
@RequestMapping("/mydb")
@Api("用户自建人脸库")
@Slf4j
public class MyFaceDbController {

    @Autowired
    MyFaceDbService myFaceDbService;

    @Autowired
    FaceService faceService;

    @Resource
    private MinioClient minioClient;

    @GetMapping("/faceList")
    @ApiOperation(value = "人脸列表",notes = "查询所有的人脸信息")
    public MyFaceResult faceList(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam Integer fid,
            @RequestParam(required = false) String faceName
    ){
        IPage<MyFaceDb> page = myFaceDbService.page(new Page(current, size), new QueryWrapper<MyFaceDb>()
                .like(StrUtil.isNotBlank(faceName),"face_name",faceName)
                .eq("fid",fid));
        return MyFaceResult.success(page);
    }


    @GetMapping("/faceDelete/{faceid}")
    @ApiOperation(value = "删除人脸",notes = "根据faceid进行删除")
    public MyFaceResult faceDelete(@PathVariable Integer faceid) throws Exception{
        MyFaceDb face = myFaceDbService.lambdaQuery().eq(MyFaceDb::getFaceId,faceid).one();
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket("facedb-"+face.getFid())
                .object("face"+face.getFaceId()+".jpg")
                .build()
        );
        myFaceDbService.removeById(faceid);
        return MyFaceResult.success("删除成功");
    }

    @GetMapping("/info/{faceid}")
    @ApiOperation(value = "查询方法",notes = "根据id查询人脸信息")
    public MyFaceResult info(@PathVariable Integer faceid){
        return MyFaceResult.success(myFaceDbService.getById(faceid));
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加")
    public MyFaceResult save(@RequestBody MyFaceDb face) throws Exception {
        MyFaceDb myFaceDb = myFaceDbService.lambdaQuery().eq(MyFaceDb::getFaceName,face.getFaceName()).one();
        if (myFaceDb != null){
            return MyFaceResult.error(400,"人脸姓名重复");
        }
        FaceResult vef = faceService.isGg(face.getFaceBase());
        MyFaceResult vefone = myFaceDbService.vefOne(face.getFaceBase(), String.valueOf(face.getFid()));//人脸是否存在
        if (vef.getCode() == 200 && vefone.getCode() != 200){
            String base64String = face.getFaceBase();
            // 检查并去掉前缀
            final String prefix = "data:image/jpeg;base64,";
            if (base64String.startsWith(prefix)) {
                base64String = base64String.substring(prefix.length());
            }
            // 解码 Base64 字符串
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            // 将字节数组转换为输入流
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);

            myFaceDbService.save(face);
            // 上传文件到 MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("facedb-"+face.getFid())
                    .object("face"+face.getFaceId()+".jpg")
                    .stream(inputStream, decodedBytes.length, -1)
                    .build()
            );
            return MyFaceResult.success("添加成功");
        }
        return MyFaceResult.error(400,"图片不合格或人脸已存在");
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public MyFaceResult update(@RequestBody MyFaceDb face) throws Exception {
        FaceResult vef = faceService.isGg(face.getFaceBase());
        if (vef.getCode() == 200){
            myFaceDbService.updateById(face);
            String base64String = face.getFaceBase();
            // 检查并去掉前缀
            final String prefix = "data:image/jpeg;base64,";
            if (base64String.startsWith(prefix)) {
                base64String = base64String.substring(prefix.length());
            }
            // 解码 Base64 字符串
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            // 将字节数组转换为输入流
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            // 上传文件到 MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("facedb-"+face.getFid())
                    .object("face"+face.getFaceId()+".jpg")
                    .stream(inputStream, decodedBytes.length, -1)
                    .build()
            );
            return MyFaceResult.success("修改成功");
        }
        return MyFaceResult.error(400,"图片不合格");
    }

}
