package com.shanjupay.transaction.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.UpdateChainWrapper;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.AmountUtil;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.PaymentUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.WeChatBean;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import com.shanjupay.transaction.convert.PayOrderConvert;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PayOrder;
import com.shanjupay.transaction.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSYoungGen;

import javax.swing.text.DefaultEditorKit;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {


    /**
     * weixin:
     *   oauth2RequestUrl: https://open.weixin.qq.com/connect/oauth2/authorize
     *   oauth2CodeReturnUrl: http://xfc.nat300.top/transaction/wx-oauth-code-return
     *   oauth2Token: https://api.weixin.qq.com/sns/oauth2/access_token
     */
    @Value("${weixin.oauth2RequestUrl}")
    private String wxOauth2RequestUrl;

    @Value("${weixin.oauth2CodeReturnUrl}")
    private String wxOauth2CodeReturnUrl;


    @Value("${weixin.oauth2Token}")
    private String oauth2Token;

    @Value("${shanjupay.payurl}")
    private String payUrl;

    @Reference
    private MerchantService merchantService;


    @Reference
    private AppService appService;

    @Autowired
    private PayOrderMapper payOrderMapper;


    @Reference
    private PayChannelAgentService payChannelAgentService;


    @Autowired
    private PayChannelService payChannelService;

    @Autowired
    private RestTemplate restTemplate;


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
     * 支付宝订单保存
     * @param payOrderDTO
     * @return
     * @throws BussinessException
     */
    @Override
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BussinessException {
        payOrderDTO.setPayChannel("ALIPAY_WAP");
        //保存订单到聚合平台
        payOrderDTO = save(payOrderDTO);

        PaymentResponseDTO paymentResponseDTO = alipayH5(payOrderDTO.getOutTradeNo());

        return paymentResponseDTO;
    }



    /**
     * 更新订单支付状态
     * @param tradeNo 聚合支付平台订单编号
     * @param payChannelTradeNo 支付宝微信的交易流水号
     * @param state 订单状态 0-订单生产 1-支付中 2-支付成功 4-关闭  5-失败
     */
    @Override
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) {

        LambdaUpdateWrapper<PayOrder> updateWrapper = new UpdateWrapper<PayOrder>().lambda();
        updateWrapper.eq(PayOrder::getTradeNo,tradeNo).set(PayOrder::getPayChannelTradeNo,payChannelTradeNo).set(PayOrder::getTradeState,state);
        if(state != null && "2".equals(state)){
            updateWrapper.set(PayOrder::getPaySuccessTime,LocalDateTime.now());
        }
                
        payOrderMapper.update(null,updateWrapper);
    }


    /**
     * 获取微信授权码
     * @param order
     * @return
     * @throws BussinessException
     */
    @Override
    public String getWXOauth2Code(PayOrderDTO order) throws BussinessException {
        //将订单对象封装到state参数中
        String state = EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(order));

        //应用id
        String appId = order.getAppId();
        //聚合支付的渠道 此处使用渠道编码
        String channel = order.getChannel();

        //获取微信支付渠道参数, 根据appid, 服务类型,支付渠道查询,支付渠道参数
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(appId, channel, "WX_JSAPI");

        if(payChannelParamDTO == null){
            throw  new BussinessException(CommonErrorCode.E_300007);
        }

        //获取支付渠道参数
        String param = payChannelParamDTO.getParam();
        WXConfigParam wxConfigParam = JSON.parseObject(param, WXConfigParam.class);

        try {
            String url = String.format("%s?appid=%s&scope=snsapi_base&state=%s&redirect_uri=%s",
                    wxOauth2RequestUrl,
                    wxConfigParam.getAppId(),
                    state,
                    EncryptUtil.encodeUTF8StringBase64(wxOauth2CodeReturnUrl)
                    );
            //微信生成授权码url
            log.info("微信生成授权码url={}",url);
            return "redirect:" + url;

        }catch (Exception e){
            e.printStackTrace();
            return "forword:/pay-page-error";//获取授权码链接失败
        }

    }


    /**
     * 获取微信openid
     * @param code
     * @param appId
     * @return
     */
    @Override
    public String getWXOauthOpenId(String code, String appId) {
        //获取微信支付渠道参数, 根据应用, 服务类型,支付渠道 查询支付渠道参数
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(appId, "shanju_c2b", "WX_JSAPI");
        if(payChannelParamDTO == null){
            throw new BussinessException(CommonErrorCode.E_300007);
        }
        String param = payChannelParamDTO.getParam();
        WXConfigParam wxConfigParam = JSON.parseObject(param, WXConfigParam.class);
        //秘钥
        String appSecret = wxConfigParam.getAppSecret();
        String url = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                oauth2Token,
                wxConfigParam.getAppId(),
                appSecret,
                code
                );


        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String body = exchange.getBody();
        return JSON.parseObject(body).getString("openid");

    }

    @Override
    public Map<String, String> submitOrderByWechat(PayOrderDTO payOrderDTO) throws BussinessException {


        String openId = payOrderDTO.getOpenId();
        //支付渠道
        payOrderDTO.setChannel("WX_JSAPI");
        //保存订单到聚合平台数据库
        PayOrderDTO save = save(payOrderDTO);

        //调用支付渠道代理服务,调用微信下单接口
        return weChatJsapi(openId,save.getTradeNo());
    }


    /**
     * 调用支付渠道代理服务,调用微信下单接口
     * @param openId
     * @param tradeNo
     * @return
     */
    private Map<String, String> weChatJsapi(String openId, String tradeNo) {

        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);

        WeChatBean weChatBean = new WeChatBean();
        weChatBean.setOpenId(openId);
        weChatBean.setOutTradeNo(payOrderDTO.getTradeNo());//聚合平台订单号
        weChatBean.setTotalFee(payOrderDTO.getTotalAmount());//单位:分


        weChatBean.setSpbillCreateIp(payOrderDTO.getClientIp());
        weChatBean.setBody(payOrderDTO.getBody());
        weChatBean.setNotifyUrl("none");

        PayChannelParamDTO payChannelParamDTO = payChannelService.queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(payOrderDTO.getAppId(), "shanju_c2b", "WX_JSAPI");

        WXConfigParam wxconfigParam = JSON.parseObject(payChannelParamDTO.getParam(),WXConfigParam.class);
        Map<String, String> payOrderByWeChatJSAPI = payChannelAgentService.createPayOrderByWeChatJSAPI(wxconfigParam, weChatBean);
        return payOrderByWeChatJSAPI;
    }


    //调用支付宝下单接口
    private PaymentResponseDTO alipayH5(String tradeNo){
        AlipayBean alipayBean = new AlipayBean();
        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);
        alipayBean.setOutTradeNo(tradeNo);
        alipayBean.setSubject(payOrderDTO.getSubject());
        String totalAmout = null;//支付宝输入的参数是元
        try {
            totalAmout = AmountUtil.changeF2Y(payOrderDTO.getTotalAmount().toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw  new BussinessException(CommonErrorCode.E_300006);
        }
        alipayBean.setTotalAmount(totalAmout);

        alipayBean.setBody(payOrderDTO.getBody());
        alipayBean.setStoreId(payOrderDTO.getStoreId());
        alipayBean.setExpireTime("30m");

        //根据应用,服务类型,支付渠道,查询支付渠道参数
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryPayChannelParamByAppIdAndPlatformChannelAndPayChannel(payOrderDTO.getAppId(),
                payOrderDTO.getChannel(),
                "ALIPAY_WAP");

        if(payChannelParamDTO == null){
            throw new BussinessException(CommonErrorCode.E_300007);
        }

        AliConfigParam aliConfigParam = JSON.parseObject(payChannelParamDTO.getParam(), AliConfigParam.class);

        PaymentResponseDTO payOrderByAliWAP = payChannelAgentService.createPayOrderByAliWAP(aliConfigParam, alipayBean);
        return payOrderByAliWAP;
    }

    /**
     * 根据订单编号查询订单信息
     * @param tradeNo
     * @return
     */
    private PayOrderDTO queryPayOrder(String tradeNo) {

        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getTradeNo,tradeNo)
        );

        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }



    /**
     * 保存订单到聚合平台
     * @param payOrderDTO
     * @return
     */
    private PayOrderDTO save(PayOrderDTO payOrderDTO) {

        PayOrder payOrder = PayOrderConvert.INSTANCE.dto2entity(payOrderDTO);

        payOrder.setTradeNo(PaymentUtil.genUniquePayOrderNo());
        payOrder.setCreateTime(LocalDateTime.now());
        //设置过期时间
        payOrder.setExpireTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));
        payOrder.setCurrency("CNY");//设置币种
        payOrder.setTradeState("0");//订单状态
        int insert = payOrderMapper.insert(payOrder);

        return PayOrderConvert.INSTANCE.entity2dto(payOrder);

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
