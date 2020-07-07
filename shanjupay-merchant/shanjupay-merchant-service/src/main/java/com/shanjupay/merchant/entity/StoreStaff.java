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
@TableName("store_staff")
@ApiModel(value="StoreStaff", description="")
public class StoreStaff implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "门店标识")
    @TableField("STORE_ID")
    private Long storeId;

    @ApiModelProperty(value = "员工标识")
    @TableField("STAFF_ID")
    private Long staffId;


}
