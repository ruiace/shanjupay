package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;

import java.util.Map;

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


    /**
     * 支付宝订单保存
     * @param payOrderDTO
     * @return
     * @throws BussinessException
     */
    PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BussinessException;


    /**
     * 更新订单支付状态
     * @param tradeNo 聚合支付平台订单编号
     * @param payChannelTradeNo 支付宝微信的交易流水号
     * @param state 订单状态 0-订单生产 1-支付中 2-支付成功 4-关闭  5-失败
     */
    void updateOrderTradeNoAndTradeState(String tradeNo,String payChannelTradeNo,String state);


    /**
     * 获取微信授权码
     * @param payOrderDTO
     * @return
     * @throws BussinessException
     */
    String getWXOauth2Code(PayOrderDTO payOrderDTO) throws BussinessException;

    /**
     * 获取微信openid
     * @param code
     * @param appId
     * @return
     */
    String getWXOauthOpenId(String code,String appId);


    /**
     * 微信确认支付
     * @param payOrderDTO
     * @return
     * @throws BussinessException
     */
    Map<String,String> submitOrderByWechat(PayOrderDTO payOrderDTO) throws BussinessException;
}
