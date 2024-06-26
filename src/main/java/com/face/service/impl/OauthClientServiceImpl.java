package com.face.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.OauthClient;
import com.face.mapper.OauthClientMapper;
import com.face.service.OauthClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthClientServiceImpl extends ServiceImpl<OauthClientMapper, OauthClient>
        implements OauthClientService {

    @Autowired
    private OauthClientMapper oauthClientMapper;

    @Override
    public OauthClient getClientByClientId(String clientId) {
        return oauthClientMapper.selectById(clientId);
    }
}
