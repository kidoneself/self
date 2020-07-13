package com.yimao.cloud.system.controller;


import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaInfoDTO;
import com.yimao.cloud.system.po.Area;
import com.yimao.cloud.system.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 区域
 * @author: yu chunlei
 * @create: 2019-02-13 09:38:06
 **/
@RestController
@Slf4j
@Api(tags = "AreaController")
public final class AreaController {

    @Resource
    private AreaService areaService;

    /**
     * 获取所有省市区
     */
    @GetMapping(value = "/area")
    @ApiOperation(value = "获取所有省市区")
    public ResponseEntity<List<AreaDTO>> areaList() {
        return ResponseEntity.ok(areaService.areaList());
    }

    /**
     * 根据区域ID查询区域
     *
     * @param id 区域ID
     */
    @GetMapping(value = "/area/{id}")
    @ApiOperation(value = "根据区域ID查询区域")
    @ApiImplicitParam(name = "id", value = "区域ID", required = true, dataType = "Long", paramType = "path")
    public Object getById(@PathVariable("id") Integer id) {
        Area area = areaService.getById(id);
        if (area == null) {
            return ResponseEntity.noContent().build();
        }
        AreaDTO dto = new AreaDTO();
        area.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据名称查询省市区ID集合
     *
     * @param name 名称
     */
    @GetMapping(value = "/area/ids")
    @ApiOperation(value = "根据名称查询省市区ID集合", notes = "根据名称查询省市区ID集合")
    @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String", paramType = "query")
    public Object getAreaIdsByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(areaService.getAreaIdsByName(name));
    }


    /**
     * 查询所有省
     */
    @GetMapping("/area/provinces")
    @ApiOperation(value = "查询所有省")
    public List<AreaDTO> findProvince() {
        return areaService.listProvince();
    }


    /**
     * 查询所有下级区域
     */
    @GetMapping("/area/subs")
    @ApiOperation(value = "查询所有下级区域")
    @ApiImplicitParam(name = "pid", value = "父级ID", dataType = "Long", paramType = "query")
    public List<AreaDTO> findCityOrCounty(@RequestParam(value = "pid", required = false) Integer pid) {
        return areaService.listSubArea(pid);
    }

    /**
     * 根据区域ID查询区域级联信息
     *
     * @param id 区域ID
     */
    @GetMapping(value = "/area/name/{id}")
    @ApiOperation(value = "根据区域ID查询区域级联信息")
    @ApiImplicitParam(name = "id", value = "区域ID", required = true, dataType = "Long", paramType = "path")
    public AreaInfoDTO getAreaInfoById(@PathVariable("id") Integer id) {
        return areaService.getAreaInfoById(id);
    }

    /**
     * 根据省市区获取区域ID
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @GetMapping(value = "/area/getRegionIdByPCR")
    public Integer getRegionIdByPCR(@RequestParam String province, @RequestParam String city, @RequestParam String region) {
        return areaService.getRegionIdByPCR(province, city, region);
    }

}
