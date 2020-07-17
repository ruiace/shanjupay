package com.shanjupay.transaction.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    private Cache cache;

    @Autowired
    private PlatformChannelMapper platformChannelMapper;


    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;


    @Autowired
    private PayChannelParamMapper payChannelParamMapper;


    /**
     * 获取平台服务类型
     * @return
     * @throws BussinessException
     */
    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() throws BussinessException {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        List<PlatformChannelDTO> platformChannelDTOS = PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }


    /**
     * 为app绑定平台服务类型
     * @param appId
     * @param platformChannelCodes
     * @throws BussinessException
     */
    @Override
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BussinessException {

        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes)
        );

        if(appPlatformChannel == null){
            AppPlatformChannel appPlatformChannelEntity = new AppPlatformChannel();
            appPlatformChannelEntity.setAppId(appId);
            appPlatformChannelEntity.setPlatformChannel(platformChannelCodes);
            appPlatformChannelMapper.insert(appPlatformChannelEntity);
        }
    }


    /**
     * 查询app是否已经绑定了某个服务类型
     * @param appId
     * @param platformChannelCodes
     * @return
     * @throws BussinessException
     */
    @Override
    public int queryAppbindPlatformChannel(String appId, String platformChannelCodes) throws BussinessException {
        Integer count = appPlatformChannelMapper.selectCount(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes)
        );

        if( count > 0){
            return 1;
        }
        return 0;
    }


    /**
     * 根据平台服务类型查询支付渠道列表
     * @param platformChannelCode
     * @return
     * @throws BussinessException
     */
    @Override
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) throws BussinessException {
        List<PayChannelDTO> lists = platformChannelMapper.selectPayChannelByPlatformChannel(platformChannelCode);
        return lists;
    }


    /**
     * 保存支付渠道参数
     * @param payChannelParamDTO
     * @throws BussinessException
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BussinessException {
        if(payChannelParamDTO == null
                || StringUtils.isBlank(payChannelParamDTO.getAppId())
                || StringUtils.isBlank(payChannelParamDTO.getPlatformChannelCode())
                || StringUtils.isBlank(payChannelParamDTO.getPayChannel())

        ){
            throw new BussinessException(CommonErrorCode.E_100101);
        }

        Long appPlatformId = selectAppPlatformIdByPlatformCodeAndAppId(payChannelParamDTO.getPlatformChannelCode(),payChannelParamDTO.getAppId());

        if(appPlatformId == null){
            throw new BussinessException(CommonErrorCode.E_200207);
        }

        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformId)
                .eq(PayChannelParam::getPayChannel, payChannelParamDTO.getPayChannel())
        );

        if(payChannelParam == null){
            PayChannelParam payChannelParam1 = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParamDTO);

            payChannelParam1.setAppPlatformChannelId(appPlatformId);
            payChannelParamMapper.insert(payChannelParam1);
        }else{
            payChannelParam.setChannelName(payChannelParamDTO.getChannelName());
            payChannelParam.setParam(payChannelParamDTO.getParam());
            payChannelParamMapper.updateById(payChannelParam);
        }


        //保存渠道参数到redis中
        updateCache(payChannelParamDTO.getAppId(),payChannelParamDTO.getPlatformChannelCode());
    }


    /**
     * 保存渠道参数成功，同时将渠道参数保存在Redis中
     * @param appId
     * @param platformChannelCode
     */
    private void updateCache(String appId, String platformChannelCode) {

        String key = RedisUtil.keyBuilder(appId, platformChannelCode);
        Boolean exists = cache.exists(key);
        if(exists){
            cache.del(key);
        }
        List<PayChannelParamDTO> payChannelParamDTOS = queryPayChannelParamByAppIdAndPlatformChannel(appId, platformChannelCode);

        if(payChannelParamDTOS != null){
            cache.set(key, JSON.toJSONString(payChannelParamDTOS));
        }

    }


    /**
     * 根据appid和平台渠道查询与原始支付渠道参数列表
     * @param appId
     * @param platformChannel
     * @return
     * @throws BussinessException
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppIdAndPlatformChannel(String appId, String platformChannel) throws BussinessException {

        String keyBuilder = RedisUtil.keyBuilder(appId, platformChannel);
        Boolean exists = cache.exists(keyBuilder);
        if(exists){
            String jsonValue = cache.get(keyBuilder);
            List<PayChannelParamDTO> payChannelParamDTOS = JSON.parseArray(jsonValue, PayChannelParamDTO.class);
            return payChannelParamDTOS;
        }


        Long aLong = selectAppPlatformIdByPlatformCodeAndAppId(platformChannel, appId);


        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, aLong));

        List<PayChannelParamDTO> payChannelParamDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
        return payChannelParamDTOS;
    }


    /**
     * 获取指定应用,指定平台渠道,指定交易渠道下的原始支付参数
     * @param appId
     * @param platformChannel
     * @return
     */
    @Override
    public PayChannelParamDTO queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(String appId, String platformChannel,String payChannel) {
        Long aLong = selectAppPlatformIdByPlatformCodeAndAppId(platformChannel, appId);


        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getAppPlatformChannelId, aLong).eq(PayChannelParam::getPayChannel,payChannel));

        PayChannelParamDTO payChannelParamDTO = PayChannelParamConvert.INSTANCE.entity2dto(payChannelParam);
        return payChannelParamDTO;
    }


    /**
     * 根据appid和platfomrCode查询appPlatformId
     * @param platformChannelCode
     * @param appId
     * @return
     */
    private Long selectAppPlatformIdByPlatformCodeAndAppId(String platformChannelCode, String appId) {

        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));

        if(appPlatformChannel == null){
            return null;
        }
        return appPlatformChannel.getId();
    }
}
