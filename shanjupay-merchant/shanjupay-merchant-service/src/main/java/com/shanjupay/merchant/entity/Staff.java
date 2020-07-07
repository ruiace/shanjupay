package com.shanjupay.merchant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("staff")
@ApiModel(value="Staff", description="")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "商户ID")
    @TableField("MERCHANT_ID")
    private Long merchantId;

    @ApiModelProperty(value = "姓名")
    @TableField("FULL_NAME")
    private String fullName;

    @ApiModelProperty(value = "职位")
    @TableField("POSITION")
    private String position;

    @ApiModelProperty(value = "用户名(关联统一用户)")
    @TableField("USERNAME")
    private String username;

    @ApiModelProperty(value = "手机号(关联统一用户)")
    @TableField("MOBILE")
    private String mobile;

    @ApiModelProperty(value = "员工所属门店")
    @TableField("STORE_ID")
    private Long storeId;

    @ApiModelProperty(value = "最后一次登录时间")
    @TableField("LAST_LOGIN_TIME")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "0表示禁用，1表示启用")
    @TableField("STAFF_STATUS")
    private Boolean staffStatus;


}
