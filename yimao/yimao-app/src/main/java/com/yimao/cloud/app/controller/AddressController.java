package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.PhoneFormatCheckUtils;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * @description: 收货地址
 * @author: yu chunlei
 * @create: 2019-01-16 11:40:18
 **/
@RestController
@Api(tags = "AddressController")
public class AddressController {

    @Resource
    private UserFeign userFeign;


    /**
     * @param userId
     * @param pageNum
     * @param pageSize
     * @Description: 经销商APP-地址列表
     * @author ycl
     * @Create: 2019/1/16 11:59
     */
    @GetMapping("/address/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询用户地址", notes = "查询用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页数", dataType = "Long", defaultValue = "1",required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "Long",defaultValue = "10",required = true, paramType = "path")
    })
    public ResponseEntity listAddress(@RequestParam(value = "userId") Integer userId,
                                      @PathVariable(value = "pageNum") Integer pageNum,
                                      @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<UserAddressDTO> addressList = userFeign.listAddress(userId, pageNum, pageSize);
        return ResponseEntity.ok(addressList);
    }

    /**
     * @param dto
     * @Description: 经销商APP-添加新地址
     * @author ycl
     * @Create: 2019/1/16 12:09
     */
    @RequestMapping(value = "/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新增用户地址", notes = "新增用户地址")
    @ApiImplicitParam(name = "dto", value = "地址实体类", dataType = "UserAddressDTO", required = true, paramType = "body")
    public ResponseEntity save(@RequestBody UserAddressDTO dto) {
        if (!PhoneFormatCheckUtils.isPhoneLegal(dto.getMobile())) {
            throw new YimaoException("手机号输入错误");
        }
        userFeign.saveAddress(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param dto
     * @Description: 经销商APP-修改地址
     * @author ycl
     * @Create: 2019/1/16 12:10
     */
    @RequestMapping(value = "/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改用户地址", notes = "修改用户地址")
    @ApiImplicitParam(name = "dto", value = "地址实体类", dataType = "UserAddressDTO", required = true, paramType = "body")
    public ResponseEntity update(@RequestBody UserAddressDTO dto) {
        userFeign.updateAddress(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id
     * @Description: 经销商APP-删除地址
     * @author ycl
     * @Create: 2019/1/16 12:12
     */
    @RequestMapping(value = "/address/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户地址", notes = "删除用户地址")
    @ApiImplicitParam(name = "id", value = "地址ID", dataType = "Long", required = true, paramType = "path")
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        userFeign.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @Description: 经销商APP-获取默认地址
     * @author ycl
     * @Create: 2019/1/16 13:48
     */
    @GetMapping("/address/default/{userId}")
    @ApiOperation(value = "获取默认地址", notes = "获取默认地址")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "path")
    public ResponseEntity defaultAddress(@PathVariable("userId") Integer userId) {
        UserAddressDTO userAddressDTO = userFeign.getDefaultAddress(userId);
        return ResponseEntity.ok(userAddressDTO);
    }

    /**
     * @param addressId
     * @Description: 设置默认地址
     * @author ycl
     * @Create: 2019/1/17 15:55
     */
    @PatchMapping("/address/{addressId}")
    @ApiOperation(value = "设置默认地址", notes = "设置默认地址")
    @ApiImplicitParam(name = "addressId", value = "用户地址ID", dataType = "Long", required = true, paramType = "path")
    public ResponseEntity setDefaultAddress(@PathVariable(value = "addressId") Integer addressId) {
        userFeign.updateDefault(addressId);
        return ResponseEntity.noContent().build();
    }



    /**
     * @Author ycl
     * @Description 根据类型查询地址信息
     * @Date 15:36 2019/9/26
     * @Param
    **/
    @GetMapping(value = "/user/address/type/{id}")
    @ApiOperation(value = "根据类型查询地址信息", notes = "根据类型查询地址信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "type", value = "1-用户地址 2-个人客户 3-企业客户", dataType = "Long", required = true, paramType = "query")
    })
    public Object getAddressByType(@PathVariable(value = "id") Integer id,
                                       @RequestParam(value = "type") Integer type){
        return userFeign.getAddressByType(id,type);
    }


}
