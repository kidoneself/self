package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.app.feign.WaterFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 描述：翼猫APP-【我的】-【水机续费】
 *
 * @Author Zhang Bo
 * @Date 2019/8/27
 */
@RestController
@Api(tags = "WaterDeviceRenewController")
public class WaterDeviceRenewController {

    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;
    @Resource
    private WaterFeign waterFeign;

    /**
     * 翼猫APP-我的-水机续费-查询列表
     */
    @GetMapping(value = "/my/waterdevice/{pageNum}/{pageSize}")
    @ApiOperation(value = "翼猫APP-我的-水机续费-查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "column", value = "栏目：10-新安装；20-待续费；30-已续费；", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "当前登录e家号", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "pageNum", value = "页数", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "Long", paramType = "path", required = true)
    })
    public Object page(@RequestParam Integer column, @RequestParam Integer userId,
                       @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        Integer distributorId = getDistributorId(userId);
        if (distributorId == null) {
            return null;
        }
        return waterFeign.listDeviceWithRenewInfo(column, distributorId, pageNum, pageSize);
    }

    /**
     * 翼猫APP-我的-水机续费-续费记录
     */
    @GetMapping(value = "/my/waterdevice/renewrecord")
    @ApiOperation(value = "翼猫APP-我的-水机续费-续费记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "水机SN码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "userId", value = "当前登录e家号", dataType = "Long", paramType = "query", required = true)
    })
    public Object renewRecord(@RequestParam String snCode, @RequestParam Integer userId) {
        Integer distributorId = getDistributorId(userId);
        if (distributorId == null) {
            return null;
        }
        return orderFeign.renewRecord(snCode, distributorId);
    }

    /**
     * 获取经销商ID
     *
     * @param userId e家号（如果是主账号，是自己或子账号的e家号）
     */
    private Integer getDistributorId(Integer userId) {
        // 当前登录的用户信息
        UserDTO currentUser = userFeign.getBasicUserById(userCache.getUserId());
        if (!UserType.isDistributor(currentUser.getUserType())) {
            return null;
        }
        Integer currentUserId = currentUser.getId();
        if (Objects.equals(currentUserId, userId)) {
            return currentUser.getMid();
        } else {
            if (currentUser.getMid() == null) {
                return null;
            }
            List<DistributorDTO> childs = userFeign.getSonDistributorByMid(currentUser.getMid());
            if (CollectionUtil.isNotEmpty(childs)) {
                for (DistributorDTO child : childs) {
                    if (Objects.equals(child.getUserId(), userId)) {
                        return child.getId();
                    }
                }
            }
        }
        return null;
    }

}
