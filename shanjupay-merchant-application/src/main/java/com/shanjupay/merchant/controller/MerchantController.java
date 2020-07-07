package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.valid.ValidUtils;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.convert.MerchantDetailConvert;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@Api(value="商户平台应用接口",tags = "商户平台应用接口",description = "商户平台应用接口")
public class MerchantController {

    @Reference
    private MerchantService merchantService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private FileService FileService;




    @ApiOperation("测试")
    @GetMapping(path = "/hello")
    public String hello(){
        return "hello";
    }

    @ApiOperation("测试")
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string")
    @PostMapping(value = "/hi")
    public String hi(String name) {
        return "hi,"+name;
    }

    @ApiOperation("根据商户id查询商户信息")
    @ApiImplicitParam(name = "id",value = "商户id",required = true,dataType = "Long",paramType = "path",example = "1")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id){
        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }


    @ApiOperation("获取手机验证码")
    @ApiImplicitParam(name = "phone",value = "手机号",required = true,dataType = "String",paramType = "query")
    @GetMapping("/sms")
    public String getSMSCode(@RequestParam("phone") String phone){
        String result = smsService.sendMsg(phone);
        return result;
    }


    @ApiOperation("注册商户")
    @PostMapping("/merchants/register")
    public MerchantRegisterVO registerMerchant(@RequestBody @Validated MerchantRegisterVO merchantRegister, BindingResult errorResult){
        ValidUtils.validParams(errorResult);
        smsService.checkVerifiyCode(merchantRegister.getVerifyKey(),merchantRegister.getVerifyCode());
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setUsername(merchantRegister.getUsername());
        merchantDTO.setPassword(merchantRegister.getPassword());
        merchantDTO.setMobile(merchantRegister.getMobile());
        MerchantDTO merchant = merchantService.createMerchant(merchantDTO);
        return merchantRegister;
    }



    @ApiOperation("证件上传")
    @ApiImplicitParam(name = "file",value = "上传文件",required = true)
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file){
        //原始文件名称
        String originalFilename = file.getOriginalFilename();
        //文件后轴
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") - 1);
        //文件名称
        String fileName = UUID.randomUUID().toString() + suffix;

        String uploadName = null;
        try {
            uploadName = FileService.upload(file.getBytes(), fileName);
        } catch (IOException e) {
            throw new BussinessException(CommonErrorCode.E_100106);
        }
        return uploadName;
    }



    @ApiOperation("商户资质申请")
    @PostMapping("/my/merchants/save")
    public void  saveMerchant(@RequestBody MerchantDetailVO merchantDetailVO){

        Long merchantId = SecurityUtil.getMerchantId();
        MerchantDTO merchantDTO = MerchantDetailConvert.INSTANCE.vo2dto(merchantDetailVO);
        merchantDTO.setId(merchantId);
        merchantService.applyMerchant(merchantDTO);
    }














}
