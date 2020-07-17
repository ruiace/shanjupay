package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.merchant.api.dto.AppDTO;

import java.util.List;

public interface AppService {

    /**
     * 商户下创建应用
     * @param dto
     * @return
     * @throws BussinessException
     */
    AppDTO createApp(AppDTO dto) throws BussinessException;

    /**
     * 根据商户id查询商户下的所用app
     * @param merchantId
     * @return
     * @throws BussinessException
     */
    List<AppDTO> queryAppByMerchantId(Long merchantId) throws BussinessException;


    /**
     * 根据app的id查询app
     * @return
     * @throws BussinessException
     * @param id
     */
    AppDTO getAppById(String id) throws BussinessException;


    /**
     * 查询应用是否属于某个商户
     * @param appId
     * @param merchantId
     * @return
     */
    Boolean quereyAppInMerchant(String appId,Long merchantId);

}
