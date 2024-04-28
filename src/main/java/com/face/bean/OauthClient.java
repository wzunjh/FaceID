package com.face.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@TableName(value ="oauth_client")
@Data
public class OauthClient implements Serializable {

    @TableId
    private String ClientId;

    private String ClientSecret;

    private String WebServerRedirectUrl;

    private Integer fid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
