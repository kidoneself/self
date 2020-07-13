package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.service.UserAddressService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户地址
 * created by liuhao@yimaokeji.com
 * 2017112017/11/22
 */
@RestController
@Slf4j
public class UserAddressController {

    @Resource
    private UserAddressService addressService;
    @Resource
    private UserCache userCache;

    /**
     * 查询用户地址
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/user/address/{pageNum}/{pageSize}")
    public PageVO<UserAddressDTO> listAddress(@RequestParam("userId") Integer userId,
                                              @PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize) {
        return addressService.listAddress(userId, pageNum, pageSize);
    }

    /**
     * 新增用户地址
     *
     * @param dto 用户地址
     * @return
     */
    @PostMapping(value = "/user/address")
    public void save(@RequestBody UserAddressDTO dto) {
        addressService.saveAddress(dto);
    }


    /**
     * 修改用户地址
     *
     * @param dto 用户地址
     * @return
     */
    @PutMapping(value = "/user/address")
    public void update(@RequestBody UserAddressDTO dto) {
        addressService.updateAddress(dto);
    }

    /**
     * 删除用户地址
     *
     * @param id 用户地址id
     * @return
     */
    @RequestMapping(value = "/user/address/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        addressService.deleteAddress(id);
    }

    /**
     * 设置默认地址
     *
     * @param addressId 用户地址id
     * @return
     */
    @PatchMapping("/user/address/{addressId}")
    public void defaultAddress(@PathVariable(value = "addressId") Integer addressId) {
        addressService.updateDefault(addressId);
    }

    /**
     * 获取默认地址
     *
     * @return
     */
    @GetMapping("/user/address/default")
    public UserAddressDTO defaultList() {
        return addressService.getDefaultAddress(userCache.getUserId());
    }


    /**
     * @Description: 经销商APP-获取默认地址
     * @author ycl
     * @Create: 2019/1/16 13:48
     */
    @GetMapping("/user/address/default/{userId}")
    public UserAddressDTO getDefaultAddress(@PathVariable("userId") Integer userId) {
        log.info("***********经销商APP-获取默认地址***********");
        UserAddressDTO userAddressDTO = addressService.getDefaultAddress(userId);
        return userAddressDTO;
    }


    /**
     * 获取用户地址总数
     *
     * @return
     */
    @GetMapping("/address/count/{id}")
    @ApiOperation(value = "用户地址总数")
    public Integer shipOrderCount(@PathVariable Integer id) {
        return addressService.countAddress(id);
    }


    /**
     * @Description: 根据地址主键获取地址信息
     * @author ycl
     * @param: id
     * @Create: 2019/3/14 15:53
     */
    @GetMapping(value = "/user/address/{id}")
    public UserAddressDTO getUserAddressById(@PathVariable(value = "id") Integer id) {
        return addressService.getUserAddressById(id);
    }


    /**
     * @Author ycl
     * @Description 地址类型查询地址
     * @Date 15:20 2019/9/26
     * @Param
     **/
    @GetMapping(value = "/user/address/type/{id}")
    public Object getAddressByType(@PathVariable(value = "id") Integer id, @RequestParam(value = "type") Integer type) {
        return addressService.getAddressByType(id, type);
    }
}
