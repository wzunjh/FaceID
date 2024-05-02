package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.MyFaceDb;
import com.face.mapper.MyFaceDbMapper;
import com.face.service.MyFaceDbService;
import org.springframework.stereotype.Service;

@Service
public class MyFaceDbServiceImpl extends ServiceImpl<MyFaceDbMapper, MyFaceDb>
        implements MyFaceDbService {
}
