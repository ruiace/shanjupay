package com.shanjupay.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RandomUuidUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppConvert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AppMapper appMapper;
    /**
     *  商户下创建应用
     * @param dto
     * @return
     * @throws BussinessException
     */
    @Override
    @Transactional
    public AppDTO createApp(AppDTO dto) throws BussinessException {

        Merchant merchant = merchantMapper.selectById(dto.getMerchantId());
        if(merchant == null){
            throw new BussinessException(CommonErrorCode.E_100108);
        }
        if(!"2".equals(merchant.getAuditStatus())){
            throw new BussinessException(CommonErrorCode.E_100109);
        }
        if(isExistAppName(dto.getAppName())){

            throw new BussinessException(CommonErrorCode.E_100110);
        }

        dto.setAppId(RandomUuidUtil.getUUID());
        App app = AppConvert.INSTANCE.dto2entity(dto);
        appMapper.insert(app);

        return dto;
    }


    /**
     * 根据商户id查询商户下的所用app
     * @param merchantId
     * @return
     * @throws BussinessException
     */
    @Override
    public List<AppDTO> queryAppByMerchantId(Long merchantId) throws BussinessException {
        List<App> apps = appMapper.selectList(new QueryWrapper<App>().lambda().eq(App::getMerchantId, merchantId));
        List<AppDTO> appDTOS = AppConvert.INSTANCE.listEntity2dto(apps);
        return appDTOS;
    }


    /**
     * 根据app的id查询app
     * @return
     * @throws BussinessException
     * @param id
     */
    @Override
    public AppDTO getAppById(String id) throws BussinessException {
        App app = appMapper.selectOne(new QueryWrapper<App>().lambda().eq(App::getAppId, id));
        AppDTO appDTO = AppConvert.INSTANCE.entity2dto(app);
        return appDTO;
    }


    /**
     * 查询应用是否属于某个商户
     * @param appId
     * @param merchantId
     * @return
     */
    @Override
    public Boolean quereyAppInMerchant(String appId, Long merchantId) {

        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>().eq(App::getId, appId).eq(App::getMerchantId, merchantId));

        return count > 0;
    }


    /**
     *  校验用户名称是否已经存在
     * @param appName
     * @return
     */
    private boolean isExistAppName(String appName) {
        Integer count = appMapper.selectCount(new QueryWrapper<App>().lambda().eq(App::getAppName, appName));
        return count > 0;

    }


}
