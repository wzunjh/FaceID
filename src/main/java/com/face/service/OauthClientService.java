package com.face.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.OauthClient;


public interface OauthClientService extends IService<OauthClient> {


    OauthClient getClientByClientId(String clientId);

}