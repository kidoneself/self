package com.yimao.cloud.user.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.user.CustomerAddressDTO;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.user.po.UserAddress;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liuhao@yimaokeji.com
 * 2017112017/11/14
 */
public interface UserAddressMapper extends Mapper<UserAddress> {

    /**
     * 收货地址数量
     *
     * @param userId e家号
     * @return int
     */
    Integer countAddress(@Param(value = "userId") Integer userId);

    /**
     * 收货地址列表
     *
     * @param userId
     * @return
     */
    Page<UserAddressDTO> listAddress(@Param("userId") Integer userId);

    void cancelDefaultAddressByUserId(@Param("userId") Integer userId);

    List<Integer> getAllIdByUserId(@Param("userId") Integer userId);

    Page<CustomerAddressDTO> pageQueryCustomerAddress(@Param("userId") Integer userId);
}
