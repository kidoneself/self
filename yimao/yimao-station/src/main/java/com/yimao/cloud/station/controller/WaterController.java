package com.yimao.cloud.station.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.query.station.ExclusiveQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DeviceGeneralSituationVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.OrderFeign;
import com.yimao.cloud.station.feign.UserFeign;
import com.yimao.cloud.station.feign.WaterFeign;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 水机
 *
 * @author yaoweijun
 */
@RestController
public class WaterController {

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private UserFeign userFeign;

    @Resource
    private UserCache userCache;

    @Resource
    private OrderFeign orderFeign;

    /**
     * 设备--概况
     *
     * @return
     */
    @GetMapping("/water/device/station/generalSituation")
    @ApiOperation(value = "概况")
    public Object generalSituation() {
        //获取售后服务区域
        Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        //查询服务站下所有安装工id
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterServiceAreas);
        if (CollectionUtil.isEmpty(engineerIds)) {
            return new DeviceGeneralSituationVO(0, 0, 0, 0);
        }
        //设备（新安装）
        ExclusiveQuery exclusiveQuery = new ExclusiveQuery();
        exclusiveQuery.setEngineerIds(engineerIds);
        StationScheduleDTO device = waterFeign.getDeviceTotalAndNewInstallNum(exclusiveQuery);
        //工单与设备（续费）
        WorkOrderQuery orderQuery = new WorkOrderQuery();
        orderQuery.setEngineerIds(engineerIds);
        DeviceGeneralSituationVO vo = waterFeign.getStationWaterDeviceGeneralChart(exclusiveQuery);
        StationScheduleDTO order = orderFeign.getStationWorkerOrderAndRenewNum(orderQuery);
        vo.setRenewNum(order.getRenewNum());
        vo.setTotalDeviceNum(device.getTotalDeviceNum());
        vo.setYesterdayInstallNum(device.getYesterdayInstallNum());
        vo.setYesterdayRenewNum(order.getYesterdayRenewNum());
        return vo;
    }

    /**
     * 设备-- 设备管理（根据安装工查询--售后权限可查）
     *
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery,serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping("/water/device/station/after/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备管理分页查询（根据安装工查询--售后权限可查）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "StationWaterDeviceQuery", paramType = "body")
    })
    public Object afterGetWaterDeviceList(@PathVariable Integer pageNum,
                                          @PathVariable Integer pageSize,
                                          @RequestBody StationWaterDeviceQuery query) {
        //设置queryType 1-售后（根据安装工查询） 2-售前（根据经销商查询）
        query.setQueryType(1);
        return waterFeign.stationPageWaterDeviceInfo(pageNum, pageSize, query);
    }

    /**
     * 设备-- 设备管理（根据经销商查询--售前权限可查）
     *
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery,serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/water/device/station/pre/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备管理分页查询（根据经销商查询--售前权限可查）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "StationWaterDeviceQuery", paramType = "body")
    })
    public Object preGetWaterDeviceList(@PathVariable Integer pageNum,
                                        @PathVariable Integer pageSize,
                                        @RequestBody StationWaterDeviceQuery query) {
        //设置queryType 1-售后（根据安装工查询） 2-售前（根据经销商查询）
        query.setQueryType(2);
        return waterFeign.stationPageWaterDeviceInfo(pageNum, pageSize, query);
    }

    /**
     * 设备--设备管理-根据设备ID查询设备详情信息（站务系统调用）
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/water/device/station/{id}/detail")
    @ApiOperation(value = "根据设备ID查询设备详情信息（返回基本信息+扩展信息）")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, dataType = "Long", paramType = "path")
    public Object getWaterDeviceDetailById(@PathVariable("id") Integer id) {
        return waterFeign.getDetailById(id);
    }

    /**
     * 查询水机摆放位置更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/placechangerecord/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备-设备管理-详情-查询水机摆放位置更换记录（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDevicePlaceChangeRecordVO> pagePlaceChangeRecord(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                                        @RequestParam(required = false) String sn) {
        return waterFeign.pagePlaceChangeRecord(pageNum, pageSize, sn);
    }

    /***
     * 功能描述:查询水机滤芯更换记录列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备-设备管理-详情-查询水机滤芯更换记录列表", notes = "查询水机滤芯更换记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(WaterDeviceFilterChangeRecordQueryDTO queryDTO,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<WaterDeviceFilterChangeRecordDTO> page = waterFeign.waterDeviceFilterChangeRecordist(queryDTO, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * 设备-设备管理-详情-设备故障
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/faultrecord/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备-设备管理-详情-设备故障")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDeviceFaultVO> pageDeviceFault(@PathVariable Integer pageNum,
                                                      @PathVariable Integer pageSize,
                                                      @RequestParam(required = false) String sn) {
        return waterFeign.pageDeviceFault(pageNum, pageSize, sn);
    }

    /**
     * 查询水机计费方式修改记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @GetMapping(value = "/costchangerecord/station/{pageNum}/{pageSize}")
    @ApiOperation(value = "设备-设备管理-详情-查询水机计费方式修改记录（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "sn", value = "SN码", dataType = "String", paramType = "query"),
    })
    public PageVO<WaterDeviceCostChangeRecordDTO> pageCostChangeRecord(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                                       @RequestParam(required = false) String sn) {
        return waterFeign.pageCostChangeRecord(pageNum, pageSize, sn);
    }

}
