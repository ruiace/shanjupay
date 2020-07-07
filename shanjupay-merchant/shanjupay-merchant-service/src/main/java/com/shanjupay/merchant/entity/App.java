package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("app")
@ApiModel(value="App", description="")
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "商店名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "所属商户")
    @TableField("MERCHANT_ID")
    private Long merchantId;

    @ApiModelProperty(value = "应用公钥(RSAWithSHA256)")
    @TableField("PUBLIC_KEY")
    private String publicKey;

    @ApiModelProperty(value = "授权回调地址")
    @TableField("NOTIFY_URL")
    private String notifyUrl;


}
