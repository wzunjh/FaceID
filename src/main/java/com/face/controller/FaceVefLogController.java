package com.face.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.bean.Face;
import com.face.bean.FaceVefLog;
import com.face.bean.result.FaceResult;
import com.face.service.FaceVefLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tanyongpeng
 * <p>des</p>
 **/
@RestController
@RequestMapping("/face/log")
@Api("人脸日志接口")
@Slf4j
public class FaceVefLogController {

    @Autowired
    FaceVefLogService faceVefLogService;

    @GetMapping("/list")
    @ApiOperation(value = "日志接口",notes = "查询所有验证人脸日志信息")
    public FaceResult faceList(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String vefCode,
            @RequestParam(required = false) String loginName
    ){
        IPage<FaceVefLog> page = faceVefLogService.page(new Page(current, size), new QueryWrapper<FaceVefLog>()
                .like(StrUtil.isNotBlank(vefCode),"vef_code",vefCode)
                .like(StrUtil.isNotBlank(loginName),"login_name",loginName)
                .between(StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime),"vef_time",startTime,endTime)
                .orderByDesc("vef_time"));
        return FaceResult.success(page);
    }

}
