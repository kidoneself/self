package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;

/**
 * @author liuhao@yimaokeji.com
 * 2017112017/11/23
 */
public interface UserAddressService {

    PageVO<UserAddressDTO> listAddress(Integer userId, Integer pageNum, Integer pageSize);

    void saveAddress(UserAddressDTO dto);

    void updateAddress(UserAddressDTO dto);

    void deleteAddress(Integer id);

    void updateDefault(Integer id);

    UserAddressDTO findAddressById(Integer id);

    /**
     * 获取用户的默认地址
     *
     * @param userId userId
     * @return
     */
    UserAddressDTO getDefaultAddress(Integer userId);

    Integer countAddress(Integer userId);

    UserAddressDTO getUserAddressById(Integer id);

    Object getAddressByType(Integer id, Integer type);
}
