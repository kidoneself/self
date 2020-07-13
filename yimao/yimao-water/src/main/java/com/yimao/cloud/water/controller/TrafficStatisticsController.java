package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.TrafficStatisticsVO;
import com.yimao.cloud.water.service.TrafficStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 描述：流量数据统计
 */
@RestController
@Api(tags = "TrafficStatisticsController")
@Slf4j
public class TrafficStatisticsController {
    @Resource
    private TrafficStatisticsService trafficStatisticsService;
    @GetMapping(value = "/statistics/traffic")
    @ApiOperation(value = "查询流量统计列表", notes = "查询流量统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "来源：1-3G 2-wifi", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sn", value = "sn", dataType = "sn", paramType = "query")
    })
    public Object queryTrafficListByCondition(@RequestParam(name = "source", required = false) Integer source,
                                              @RequestParam(name = "sn", required = false) String sn) {
        Map<Integer, List<TrafficStatisticsDTO>> map = trafficStatisticsService.queryTrafficListByPlatform(source, sn);
        return ResponseEntity.ok(map);
    }

    @GetMapping(value = "/statistics/traffic/sn/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据sn查询设备流量统计列表", notes = "根据sn查询设备流量统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "来源：1-3G 2-wifi", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sn", value = "sn", dataType = "sn", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long",defaultValue = "1",required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long",defaultValue = "10", required = true, paramType = "path")
    })
    public Object queryTrafficListBySn(@RequestParam(name = "source", required = false) Integer source,
                                       @RequestParam(name = "sn", required = false) String sn,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<TrafficStatisticsVO> list = trafficStatisticsService.queryTrafficListBySn(source, sn, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }
}
