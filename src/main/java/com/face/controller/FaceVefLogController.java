package com.face.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.bean.ApiLog;
import com.face.bean.FaceVefLog;
import com.face.bean.result.ApiResult;
import com.face.bean.result.FaceResult;
import com.face.service.ApiLogService;
import com.face.service.FaceService;
import com.face.service.FaceVefLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/face/log")
@Api("人脸日志接口")
@Slf4j
public class FaceVefLogController {

    @Autowired
    FaceVefLogService faceVefLogService;

    @Autowired
    ApiLogService apiLogService;

    @Autowired
    FaceService faceService;



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

    @GetMapping("/{fid}")
    @ApiOperation(value = "查询Api日志信息", notes = "根据id查询")
    public FaceResult info(
            @PathVariable Integer fid,
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        IPage<ApiLog> page;
        if (fid.equals(1)) {
            // fid为1时，查询所有日志信息
            page = apiLogService.page(new Page<>(current, size), new QueryWrapper<ApiLog>()
                    .between(StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime), "api_time", startTime, endTime)
                    .orderByDesc("api_time"));
        } else {
            // fid不为1时，查询fid为传入值的日志信息
            page = apiLogService.page(new Page<>(current, size), new QueryWrapper<ApiLog>()
                    .eq("fid", fid)
                    .between(StrUtil.isNotBlank(startTime) && StrUtil.isNotBlank(endTime), "api_time", startTime, endTime)
                    .orderByDesc("api_time"));
        }
        return FaceResult.success(page);
    }

    @GetMapping("/vef")
    @ApiOperation(value = "Auth认证", notes = "根据Auth进行认证")
    public ApiResult vefAuth(@RequestParam String Auth, @RequestParam Integer fid) {
        return faceService.vefAuth(Auth,fid);
    }


}
