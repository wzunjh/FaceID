package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.Goods;
import com.face.service.GoodsService;
import com.face.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

/**
* @author 27877
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2024-07-31 14:43:40
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
implements GoodsService{

}
