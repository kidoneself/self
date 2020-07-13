package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.system.StationGeneralSituationDTO;
import com.yimao.cloud.system.service.StationGeneralSituationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/2/13
 */

@RestController
@Slf4j
@Api(tags = "StationGeneralSituationController")
public class StationGeneralSituationController {

    @Resource
    private StationGeneralSituationService stationGeneralSituationService;

    /**
     * 服务站模块概况
     *
     * @return
     */
    @GetMapping("/station/general/situation")
    @ApiOperation(value = "服务站模块概况")
    public Object getStationGeneralSituation() {
        StationGeneralSituationDTO dto = stationGeneralSituationService.getStationGeneralSituation();
        return ResponseEntity.ok(dto);
    }


    /**
     * 根据天数获取服务站模块变化趋势
     *
     * @return
     */
    @GetMapping("/station/variation/trend/{days}")
    @ApiOperation(value = "根据天数获取服务站模块变化趋势")
    @ApiImplicitParam(name = "days", value = "days", defaultValue = "7", dataType = "Long", paramType = "path")
    public Object getStationVariationTrend(@PathVariable Integer days) {
        List<Map<String, Integer>> companyNumberByDays = stationGeneralSituationService.getStationVariationTrend(days);
        return ResponseEntity.ok(companyNumberByDays);
    }

}
