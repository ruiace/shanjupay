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
@TableName("store")
@ApiModel(value="Store", description="")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "门店名称")
    @TableField("STORE_NAME")
    private String storeName;

    @ApiModelProperty(value = "门店编号")
    @TableField("STORE_NUMBER")
    private Long storeNumber;

    @ApiModelProperty(value = "所属商户")
    @TableField("MERCHANT_ID")
    private Long merchantId;

    @ApiModelProperty(value = "父门店")
    @TableField("PARENT_ID")
    private Long parentId;

    @ApiModelProperty(value = "0表示禁用，1表示启用")
    @TableField("STORE_STATUS")
    private Boolean storeStatus;

    @ApiModelProperty(value = "门店地址")
    @TableField("STORE_ADDRESS")
    private String storeAddress;


}
