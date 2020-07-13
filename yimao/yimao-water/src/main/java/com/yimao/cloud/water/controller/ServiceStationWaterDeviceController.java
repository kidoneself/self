package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.enums.AreaFieldEnum;
import com.yimao.cloud.base.enums.AreaTypeEnum;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.water.AdslotDTO;
import com.yimao.cloud.pojo.dto.water.ServiceStationWaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.feign.SystemFeign;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.service.ServiceStationWaterDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 描述：广告位
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:36
 * @Version 1.0
 */
@RestController
@Api(tags = "ServiceStationWaterDeviceController")
@Slf4j
public class ServiceStationWaterDeviceController {

    @Resource
    private ServiceStationWaterDeviceService servicer;


    // /**
    //  * 同步服务站站数据到本地
    //  *
    //  * @return
    //  */
    // @PostMapping(value = "/station")
    // @ApiOperation(value = "同步服务站水机设备", notes = "同步服务站水机设备")
    // public Object save() {
    //     servicer.save();
    //     return ResponseEntity.noContent().build();
    // }


    @GetMapping(value = "/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询服务站设备数据列表", notes = "查询服务站设备数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "areaId", value = "区域ID，多级只需传最下级的区域ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "model", value = "水机型号，选'全部'时不传值", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "online", value = "水机是否在线，选'全部'时不传值", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "connectionType", value = "网络连接类型：暂时只支持查询全部，选'全部'时不传值", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "keyWord", value = "关键词搜索", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "snCode", value = "设备编码SN", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "可投开始时间", dataType = "Date", required = true, format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "可投结束时间", dataType = "Date", required = true, format = "yyyy-MM-dd HH:mm:ss", paramType = "query")
    })
    public Object queryListByCondition(@PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize,
                                       @RequestParam(value = "areaId", required = false) Integer areaId,
                                       @RequestParam(value = "model", required = false) String model,
                                       @RequestParam(value = "online", required = false) Boolean online,
                                       @RequestParam(value = "connectionType", required = false) Integer connectionType,
                                       @RequestParam(value = "keyWord", required = false) String keyWord,
                                       @RequestParam(value = "snCode", required = false) String snCode,
                                       @RequestParam(value = "beginTime") Date beginTime,
                                       @RequestParam(value = "endTime") Date endTime) {
        PageVO<ServiceStationWaterDeviceDTO> list = servicer.queryListByCondition(areaId, model, online, connectionType, keyWord,
                snCode, beginTime, endTime, pageNum, pageSize);
        return ResponseEntity.ok(list);
    }


    @GetMapping(value = "/station")
    @ApiOperation(value = "根据sn查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "snCode", value = "设备编码SN", required = true, dataType = "String", paramType = "query")
    })
    public Object selectBySn(@RequestParam(value = "snCode") String snCode) {
        ServiceStationWaterDeviceDTO list = servicer.selectBySn(snCode);
        return ResponseEntity.ok(list);
    }


}
