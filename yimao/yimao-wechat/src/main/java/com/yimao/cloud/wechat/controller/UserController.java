package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.user.UserAliasDTO;
import com.yimao.cloud.pojo.dto.user.UserBindDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.wechat.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/7/22.
 */
@RestController
@Slf4j
@Api(tags = "UserController")
public class UserController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private DomainProperties domainProperties;

    /**
     * 根据用户id获取用户信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/{id}")
    @ApiOperation(value = "通过用户ID获取用户信息", notes = "通过用户ID获取用户信息")
    @ApiImplicitParam(name = "id", value = "用户e家号", required = true, dataType = "Long", paramType = "path")
    public Object get(@PathVariable(name = "id") Integer id) {
        UserDTO dto = userFeign.getUserDTOById(id);
        if (dto.getId() == -1) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据用户id获取用户信息（用于分享，获取用户信息。 不验证权限）
     *
     * @param id id
     */
    @GetMapping(value = "/user/get")
    @ApiOperation(value = "通过用户ID获取用户信息")
    @ApiImplicitParam(name = "id", value = "用户e家号", required = true, dataType = "Long", paramType = "query")
    public Object getUserInfoByShare(@RequestParam Integer id) {
        UserDTO user = userFeign.getBasicUserById(id);
        if (user == null) {
            return null;
        }
        String headImg = domainProperties.getWechat() + user.getHeadImg();
        String nickName = user.getNickName();
        Map<String, String> result = new HashMap<>();
        result.put("headImg", headImg);
        result.put("nickName", nickName);
        return result;
    }

    /**
     * 根据用户id更新用户信息
     *
     * @param userDTO
     * @return
     */
    @PutMapping(value = "/user")
    @ApiOperation(value = "通过用户ID获取用户信息，设置nickName后更新用户信息", notes = "通过用户ID获取用户信息，设置nickName后更新用户信息")
    @ApiImplicitParam(name = "userDTO", value = "用户信息", required = true, dataType = "UserDTO", paramType = "body")
    public void update(@RequestBody UserDTO userDTO) {
        userFeign.update(userDTO);
    }

    /**
     * 新：根据用户id：生成分享二维码
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/user/share/{id}/qrcode")
    @ApiOperation(value = "生成分享二维码", notes = "生成分享二维码")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long", paramType = "path")
    public String buildShareQRCode(@PathVariable("id") Integer id) {
        String qrcode = userFeign.getQRCodeWithParam(id);
        if (StringUtil.isEmpty(qrcode)) {
            throw new BadRequestException("生成二维码失败！");
        }
        return qrcode;
    }

    /**
     * 绑定经销商账户
     *
     * @param userBindDTO 绑定信息
     * @return ResponseEntity
     * @author hhf
     * @date 2019/4/16
     */
    @PostMapping(value = "/user/bind/distributor")
    @ApiOperation(value = "绑定经销商账户", notes = "绑定经销商账户")
    @ApiImplicitParam(name = "userBindDTO", value = "绑定信息", required = true, dataType = "UserBindDTO", paramType = "body")
    public UserDTO bindDistributor(@RequestBody UserBindDTO userBindDTO) {
        return userFeign.bindDistributor(userBindDTO);
    }

    /**
     * 绑定手机号
     *
     * @param userBindDTO 绑定信息
     * @return org.springframework.http.ResponseEntity<com.yimao.cloud.pojo.dto.user.UserDTO>
     * @author hhf
     * @date 2019/4/17
     */
    @PostMapping(value = "/user/bind/phone")
    @ApiOperation(value = "绑定手机号", notes = "绑定手机号")
    @ApiImplicitParam(name = "userBindDTO", value = "绑定信息", required = true, dataType = "UserBindDTO", paramType = "body")
    public UserDTO bindPhone(@RequestBody UserBindDTO userBindDTO) {
        return userFeign.bindPhone(userBindDTO);
    }

    /**
     * 获取我的推广客户列表（企业版主账号）
     *
     * @param userId 用户ID
     * @return ResponseEntity
     * @author hhf
     * @date 2019/4/22
     */
    @GetMapping(value = "user/my/customers/company")
    @ApiOperation(value = "获取我的推广客户列表（企业版主账号）", notes = "获取我的推广客户列表（企业版主账号）")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    public List<UserDTO> myCustomersCompany(@RequestParam Integer userId) {
        return userFeign.findShareCustomers(userId);
    }

    /**
     * 获取我的推广客户列表
     *
     * @param userId 用户ID
     * @return Map
     * @author hhf
     * @date 2019/4/22
     */
    @GetMapping(value = "/user/my/customers")
    @ApiOperation(value = "获取我的推广客户列表", notes = "获取我的推广客户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商ID", dataType = "Long", paramType = "query")
    })
    public Map<String, Object> myCustomers(@RequestParam(required = false) Integer userId, @RequestParam(required = false) Integer distributorId) {
        return userFeign.myCustomers(userId, distributorId);
    }

    /**
     * 获取我的推广客户数量
     *
     * @param userId 用户ID
     * @return Integer
     * @author hhf
     * @date 2019/4/24
     */
    @GetMapping(value = "/user/my/customers/count")
    @ApiOperation(value = "获取我的推广客户数量", notes = "获取我的推广客户数量")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    public Integer myCustomersCount(@RequestParam Integer userId) {
        return userFeign.myCustomersCount(userId);
    }

    /**
     * 修改用户星级
     *
     * @param userAliasDTO 用户星级信息
     * @return void
     * @author hhf
     * @date 2019/4/24
     */
    @PostMapping(value = "/user/asterisk")
    @ApiOperation(value = "修改用户星级", notes = "修改用户星级")
    @ApiImplicitParam(name = "userAliasDTO", value = "用户ID", required = true, dataType = "UserAliasDTO", paramType = "body")
    public void asterisk(@RequestBody UserAliasDTO userAliasDTO) {
        userFeign.asterisk(userAliasDTO);
    }

    /**
     * 我的健康大使
     *
     * @param userId 用户ID
     * @return com.yimao.cloud.pojo.dto.user.UserDTO
     * @author hhf
     * @date 2019/4/28
     */
    @GetMapping(value = "/user/ambassador")
    @ApiOperation(value = "我的健康大使", notes = "我的健康大使")
    @ApiImplicitParam(name = "userId", value = "健康大使", dataType = "Integer", paramType = "query")
    public UserDTO ambassador(@RequestParam Integer userId) {
        return userFeign.getAmbassador(userId);
    }

}
