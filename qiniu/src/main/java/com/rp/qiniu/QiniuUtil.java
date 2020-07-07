package com.rp.qiniu;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author rp
 * @create 2020/7/7
 */
public class QiniuUtil {

    public static  void  upload() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
         //...生成上传凭证，然后准备上传

        String accessKey = "6PI0-_PwTXk3a7phghsjvyeOd8i8y7E4B8N13PJS";
        String secretKey = "CVmfQHAJVAnBjSqOVntptzDxGfeI-cgSxfc0DKGp";
        String bucket = "ruiace";

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID().toString()+".jpg";
        System.out.println("key----------------->" + key);
        FileInputStream fileInputStream;
        try {
             fileInputStream = new FileInputStream(new File("D:\\test.jpg"));
             byte[] uploadBytes = IOUtils.toByteArray(fileInputStream);
            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(uploadBytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                System.out.println("------------------" + "http://qd3lavd0k.bkt.clouddn.com/" + key);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }

    }
}