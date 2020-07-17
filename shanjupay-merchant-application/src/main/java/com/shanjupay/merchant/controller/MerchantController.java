package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QRCodeUtil;
import com.shanjupay.common.valid.ValidUtils;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.convert.MerchantDetailConvert;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.TreeMap;
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


    /**
     * 门店二维码订单标题
     */
    @Value("${shanjupay.c2b.subject}")
    private String subject;


    /**
     * 门店二维码订单内容
     */
    @Value("shanjupay.c2b.body")
    private String body;

    @Reference
    private TransactionService transactionService;



    @ApiOperation("生产商户应用门店二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId",value = "商户应用id",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "storeId",value = "商户门店id",required = true,dataType = "Long",paramType = "path")
    })
    @GetMapping("/my/apps/{appId}/stores/{storeId}/app-store-qrcode")
    private String createCScanBStoreQRCode(String appId,Long storeId){
        Long merchantId = SecurityUtil.getMerchantId();
        QRCodeDto qrCodeDto = new QRCodeDto();
        qrCodeDto.setMerchantId(merchantId);
        qrCodeDto.setAppId(appId);
        qrCodeDto.setStoreId(storeId);

        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);
        qrCodeDto.setBody(String.format(subject,merchantDTO.getMerchantName()));
        qrCodeDto.setSubject(String.format(body,merchantDTO.getMerchantName()));

        String storeQRCode = transactionService.createStoreQRCode(qrCodeDto);

        try {
            //根据返回的url,调用生产二维码工具类,生产二维码base64 url
            String qrCode = QRCodeUtil.createQRCode(storeQRCode, 200, 200);
            return  qrCode;
        } catch (IOException e) {

            throw new BussinessException(CommonErrorCode.E_200007);
        }

    }




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
