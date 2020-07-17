package com.shanjupay.merchant.controller;

import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商户平台应用管理",tags = "商户平台应用管理",description = "商户平台应用管理")
@RestController
public class AppController {


    @Reference
    private AppService appService;

    @Reference
    private PayChannelService payChannelService;


    @ApiOperation("商户创建应用")
    @PostMapping(value = "/my/apps")
    public AppDTO createApp(@RequestBody AppDTO appDTO){
        Long merchantId = SecurityUtil.getMerchantId();
        appDTO.setMerchantId(merchantId);
        AppDTO app = appService.createApp(appDTO);
        return app;
    }

    @ApiOperation("根据app的id查询app")
    @ApiImplicitParam(name = "appId",value = "应用id",required = true,dataType = "String",paramType = "path")
    @GetMapping("/my/apps/{appId}")
    public AppDTO getApp(@PathVariable("appId") String appId){
        AppDTO dto = appService.getAppById(appId);
        return dto;
    }

    @ApiOperation("查询商户下的应用列表")
    @GetMapping("/my/apps")
    public List<AppDTO> queryMyApps(){

        Long merchantId = SecurityUtil.getMerchantId();
        List<AppDTO> appDTOS = appService.queryAppByMerchantId(merchantId);
        return appDTOS;
    }


    @ApiOperation("绑定服务类型")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id",name = "appId",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "服务类型Code",name = "platformChannelCodes",required = true,dataType = "String",paramType = "path")
    })
    @PostMapping("/my/apps/{appId}/{platform-channels}")
    public void bindPlatformForApp(@PathVariable("appId") String appId,@PathVariable("platformChannelCodes") String platformChannelCodes){

        payChannelService.bindPlatformChannelForApp(appId,platformChannelCodes);
    }


    @ApiOperation("查询应用是否绑定某个服务类型")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id",name = "appId",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "服务类型Code",name = "platformChannelCodes",required = true,dataType = "String",paramType = "path")
    })
    @GetMapping("/my/apps/{appId}/{platform-channels}")
    public int queryAppbindPlatform(@PathVariable("appId") String appId,@PathVariable("platformChannelCodes") String platformChannelCodes){
        int count = payChannelService.queryAppbindPlatformChannel(appId, platformChannelCodes);
        return count;
    }

}
