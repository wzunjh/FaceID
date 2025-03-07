package com.face.service;

import com.face.bean.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.face.bean.result.PayResult;

/**
* @author 27877
* @description 针对表【order】的数据库操作Service
* @createDate 2024-07-31 15:11:43
*/
public interface OrderService extends IService<Orders> {

    PayResult payCode(Integer GoodsId, Integer fid) throws Exception;

    void isPaid(Integer OrderId);

    PayResult payCodeAgain(Integer OrderId) throws Exception;

}
