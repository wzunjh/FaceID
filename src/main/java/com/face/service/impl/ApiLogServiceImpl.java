package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.ApiLog;
import com.face.mapper.ApiLogMapper;
import com.face.service.ApiLogService;
import org.springframework.stereotype.Service;

@Service
public class ApiLogServiceImpl extends ServiceImpl<ApiLogMapper, ApiLog>
    implements ApiLogService {

}




