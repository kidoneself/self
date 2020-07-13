package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.system.GoodsCategoryDTO;
import com.yimao.cloud.pojo.dto.system.GoodsMaterialsDTO;
import com.yimao.cloud.pojo.query.system.StationCompanyStoreHouseQuery;
import com.yimao.cloud.pojo.query.system.StationStoreHouseQuery;
import com.yimao.cloud.station.constant.StationConstant;
import com.yimao.cloud.station.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 库存
 *
 * @author Liu Long Jie
 * @date 2020-6-17
 */
@RestController
@Slf4j
@Api(tags = "StoreHouseController")
public class StoreHouseController {

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private UserCache userCache;

    /**
     * 分页查询服务站公司库存
     */
    @PostMapping(value = "/store/house/stationCompany/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询服务站公司库存", notes = "分页查询服务站公司库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationCompanyStoreHouseQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageStationCompanyStoreHouse(@RequestBody StationCompanyStoreHouseQuery query,
                                               @PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize) {
        Integer stationCompanyId = userCache.getJWTInfo().getStationCompanyId();
        if (stationCompanyId == StationConstant.SUPERCOMPANYID) {
            //超级管理员
            return null;
        }
        query.setStationCompanyId(stationCompanyId);
        return systemFeign.pageStationCompanyStoreHouse(query, pageNum, pageSize);
    }

    /**
     * 分页查询服务站门店库存
     */
    @PostMapping(value = "/store/house/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询服务站门店库存", notes = "分页查询服务站门店库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StationStoreHouseQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageStationStoreHouse(@RequestBody StationStoreHouseQuery query,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize) {
        if (CollectionUtil.isEmpty(query.getStationIds())) {
            Set<Integer> stationIds = userCache.getStationUserAreas(0, null);
            query.setStationIds(stationIds);
        }
        return systemFeign.pageStationStoreHouse(query, pageNum, pageSize);
    }

    @GetMapping("/goods/category/filterItem")
    @ApiOperation(value = "库存物资分类筛选项")
    @ApiImplicitParam(name = "type", value = "类型", dataType = "Long", paramType = "query")
    public List<GoodsCategoryDTO> getGoodsCategoryFilter(@RequestParam(value = "type", required = false) Integer type) {

        return systemFeign.getGoodsCategoryFilter(type);
    }

    @GetMapping("/goods/filterItem/{goodCategoryId}")
    @ApiOperation(value = "根据物资分类筛选项获取物资列表")
    @ApiImplicitParam(name = "goodCategoryId", value = "物资分类id", dataType = "Long", paramType = "path")
    public List<GoodsMaterialsDTO> getGoodsByCategoryId(@PathVariable(value = "goodCategoryId") Integer goodCategoryId) {

        return systemFeign.getGoodsByCategoryId(goodCategoryId);

    }
}
