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
 * @TableName face
 */
@TableName(value ="face")
@Data
public class Face implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer fid;

    /**
     * 图片数据 base_64编码
     */
    private String faceBase;

    /**
     * 插入时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 验证次数
     */
    private Integer vefNum;

    /**
     * 人脸名称
     */
    private String faceName;

    /**
     * 人脸备注
     */
    private String remark;

    /**
     * 人脸是否可用，(0==可用，1,不可用)
     */
    private Integer faceStatus;

    /**
     * 扩展字段1
     */
    private String updateExtend1;

    /**
     * 扩展字段2
     */
    private String updateExtend2;

    /**
     * 扩展字段3
     */
    private String updateExtend3;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}