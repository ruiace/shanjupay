package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

/**
 * Created by Administrator.
 */
public interface MerchantService {

    //根据 id查询商户
    MerchantDTO queryMerchantById(Long id);

    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO);


    /**
     * 商户资质申请
     * @param merchantDTO
     */
    void applyMerchant(MerchantDTO merchantDTO);


    /**
     * 新增商户
     * @param storeDTO
     * @return
     * @throws BussinessException
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BussinessException;


    /**
     * 新增员工
     * @param dto
     * @return
     * @throws BussinessException
     */
    StaffDTO createStaff(StaffDTO dto) throws BussinessException;

    /**
     * 为门店设置管理员
     * @param staffId
     * @param storeId
     * @throws BussinessException
     */
    void bindStaffToStore(Long staffId,Long storeId) throws BussinessException;


    /**
     * 分页条件查询商户下门店
     * @param storeDTO
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BussinessException
     */
    PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO,Integer pageNo, Integer pageSize) throws BussinessException;


    /**
     * 查询某个门店是否舒服某个商户
     * @param storeId
     * @param merchantId
     * @return
     */
    Boolean queryStoreInMerchant(Long storeId,Long merchantId);

}
