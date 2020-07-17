package com.shanjupay.merchant.controller;


import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商户平台-渠道和支付参数相关",tags = "商户平台-渠道和支付参数相关",description = "商户平台-渠道和支付参数相关")
@RestController
public class PlatformParamController {

    @Reference
    private PayChannelService payChannelService;


    @ApiOperation("获取平台服务类型")
    @GetMapping(value = "/my/platform-channels")
    public List<PlatformChannelDTO> queryPlatformChannel(){
        List<PlatformChannelDTO> platformChannelDTOS = payChannelService.queryPlatformChannel();
        return platformChannelDTOS;
    }

    @ApiOperation("根据平台服务类型查询支付渠道列表")
    @ApiImplicitParam(value = "服务类型编码",name = "platformChannelCode",required = true,dataType = "String",paramType = "path")
    @GetMapping(value = "/my/pay‐channels/platform‐channel/{platformChannelCode}")
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(@PathVariable("platformChannelCode") String platformChannelCode){
        List<PayChannelDTO> payChannelDTOS = payChannelService.queryPayChannelByPlatformChannel(platformChannelCode);
        return payChannelDTOS;
    }


    @ApiOperation("配置支付渠道参数")
    @PostMapping(value = "/my/pay‐channel‐params")
    public void createPayChannelParam(@RequestBody PayChannelParamDTO payChannelParamDTO){
        Long merchantId = SecurityUtil.getMerchantId();
        payChannelParamDTO.setMerchantId(merchantId);
        payChannelService.savePayChannelParam(payChannelParamDTO);
    }



    @ApiOperation("根据appid和平台渠道查询原始支付渠道参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId",value = "应用id",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "platformChannel",value = "平台渠道",required = true,dataType = "String",paramType = "path")
    })
    @GetMapping(value = "/my/pay‐channel‐params/apps/{appId}/platform‐channels/{platformChannel}")
    public List<PayChannelParamDTO> getPayChannelParam(@PathVariable("appId") String appId,@PathVariable("platformChannel") String platformChannel){
        List<PayChannelParamDTO> payChannelParamDTOS = payChannelService.queryPayChannelParamByAppIdAndPlatformChannel(appId, platformChannel);
        return payChannelParamDTOS;
    }





    @ApiOperation("获取指定应用,指定平台渠道,指定交易渠道下的原始支付参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId",value = "应用id",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "platformChannel",value = "平台渠道",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(name = "payChannel",value = "支付渠道",required = true,dataType = "String",paramType = "path")
    })
    @GetMapping(value = "/my/pay‐channel‐params/apps/{appId}/platform‐channels/{platformChannel}/pay-channels/{payChannel}")
    public PayChannelParamDTO getPayChannelParam(@PathVariable("appId") String appId,
                                                       @PathVariable("platformChannel") String platformChannel,
                                                       @PathVariable("payChannel") String payChannel
    ){
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(appId, platformChannel,payChannel);
        return payChannelParamDTO;
    }
}



