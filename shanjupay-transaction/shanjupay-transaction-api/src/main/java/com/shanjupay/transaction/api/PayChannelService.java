package com.shanjupay.transaction.api;


import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * 支付渠道服务,管理平台支付渠道,原始支付渠道,以及相关配置
 */
public interface PayChannelService {

    /**
     * 获取平台服务类型
     * @return
     * @throws BussinessException
     */
    List<PlatformChannelDTO> queryPlatformChannel() throws BussinessException;


    /**
     * 为app绑定平台服务类型
     * @throws BussinessException
     */
    void bindPlatformChannelForApp(String appId,String platformChannelCodes) throws BussinessException;


    /**
     * 查询app是否已经绑定了某个服务类型
     * @param appId
     * @param platformChannelCodes
     * @return
     * @throws BussinessException
     */
    int queryAppbindPlatformChannel(String appId,String platformChannelCodes) throws BussinessException;


    /**
     * 根据平台服务类型查询支付渠道列表
     * @param platformChannelCode
     * @return
     * @throws BussinessException
     */
    List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode)throws BussinessException;

    /**
     * 保存支付渠道参数
     * @param payChannelParamDTO
     * @throws BussinessException
     */
    void savePayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BussinessException;


    /**
     * 根据appid和平台渠道查询与原始支付渠道参数列表
     * @param appId
     * @param platformChannel
     * @return
     * @throws BussinessException
     */
    List<PayChannelParamDTO> queryPayChannelParamByAppIdAndPlatformChannel(String appId,String platformChannel) throws BussinessException;


    /**
     * 获取指定应用,指定平台渠道,指定交易渠道下的原始支付参数
     * @param appId
     * @param platformChannel
     * @return
     */
    PayChannelParamDTO queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(String appId, String platformChannel,String payChannel);
}
