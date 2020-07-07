package com.shanjupay.merchant.service.impl;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QiniuUtils;
import com.shanjupay.merchant.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author rp
 * @create 2020/7/7
 */
@Service
public class FileServiceImpl implements FileService {


    @Value("${oss.qiniu.url}")
    private String qiniuUrl;

    @Value("${oss.qiniu.accessKey}")
    private String accessKey;

    @Value("${oss.qiniu.secretKey}")
    private String secretKey;

    @Value("${oss.qiniu.bucket}")
    private String bucket;

    /**
     * 上传文件
     * @param bytes
     * @param fileName
     * @return
     * @throws BussinessException
     */
    @Override
    public String upload(byte[] bytes, String fileName) throws BussinessException {

        try {
            QiniuUtils.upload2Qiniu(accessKey,secretKey,bucket,bytes,fileName);
        }catch (Exception e){
            throw  new BussinessException(CommonErrorCode.E_100106);
        }
        return qiniuUrl + fileName;
    }
}