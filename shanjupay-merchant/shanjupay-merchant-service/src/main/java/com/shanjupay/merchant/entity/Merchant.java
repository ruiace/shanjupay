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
@TableName("merchant")
@ApiModel(value="Merchant", description="")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    @ApiModelProperty(value = "商户名称")
    @TableField("MERCHANT_NAME")
    private String merchantName;

    @ApiModelProperty(value = "企业编号")
    @TableField("MERCHANT_NO")
    private String merchantNo;

    @ApiModelProperty(value = "企业地址")
    @TableField("MERCHANT_ADDRESS")
    private String merchantAddress;

    @ApiModelProperty(value = "商户类型")
    @TableField("MERCHANT_TYPE")
    private String merchantType;

    @ApiModelProperty(value = "营业执照（企业证明）")
    @TableField("BUSINESS_LICENSES_IMG")
    private String businessLicensesImg;

    @ApiModelProperty(value = "法人身份证正面照片")
    @TableField("ID_CARD_FRONT_IMG")
    private String idCardFrontImg;

    @ApiModelProperty(value = "法人身份证反面照片")
    @TableField("ID_CARD_AFTER_IMG")
    private String idCardAfterImg;

    @ApiModelProperty(value = "联系人姓名")
    @TableField("USERNAME")
    private String username;

    @ApiModelProperty("密码")
    @TableField("PASSWORD")
    private String password;

    @ApiModelProperty(value = "联系人手机号(关联统一账号)")
    @TableField("MOBILE")
    private String mobile;

    @ApiModelProperty(value = "联系人地址")
    @TableField("CONTACTS_ADDRESS")
    private String contactsAddress;

    @ApiModelProperty(value = "审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝")
    @TableField("AUDIT_STATUS")
    private String auditStatus;

    @ApiModelProperty(value = "租户ID,关联统一用户")
    @TableField("TENANT_ID")
    private Long tenantId;


}
