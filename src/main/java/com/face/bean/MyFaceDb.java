package com.face.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="myfacedb")
@Data
public class MyFaceDb implements Serializable {
        /**
         * 用户主键
         */
        private Integer fid;

        /**
         * 图片数据 base_64编码
         */
        private String faceBase;


        /**
         * 人脸名称
         */
        private String faceName;


        /**
         * 人脸主键
         */
        @TableId(type = IdType.AUTO)
        private Integer faceId;



        @TableField(exist = false)
        private static final long serialVersionUID = 1L;
}
