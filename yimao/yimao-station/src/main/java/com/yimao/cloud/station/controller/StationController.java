package com.yimao.cloud.station.controller;


import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.StationCompanyDTO;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.query.system.StationQuery;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 服务站模块
 *
 * @author Liu Long Jie
 */
@RestController
@Api(tags = "StationController")
@Slf4j
public class StationController {
    @Resource
    private UserCache userCache;

    @Resource
    private SystemFeign systemFeign;

    /**
     * 服务站--服务站公司信息
     *
     * @return
     */
    @GetMapping("/station/company/detail")
    @ApiOperation(value = "服务站公司信息")
    public Object getStationCompanyDetail() {

        //获取用户所在服务站公司
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        } else if (stationCompanyId.equals(StationConstant.SUPERCOMPANYID)) {
            return null;
        } else {
            return systemFeign.getStationCompanyById(stationCompanyId);
        }


    }

    /**
     * 服务站--服务站门店管理
     *
     * @return
     */
    @PostMapping("/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "服务站门店管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationQuery", paramType = "body")
    })
    public Object getStationList(@PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize,
                                 @RequestBody StationQuery query) {

        //获取用户所在服务站门店
        Set<Integer> stationList = userCache.getStationUserAreas(0,null);

        if (CollectionUtil.isEmpty(stationList)) {
            return null;
        } else {
            //注，system方法查询条件中不存在使用hraOnline,无需考虑
            query.setIds(stationList);
            return systemFeign.adminStationList(pageNum, pageSize, query);
        }

    }

    /**
     * 服务站--服务站公司-修改联系人
     *
     * @return
     */
    @PostMapping("/station/company/editContactInfo")
    @ApiOperation(value = "服务站公司-修改联系人")
    @ApiImplicitParam(name = "dto", dataType = "StationCompanyDTO", paramType = "body")
    public void editStationCompanyContactInfo(@RequestBody StationCompanyDTO dto) {
        //获取用户所在服务站公司
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();

        if (stationCompanyId == null) {
            throw new BadRequestException("用户登录失效。");
        }

        if (StringUtils.isBlank(dto.getContact())) {
            throw new BadRequestException("联系人不能为空");
        }

        if (StringUtils.isBlank(dto.getContactPhone())) {
            throw new BadRequestException("联系电话不能为空");
        }

        if (StringUtils.isBlank(dto.getEmail())) {
            throw new BadRequestException("服务站邮箱不能为空");
        }

        StationCompanyDTO update = new StationCompanyDTO();
        update.setId(stationCompanyId);
        update.setContact(dto.getContact());
        update.setContactPhone(dto.getContactPhone());
        update.setEmail(dto.getEmail());

        systemFeign.editStationCompanyContactInfo(update);
    }

    /**
     * 服务站--服务站门店管理--详情--修改联系人
     *
     * @return
     */
    @PostMapping("/station/editContactInfo")
    @ApiOperation(value = "服务站门店管理--详情--修改联系人")
    @ApiImplicitParam(name = "dto", dataType = "StationCompanyDTO", paramType = "body")
    public void editStationContactInfo(@RequestBody StationDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("门店id不能为空");
        }

        if (StringUtils.isBlank(dto.getContact())) {
            throw new BadRequestException("联系人不能为空");
        }

        if (StringUtils.isBlank(dto.getContactPhone())) {
            throw new BadRequestException("联系电话不能为空");
        }

        StationDTO update = new StationDTO();
        update.setId(dto.getId());
        update.setContact(dto.getContact());
        update.setContactPhone(dto.getContactPhone());

        systemFeign.editStationContactInfo(update);
    }

}
