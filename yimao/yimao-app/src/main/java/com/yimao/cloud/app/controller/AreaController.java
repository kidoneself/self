package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.SystemFeign;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/3/14
 */
@Slf4j
@RestController
public class AreaController {

    @Resource
    private SystemFeign systemFeign;

    /**
     * 获取所有省份
     *
     * @return
     */
    @GetMapping("/area/provinces")
    @ApiOperation(value = "查询所有省", notes = "查询所有省")
    public ResponseEntity findProvince() {
        List<AreaDTO> areaList = systemFeign.listProvince();
        return ResponseEntity.ok(areaList);
    }


    /**
     * 获取所有省市区
     */
    @GetMapping(value = "/area")
    @ApiOperation(value = "获取所有省市区")
    public ResponseEntity<List<AreaDTO>> areaList() {
        return ResponseEntity.ok(systemFeign.areaList());
    }


}
