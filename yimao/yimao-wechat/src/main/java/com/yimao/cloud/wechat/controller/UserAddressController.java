package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.pojo.dto.user.UserAddressDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 收货地址
 * created by liuhao@yimaokeji.com
 * 2017112017/11/22
 */
@RestController
@Slf4j
@Api(tags = "UserAddressController")
public class UserAddressController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private UserCache userCache;

    /**
     * 查询用户地址
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return
     */
    @GetMapping("/user/address/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询用户地址", notes = "查询用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Long", required = true, paramType = "path"),
    })
    public Object listAddress(@PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<UserAddressDTO> pageVO = userFeign.listUserAddress(userCache.getUserId(), pageNum, pageSize);
        if (pageVO == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(pageVO);
    }

    /**
     * 新增收货地址
     *
     * @param dto 用户地址
     * @return
     */
    @PostMapping("/user/address")
    @ApiOperation(value = "新增用户地址", notes = "新增用户地址")
    @ApiImplicitParam(name = "dto", value = "用户地址", dataType = "UserAddressDTO", required = true, paramType = "body")
    public Object save(@RequestBody UserAddressDTO dto) {
        dto.setUserId(userCache.getUserId());
        Integer result = userFeign.saveUserAddress(dto);
        if (result != null && result == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改用户地址
     *
     * @param dto 用户地址
     * @return
     */
    @PutMapping("/user/address")
    @ApiOperation(value = "修改用户地址", notes = "修改用户地址")
    @ApiImplicitParam(name = "dto", value = "用户地址", dataType = "UserAddressDTO", required = true, paramType = "body")
    public Object update(@RequestBody UserAddressDTO dto) {
        // if (!UserValidator.addressValidator(dto)) {
        //     throw new BadRequestException("用户地址信息不全，请检查!");
        // }
        dto.setUserId(userCache.getUserId());
        Integer result = userFeign.updateUserAddress(dto);
        if (result != null && result == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除用户地址
     *
     * @param id 用户地址id
     */
    @DeleteMapping("/user/address/{id}")
    @ApiOperation(value = "删除用户地址", notes = "删除用户地址")
    @ApiImplicitParam(name = "id", value = "用户地址id", dataType = "Long", required = true, paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        Integer result = userFeign.deleteUserAddress(id);
        if (result != null && result == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置默认地址
     *
     * @param id 用户地址id
     * @return
     */
    @PatchMapping("/user/address/{id}")
    @ApiOperation(value = "设置默认地址", notes = "设置默认地址")
    @ApiImplicitParam(name = "id", value = "用户地址id", dataType = "Long", required = true, paramType = "path")
    public Object defaultAddress(@PathVariable(value = "id") Integer id) {
        Integer result = userFeign.setDefaultAddress(id);
        if (result != null && result == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取默认地址
     *
     * @return
     */
    @GetMapping("/user/address/default")
    @ApiOperation(value = "获取默认地址", notes = "获取默认地址")
    public Object defaultList() {
        UserAddressDTO defaultAddress = userFeign.getDefaultAddress();
        if (defaultAddress == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(defaultAddress);
    }

    /**
     * 获取用户地址总数
     *
     * @return
     */
    @GetMapping("/user/address/count")
    @ApiOperation(value = "获取用户地址总数", notes = "获取用户地址总数")
    public Object shipOrderCount() {
        userCache.getUserId();
        Integer count = userFeign.countAddress();
        if (count != null && count == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(count == null ? 0 : count);
    }

}
