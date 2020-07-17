package com.shanjupay.transaction.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {


    @Value("${shanjupay.payurl}")
    private String payUrl;

    @Reference
    private MerchantService merchantService;


    @Reference
    private AppService appService;


    /**
     * 产门店二维码
     * @param qrCodeDto
     * @return
     * @throws BussinessException
     */
    @Override
    public String createStoreQRCode(QRCodeDto qrCodeDto) throws BussinessException {

        verifyAppAndStore(qrCodeDto.getMerchantId(),qrCodeDto.getAppId(),qrCodeDto.getStoreId());
        //生产支付信息
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setMerchantId(qrCodeDto.getMerchantId());
        payOrderDTO.setAppId(qrCodeDto.getAppId());
        payOrderDTO.setStoreId(qrCodeDto.getStoreId());
        payOrderDTO.setSubject(qrCodeDto.getSubject());//显示标题信息
        payOrderDTO.setChannel("shangju_c2b");//服务类型
        payOrderDTO.setBody(qrCodeDto.getBody());
        String json = JSON.toJSONString(payOrderDTO);
        //将支付信息保存到跑票据中
        String ticket = EncryptUtil.encodeUTF8StringBase64(json);

        //支付入口url
        String payEntryUrl = payUrl +  ticket;

        return payEntryUrl;
    }

    /**
     * 校验应用和门店是否属于当前登录的商户
     * @param merchantId
     * @param appId
     * @param storeId
     */
    private void verifyAppAndStore(Long merchantId, String appId, Long storeId) {
        Boolean containsApp = appService.quereyAppInMerchant(appId, merchantId);
        if(!containsApp){
            throw  new BussinessException(CommonErrorCode.E_200005);
        }
        Boolean containStore = merchantService.queryStoreInMerchant(storeId, merchantId);
        if(!containStore){
            throw new BussinessException(CommonErrorCode.E_200006);
        }
    }
}
