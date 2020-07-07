package com.shanjupay.common.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.shanjupay.common.domain.BussinessException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;

/**
 * 〈一句话功能简述〉<br>
 * 〈七牛云操作工具类〉
 *
 * @author rp
 * @create 2020/7/7
 */
@Slf4j
public class QiniuUtils {

    /**
     * 上传图片
     *
     * @param accessKey
     * @param secretKey
     * @param bucket
     * @param bytes
     * @param fileName
     */
    public static void upload2Qiniu(String accessKey, String secretKey, String bucket, byte[] bytes, String fileName) {
        log.info("七牛云上传图片请求参数accessKey={},secretKey={},bucket={},fileName={}",accessKey,secretKey,bucket,fileName);
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(cfg);

        FileInputStream fileInputStream;
        try {
           // fileInputStream = new FileInputStream(new File("D:\\test.jpg"));
            //byte[] uploadBytes = IOUtils.toByteArray(fileInputStream);
            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(bytes, fileName, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                log.info("七牛云上传返回结果putRet.key ={}",putRet.key);
                log.info("七牛云上传返回结果putRet.hash ={}",putRet.hash);

            } catch (QiniuException ex) {
                Response r = ex.response;
                log.error("七牛云上传异常:r.toString() = {}",r.toString());
                try {
                    log.error("七牛云上传异常:r.bodyString() = {}",r.bodyString());
                } catch (QiniuException ex2) {
                    throw new BussinessException("七牛云上传异常1");
                }
                throw new BussinessException("七牛云上传异常2");
            }
        } catch (Exception ex) {
            throw new BussinessException("七牛云上传异常3");
        }
    }
}