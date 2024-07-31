package com.face.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@TableName(value ="orders")
@Data
public class Orders implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer orderId;

    /**
     * 
     */
    private Double orderAmount;

    /**
     * 
     */
    private String orderSubject;

    /**
     * 
     */
    private Integer fid;

    /**
     * 
     */
    private Integer goodsId;

    /**
     * 
     */
    private Date orderDate;

    /**
     * 0/未支付，1/已支付
     */
    private Integer payStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}