package com.face.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName face_vef_log
 */
@TableName(value ="face_vef_log")
@Data
public class FaceVefLog implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer lid;

    /**
     * 验证时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date vefTime;

    /**
     * 返回code
     */
    private Integer vefCode;

    /**
     * 返回的消息
     */
    private String vefMsg;

    /**
     * 验证人
     */
    private String loginName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}