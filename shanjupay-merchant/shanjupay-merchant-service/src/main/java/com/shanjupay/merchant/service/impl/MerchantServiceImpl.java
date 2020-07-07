package com.shanjupay.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.oracle.tools.packager.Log;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 根据id查询商户信息
     * @param id
     * @return
     */
    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        log.info("根据id查询商户信息,返回结果={}",JSON.toJSONString(merchantDTO));
        return merchantDTO;
    }


    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {

        log.info("商户注册,请求参数={}",JSON.toJSONString(merchantDTO));
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
        merchant.setAuditStatus("0");
        int insert = merchantMapper.insert(merchant);
        MerchantDTO merchantDTONew = MerchantConvert.INSTANCE.entity2dto(merchant);
        log.info("商户注册,返回结果={}", JSON.toJSONString(merchantDTONew));
        return merchantDTONew;
    }
}
