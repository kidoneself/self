package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.EffectStatisticsDTO;
import com.yimao.cloud.pojo.dto.water.VersionStatisticsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.service.StatisticsService;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述：数据统计
 */
@RestController
@Api(tags = "StatisticsController")
@Slf4j
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;


    @GetMapping(value = "/statistics/version/detail/{pageNum}/{pageSize}")
    @ApiOperation(value = "版本统计更新详情", notes = "版本统计更新详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "areaId", value = "区域ID，多级只需传最下级的区域ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "model", value = "水机型号，选'全部'时不传值", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "deviceGroup", value = "设备组：1-用户组，2-服务站组", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "snCode", value = "sn编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public Object queryVersionDetailList(@RequestParam(value = "areaId", required = false) Integer areaId,
                                         @RequestParam(value = "model", required = false) String model,
                                         @RequestParam(value = "version") String version,
                                         @RequestParam(value = "deviceGroup") Integer deviceGroup,
                                         @RequestParam(value = "snCode", required = false) String snCode,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize) {
        return statisticsService.queryVersionDetailList(areaId, model, version,deviceGroup,snCode, pageNum, pageSize);
    }


    @GetMapping(value = "/statistics/effect/snCode/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据sn查询效果统计详情", notes = "根据sn查询效果统计详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "sn编码",required = true,dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long",defaultValue ="1",required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long",defaultValue ="10", required = true, paramType = "path")
    })
    public Object queryStatisticsListBySn(@RequestParam(value = "snCode") String snCode,
                                         @RequestParam(value = "startTime", required = false) Date startTime,
                                         @RequestParam(value = "endTime", required = false) Date endTime,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<EffectStatisticsDTO> list = statisticsService.queryEffectListBySn(snCode,startTime,endTime, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/statistics/effect/snCode/count")
    @ApiOperation(value = "根据sn查询效果统计详情", notes = "根据sn查询效果统计详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "sn编码",required = true,dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query")
    })
    public Object queryStatisticsCountBySn(@RequestParam(value = "snCode") String snCode,
                                          @RequestParam(value = "startTime", required = false) Date startTime,
                                          @RequestParam(value = "endTime", required = false) Date endTime) {
        EffectStatisticsDTO dto = statisticsService.queryEffectCountBySn(snCode,startTime,endTime);
        return ResponseEntity.ok(dto);
    }


    @GetMapping(value = "/statistics/version")
    @ApiOperation(value = "版本统计", notes = "版本统计")
    public Object queryVersionList() {
        List<VersionStatisticsDTO> list = statisticsService.queryVersionList();
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/statistics/traffic/yesterday")
    @ApiOperation(value = "效果统计---昨日数据(区分第三方和自有)", notes = "效果统计---昨日数据(区分第三方和自有)")
    public Object queryEffectCountByYesterday() {
        Map<String, Object> map = statisticsService.queryEffectCountByYesterday();
        return ResponseEntity.ok(map);
    }

    @GetMapping(value = "/statistics/effect/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询效果统计列表", notes = "查询效果统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "screenLocation", value = "大小屏,1-大屏；2-小屏", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "materielName", value = "物料名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public Object queryEffectListByCondition(@RequestParam(name = "platform", required = false) Integer platform,
                                             @RequestParam(name = "screenLocation", required = false) Integer screenLocation,
                                             @RequestParam(name = "materielName", required = false) String materielName,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<EffectStatisticsDTO> list = statisticsService.queryEffectListByCondition(platform, screenLocation, materielName, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }


    @GetMapping(value = "/statistics/detail/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询效果统计--展示详情", notes = "查询效果统计列表--展示详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materielId", value = "物料ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "advertisingId", value = "投放配置ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adslotId", value = "广告位编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "投放开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "投放结束时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public Object queryDetail(@RequestParam(name = "materielId", required = false) Integer materielId,
                              @RequestParam(name = "advertisingId") Integer advertisingId,
                              @RequestParam(name = "adslotId", required = false) String adslotId,
                              @RequestParam(name = "beginTime", required = false) Date beginTime,
                              @RequestParam(name = "endTime", required = false) Date endTime,
                              @PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<EffectStatisticsDTO> list = statisticsService.queryDetail(materielId, advertisingId, adslotId, beginTime, endTime, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }


    @GetMapping(value = "/statistics/effect/detail/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询效果统计--每日展示详情", notes = "查询效果统计列表--每日展示详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materielId", value = "物料ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "advertisingId", value = "投放配置ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adslotId", value = "广告位编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "time", value = "投放时间", dataType = "Date", required = true, paramType = "query"),
            @ApiImplicitParam(name = "snCode", value = "设备编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path")
    })
    public Object queryEffectDetail(@RequestParam(name = "materielId", required = false) Integer materielId,
                                    @RequestParam(name = "advertisingId") Integer advertisingId,
                                    @RequestParam(name = "adslotId", required = false) String adslotId,
                                    @RequestParam(name = "time") Date time,
                                    @RequestParam(name = "snCode", required = false) String snCode,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<EffectStatisticsDTO> list = statisticsService.queryEffectDetail(materielId, advertisingId, adslotId, time, snCode, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }


    @GetMapping(value = "/statistics/detail/{advertisingId}")
    @ApiOperation(value = "查询效果统计--通过投放配置ID查询详情", notes = "查询效果统计列表--通过投放配置ID查询详情")
    @ApiImplicitParam(name = "advertisingId", value = "投放配置ID", dataType = "Long", required = true, paramType = "path")
    public Object queryEffectDetailByAdvertising(@PathVariable(name = "advertisingId") Integer advertisingId) {
        EffectStatisticsDTO dto = statisticsService.queryEffectDetailByAdvertising(advertisingId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/statistics/effect/export")
    @ApiOperation(value = "导出效果统计列表", notes = "导出效果统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", value = "第三方广告平台", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "screenLocation", value = "大小屏,1-大屏；2-小屏", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "materielName", value = "物料名称", dataType = "String", paramType = "query")
    })
    public Object exportEffect(@RequestParam(name = "platform", required = false) Integer platform,
                               @RequestParam(name = "position", required = false) Integer screenLocation,
                               @RequestParam(name = "materielName", required = false) String materielName,
                               HttpServletResponse response) {
        statisticsService.exportEffect(platform, screenLocation, materielName, response);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/statistics/effect/detail/export")
    @ApiOperation(value = "导出效果统计--展示详情", notes = "导出效果统计列表--展示详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materielId", value = "物料ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "advertisingId", value = "投放配置ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adslotId", value = "广告位编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "投放开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "投放结束时间", dataType = "Date", paramType = "query")
    })
    public Object exportEffectDetail(@RequestParam(name = "materielId", required = false) Integer materielId,
                                     @RequestParam(name = "advertisingId") Integer advertisingId,
                                     @RequestParam(name = "adslotId", required = false) String adslotId,
                                     @RequestParam(name = "beginTime", required = false) Date beginTime,
                                     @RequestParam(name = "endTime", required = false) Date endTime,
                                     HttpServletResponse response) {
        statisticsService.exportEffectDetail(materielId, advertisingId, adslotId, beginTime, endTime, response);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/statistics/effect/day/export")
    @ApiOperation(value = "导出效果统计--每日展示详情", notes = "导出效果统计列表--每日展示详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materielId", value = "物料ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "advertisingId", value = "投放配置ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adslotId", value = "广告位编码", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "time", value = "投放时间", dataType = "Date", required = true, paramType = "query"),
            @ApiImplicitParam(name = "snCode", value = "设备编码", dataType = "String", paramType = "query")
    })
    public Object exportEffectDetailByDay(@RequestParam(name = "materielId", required = false) Integer materielId,
                                          @RequestParam(name = "advertisingId") Integer advertisingId,
                                          @RequestParam(name = "adslotId", required = false) String adslotId,
                                          @RequestParam(name = "time") Date time,
                                          @RequestParam(name = "snCode", required = false) String snCode,
                                          HttpServletResponse response) {
        statisticsService.exportEffectDetailByDay(materielId, advertisingId, adslotId, time, snCode, response);
        return ResponseEntity.noContent().build();
    }

}
