package com.shanjupay.merchant.service;/**
 * Created by Administrator on 2020/7/7.
 */


import com.shanjupay.common.domain.BussinessException;

/**
 *  操作文件接口
 *
 * @author rp
 * @create 2020/7/7
 */
public interface FileService {


    /**
     * 上传文件
     * @param bytes
     * @param fileName
     * @return
     * @throws BussinessException
     */
    String upload(byte[] bytes,String fileName) throws BussinessException;
}
