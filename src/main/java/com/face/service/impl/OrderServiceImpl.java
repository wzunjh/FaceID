package com.face.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.face.bean.Order;
import com.face.service.OrderService;
import com.face.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author 27877
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-07-31 15:11:43
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
implements OrderService{

}
