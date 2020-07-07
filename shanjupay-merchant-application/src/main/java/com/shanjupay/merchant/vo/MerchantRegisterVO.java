package com.shanjupay.merchant.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel(value = "MerchantRegisterVO",description = "商户注册信息")
@Data
public class MerchantRegisterVO implements Serializable {

    @ApiModelProperty("商户名称")
    @NotBlank(message = "商户名称不能为空")
    private String username;

    @ApiModelProperty("商户密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("商户手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty("验证码的key")
    @NotBlank(message = "验证码key不能为空")
    private String verifyKey;

    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

}
