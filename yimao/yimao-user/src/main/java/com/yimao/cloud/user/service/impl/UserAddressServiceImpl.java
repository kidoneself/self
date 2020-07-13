package com.yimao.cloud.user.service.impl;


import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.CompanyCustomerDTO;
import com.yimao.cloud.pojo.dto.user.PersonCustomerDTO;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.mapper.CompanyCustomerMapper;
import com.yimao.cloud.user.mapper.PersonCustomerMapper;
import com.yimao.cloud.user.mapper.UserAddressMapper;
import com.yimao.cloud.user.po.CompanyCustomer;
import com.yimao.cloud.user.po.PersonCustomer;
import com.yimao.cloud.user.po.UserAddress;
import com.yimao.cloud.user.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * created by liuhao@yimaokeji.com
 * 2017112017/11/23
 */
@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;
    @Resource
    private PersonCustomerMapper personCustomerMapper;
    @Resource
    private CompanyCustomerMapper companyCustomerMapper;

    /**
     * 查询当前用户的所有收货地址
     */
    @Override
    public PageVO<UserAddressDTO> listAddress(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageVO<>(pageNum, userAddressMapper.listAddress(userId));
    }

    /**
     * 添加收货地址
     *
     * @return
     */
    @Override
    public void saveAddress(UserAddressDTO dto) {
        UserAddress newAddress = new UserAddress(dto);
        if (Objects.nonNull(dto.getDefaultAddress()) && dto.getDefaultAddress()) {
            UserAddress address = new UserAddress();
            address.setUserId(dto.getUserId());
            address.setDefaultAddress(true);
            UserAddress userAddress = userAddressMapper.selectOne(address);
            if (null != userAddress) {
                userAddress.setDefaultAddress(false);
                userAddressMapper.updateByPrimaryKeySelective(userAddress);
            }
            newAddress.setDefaultAddress(true);
        } else {
            newAddress.setDefaultAddress(false);
        }
        newAddress.setCreateTime(new Date());
        if (StringUtil.isNotEmpty(newAddress.getStreet())) {
            newAddress.setStreet(newAddress.getStreet().trim().replaceAll("\n", ""));
        }
        userAddressMapper.insertSelective(newAddress);
    }

    /**
     * 修改收货地址
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateAddress(UserAddressDTO dto) {
        if (dto.getDefaultAddress() != null && dto.getDefaultAddress()) {
            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(dto.getUserId());
            userAddress.setDefaultAddress(true);
            UserAddress updateAddress = userAddressMapper.selectOne(userAddress);
            if (null != updateAddress) {
                updateAddress.setDefaultAddress(false);
                userAddressMapper.updateByPrimaryKeySelective(updateAddress);
            }
        }

        dto.setUpdateTime(new Date());
        UserAddress address = new UserAddress(dto);
        if (StringUtil.isNotEmpty(address.getStreet())) {
            address.setStreet(address.getStreet().trim().replaceAll("\n", ""));
        }
        userAddressMapper.updateByPrimaryKeySelective(address);
    }

    /**
     * 删除地址(逻辑删除)
     */
    @Override
    public void deleteAddress(Integer id) {
        int result = userAddressMapper.deleteByPrimaryKey(id);
        if (result < 1) {
            throw new YimaoException("操作失败。");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateDefault(Integer addressId) {
        UserAddress address = userAddressMapper.selectByPrimaryKey(addressId);
        if (address != null) {
            log.info("是否为默认:" + address.getDefaultAddress());
            //修改该用户下的所有地址为非默认
            userAddressMapper.cancelDefaultAddressByUserId(address.getUserId());
            address.setDefaultAddress(true);//0:false 1:true
            userAddressMapper.updateByPrimaryKeySelective(address);
        }
    }

    @Override
    public UserAddressDTO findAddressById(Integer id) {
        UserAddress shopAddress = userAddressMapper.selectByPrimaryKey(id);
        if (shopAddress != null) {
            UserAddressDTO dto = new UserAddressDTO();
            shopAddress.convert(dto);
            return dto;
        }
        return null;
    }

    /**
     * 获取用户的默认地址
     *
     * @param userId userId
     * @return
     */
    @Override
    public UserAddressDTO getDefaultAddress(Integer userId) {
        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("defaultAddress", 1);
        criteria.andEqualTo("deleted", 0);
        List<UserAddress> addressList = userAddressMapper.selectByExample(example);
        UserAddress address;
        UserAddressDTO addressDTO;
        if (CollectionUtil.isNotEmpty(addressList)) {
            address = addressList.get(0);
            addressDTO = new UserAddressDTO();
            address.convert(addressDTO);
            return addressDTO;
        }
        return null;
    }

    @Override
    public Integer countAddress(Integer userId) {
        return userAddressMapper.countAddress(userId);
    }

    @Override
    public UserAddressDTO getUserAddressById(Integer id) {
        UserAddress userAddress = userAddressMapper.selectByPrimaryKey(id);
        if (null == userAddress) {
            throw new NotFoundException("userAddress为空");
        }
        UserAddressDTO dto = new UserAddressDTO();
        userAddress.convert(dto);
        return dto;
    }

    @Override
    public Object getAddressByType(Integer id, Integer type) {
        Map<String, Object> map = new HashMap<>(8);
        if (type == 1) {
            UserAddress userAddress = userAddressMapper.selectByPrimaryKey(id);
            if (null == userAddress) {
                throw new NotFoundException("不存在该地址");
            }
            UserAddressDTO dto = new UserAddressDTO();
            userAddress.convert(dto);
            map.put("address", dto);
        } else if (type == 2) {
            PersonCustomer personCustomer = new PersonCustomer();
            personCustomer.setId(id);
            personCustomer.setDeleted(false);
            PersonCustomer customer = personCustomerMapper.selectOne(personCustomer);
            if (null == customer) {
                throw new NotFoundException("不存在该客户地址");
            }
            PersonCustomerDTO customerDTO = new PersonCustomerDTO();
            customer.convert(customerDTO);
            map.put("address", customerDTO);
        } else if (type == 3) {
            CompanyCustomer companyCustomer = new CompanyCustomer();
            companyCustomer.setId(id);
            companyCustomer.setDeleted(false);
            CompanyCustomer customer = companyCustomerMapper.selectOne(companyCustomer);
            if (null == customer) {
                throw new NotFoundException("不存在该企业客户地址");
            }
            CompanyCustomerDTO companyCustomerDTO = new CompanyCustomerDTO();
            customer.convert(companyCustomerDTO);
            map.put("address", companyCustomerDTO);
        }
        return map;
    }
}
