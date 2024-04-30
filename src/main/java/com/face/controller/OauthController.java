package com.face.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.face.bean.Face;
import com.face.bean.OauthClient;
import com.face.bean.result.FaceResult;
import com.face.service.OauthClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/oau")
@Api("授权端信息增删改查")
@Slf4j
public class OauthController {

    @Autowired
    OauthClientService oauthClientService;

    @GetMapping("/List")
    @ApiOperation(value = "应用列表",notes = "查询所有的应用信息")
    public FaceResult faceList(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam String fid,
            @RequestParam(required = false) String name
    ){
        if(Objects.equals(fid, "1")){
            IPage<Face> page = oauthClientService.page(new Page(current, size), new QueryWrapper<OauthClient>()
                    .like(StrUtil.isNotBlank(name),"name",name));
            return FaceResult.success(page);
        }

        IPage<Face> page = oauthClientService.page(new Page(current, size), new QueryWrapper<OauthClient>()
                .eq(StrUtil.isNotBlank(fid),"fid",fid)
                .like(StrUtil.isNotBlank(name),"name",name));
        return FaceResult.success(page);
    }

    @GetMapping("/Delete")
    @ApiOperation(value = "删除应用",notes = "根据id进行删除")
    public FaceResult faceDelete(@RequestParam String clientId){
        oauthClientService.removeById(clientId);
        return FaceResult.success("删除成功");
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加")
    public FaceResult save(@RequestBody OauthClient oauthClient){
        String client_id = generateRandomOTP();
        String client_secret = getString();
        oauthClient.setClientId(client_id);
        oauthClient.setClientSecret(client_secret);
        oauthClientService.save(oauthClient);
        return FaceResult.success("添加成功");
    }

    private String generateRandomOTP() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    //随机生成密钥算法
    private static @NotNull String getString() {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
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
