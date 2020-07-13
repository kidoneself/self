package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.water.feign.SystemFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2019/4/10
 */
@RestController
@Api(tags = "AreaController")
public class AreaController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 获取所有省份
     */
    @GetMapping("/area/provinces")
    @ApiOperation(value = "查询所有省")
    public List<AreaDTO> findProvince() {
        return systemFeign.listProvince();
    }

    /**
     * 查询所有下级区域
     */
    @GetMapping("/area/subs")
    @ApiOperation(value = "查询所有下级区域")
    @ApiImplicitParam(name = "pid", value = "父级ID", dataType = "Long", paramType = "query")
    public List<AreaDTO> findCityOrCounty(@RequestParam(value = "pid",required = false) Integer pid) {
        return systemFeign.listSubArea(pid);
    }

}
