package com.face.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


@TableName(value ="api_log")
@Data
public class ApiLog implements Serializable {
    /**
     * 主键
     */
    private Integer fid;

    /**
     * 请求时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date apiTime;

    /**
     * 返回code
     */
    private Integer apiCode;

    /**
     * 返回的消息
     */
    private String apiMsg;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}