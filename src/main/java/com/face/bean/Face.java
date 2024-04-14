package com.face.bean;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
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
     * 身份证号码
     */
    private String idNo;

    /**
     * 认证状态
     */
    private String id2Status;

    /**
     * 城市地区
     */
    private String city;

    /**
     * apikey
     */
    private String apiKey;

    /**
     * 接口调用次数
     */
    private Integer apiNum;

    /**
     * 接口调用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date apiTime;

    /**
     * phone
     */
    private String phone;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}