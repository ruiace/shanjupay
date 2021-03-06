package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商户平台 -- 门店管理",tags = "商户平台 -- 门店管理",description = "商户平台 -- 门店管理")
@RestController
public class StoreController {


    @Reference
    private MerchantService merchantService;

    @ApiOperation("分页条件查询商户下门店列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo",value = "页码",required = true,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,paramType = "query",dataType = "int")

    })
    @PostMapping("/my/stores/merchants/page")
    public PageVO<StoreDTO> queryStoreByPage(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {

        Long merchantId = SecurityUtil.getMerchantId();
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchantId);

        PageVO<StoreDTO> storeDTOS = merchantService.queryStoreByPage(storeDTO, pageNo, pageSize);
        return storeDTOS;
    }
}