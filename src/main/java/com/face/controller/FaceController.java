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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tanyongpeng
 * <p>des</p>
 **/
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
        face.setVefNum(0);
        face.setCreateTime(new Date());
        faceService.save(face);
        return FaceResult.success("添加成功");
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改")
    public FaceResult update(@RequestBody Face face){
        faceService.updateById(face);
        return FaceResult.success("修改成功");
    }


}
