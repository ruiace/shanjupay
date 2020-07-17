package com.shanjupay.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BussinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private StoreStaffMapper storeStaffMapper;

    @Reference
    private TenantService tenantService;

    /**
     * 根据id查询商户信息
     * @param id
     * @return
     */
    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        log.info("根据id查询商户信息,返回结果={}",JSON.toJSONString(merchantDTO));
        return merchantDTO;
    }


    /**
     * 商户注册
     * @param merchantDTO
     * @return
     */
    @Override
    @Transactional
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        if(merchantDTO == null){
            throw new BussinessException(CommonErrorCode.E_100108);
        }
        if(StringUtils.isBlank(merchantDTO.getMobile())){
            throw new BussinessException(CommonErrorCode.E_100112);
        }

        if(!PhoneUtil.isMatches(merchantDTO.getMobile())){
            throw new BussinessException(CommonErrorCode.E_100109);
        }

        if(StringUtils.isBlank(merchantDTO.getUsername())){
            throw new BussinessException(CommonErrorCode.E_100110);
        }

        if(StringUtils.isBlank(merchantDTO.getPassword())){
            throw new BussinessException(CommonErrorCode.E_100111);
        }

        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMobile, merchantDTO.getMobile())
        );
        if(count > 0){
            throw new BussinessException(CommonErrorCode.E_100113);
        }


        CreateTenantRequestDTO tenantAcout = new CreateTenantRequestDTO();
        tenantAcout.setBundleCode("shanju‐merchant");
        tenantAcout.setMobile(merchantDTO.getMobile());
        tenantAcout.setName(merchantDTO.getUsername());
        tenantAcout.setPassword(merchantDTO.getPassword());
        tenantAcout.setTenantTypeCode("shanju‐merchant");
        tenantAcout.setUsername(merchantDTO.getUsername());
        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(tenantAcout);
        if(tenantAndAccount == null || tenantAndAccount.getId() == null){
            throw new BussinessException(CommonErrorCode.E_200012);
        }

        Merchant merchant1 = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getTenantId, tenantAndAccount.getId())
        );

        if(merchant1 != null && merchant1.getId() != null){
            throw new BussinessException(CommonErrorCode.E_200017);
        }

        merchantDTO.setTenantId(tenantAndAccount.getId());
        //审核状态 0-未申请,1-已申请待审核,2-审核通过,3-审核拒绝
        merchantDTO.setAuditStatus("0");
        log.info("商户注册,请求参数={}",JSON.toJSONString(merchantDTO));
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);

        int insert = merchantMapper.insert(merchant);
        MerchantDTO merchantDTONew = MerchantConvert.INSTANCE.entity2dto(merchant);
        log.info("商户注册,返回结果={}", JSON.toJSONString(merchantDTONew));


        //新增门店,创建新门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchant.getId());
        storeDTO.setStoreName("根门店");

        StoreDTO store = createStore(storeDTO);

        //新增员工 并设置归属根门店
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMerchantId(merchant.getId());
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
        staffDTO.setStoreId(store.getId());
        StaffDTO staff = createStaff(staffDTO);
        bindStaffToStore(store.getId(),staff.getId());




        return merchantDTONew;
    }


    /**
     * 商户资质申请
     * @param merchantDTO
     */
    @Override
    public void applyMerchant(MerchantDTO merchantDTO) {
        if(merchantDTO == null || merchantDTO.getId() == null){
            throw new BussinessException(CommonErrorCode.E_100108);
        }

        Merchant merchant1 = merchantMapper.selectById(merchantDTO.getId());
        if(merchant1 == null){
            throw new BussinessException(CommonErrorCode.E_200207);
        }

        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        merchant.setAuditStatus("1");
        merchant.setTenantId(merchant1.getTenantId());
        int i = merchantMapper.updateById(merchant);
    }


    /**
     * 新增商户
     * @param storeDTO
     * @return
     * @throws BussinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BussinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        storeMapper.insert(store);
        StoreDTO storeDTO1 = StoreConvert.INSTANCE.entity2dot(store);
        return storeDTO1;
    }


    /**
     * 新增员工
     * @param dto
     * @return
     * @throws BussinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO dto) throws BussinessException {

        String mobile = dto.getMobile();
        if(StringUtils.isBlank(mobile)){
            throw new BussinessException(CommonErrorCode.E_100112);
        }

        if(isExistStaffByMerchantIdAndMobile(dto.getMerchantId(),mobile)){
            throw  new BussinessException(CommonErrorCode.E_100113);
        }

        String username = dto.getUsername();
        if(StringUtils.isBlank(username)){
            throw new BussinessException(CommonErrorCode.E_110001);
        }

        if(isExistStaffByMerchantIdAndName(dto.getMerchantId(),dto.getUsername())){
            throw new BussinessException(CommonErrorCode.E_100114);
        }

        Staff staff = StaffConvert.INSTANCE.dto2entity(dto);
        staffMapper.insert(staff);
        StaffDTO staffDTO = StaffConvert.INSTANCE.entity2dto(staff);
        return staffDTO;
    }


    /**
     * 为门店设置管理员
     * @param staffId
     * @param storeId
     * @throws BussinessException
     */
    @Override
    public void bindStaffToStore(Long staffId, Long storeId) throws BussinessException {

        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStaffId(staffId);
        storeStaff.setStoreId(storeId);
        storeStaffMapper.insert(storeStaff);
    }


    /**
     * 分页条件查询商户下门店
     * @param storeDTO
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BussinessException
     */
    @Override
    public PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize) throws BussinessException {
        IPage<Store> page = new Page<Store>(pageNo,pageSize);
        LambdaQueryWrapper<Store> qw = new LambdaQueryWrapper<Store>();
        if(storeDTO != null && storeDTO.getMerchantId() != null){
            qw.eq(Store::getMerchantId,storeDTO.getMerchantId());
        }
        IPage<Store> storeIPage = storeMapper.selectPage(page, qw);
        List<StoreDTO> storeDTOS = StoreConvert.INSTANCE.listEntity2listDto(storeIPage.getRecords());
        PageVO<StoreDTO> storeDTOS1 = new PageVO<>(storeDTOS, storeIPage.getTotal(), pageNo, pageSize);
        return storeDTOS1;
    }


    /**
     * 查询某个门店是否舒服某个商户
     * @param storeId
     * @param merchantId
     * @return
     */
    @Override
    public Boolean queryStoreInMerchant(Long storeId, Long merchantId) {
        Integer count = storeMapper.selectCount(new LambdaQueryWrapper<Store>().eq(Store::getId, storeId).eq(Store::getMerchantId, merchantId));
        return count > 0;
    }

    /**
     * 判断指定员工 是否已经存在指定商户
     * @param merchantId
     * @param username
     * @return
     */
    private boolean isExistStaffByMerchantIdAndName(Long merchantId, String username) {
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getMerchantId, merchantId)
                .eq(Staff::getUsername, username)
        );

        return count > 0 ;
    }

    /**
     * 根据手机号判断员工是否已经在此商户
     * @param merchantId
     * @param mobile
     * @return
     */
    private boolean isExistStaffByMerchantIdAndMobile(Long merchantId, String mobile) {
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getMerchantId, merchantId)
                .eq(Staff::getMobile, mobile)
        );

        return count > 0 ;
    }
}
