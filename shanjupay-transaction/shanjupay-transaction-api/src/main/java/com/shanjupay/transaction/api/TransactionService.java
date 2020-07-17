package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.transaction.api.dto.QRCodeDto;

/**
 * 交易订单相关服务接口
 */
public interface TransactionService {

    /**
     * 生产门店二维码
     * @param qrCodeDto
     * @return  支付入口url,将二维码的参数组成json,并用base64编码;
     * @throws BussinessException
     */
    String createStoreQRCode(QRCodeDto qrCodeDto)throws BussinessException;
}
