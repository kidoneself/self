package com.yimao.cloud.station.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.query.station.ExclusiveQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DeviceGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.StationWaterDeviceVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：水机微服务的接口列表
 *
 * @author yaoweijun
 * @date 2019/12/23.
 */
@FeignClient(name = Constant.MICROSERVICE_WATER, configuration = MultipartSupportConfig.class)
public interface WaterFeign {

    /**
     * 站务系统-设备-设备管理(站务系统调用)
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/water/device/station/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<StationWaterDeviceVO> stationPageWaterDeviceInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                            @PathVariable(value = "pageSize") Integer pageSize,
                                                            @RequestBody StationWaterDeviceQuery query);

    @GetMapping(value = "/waterdevice/{id}/detail")
    WaterDeviceVO getDetailById(@PathVariable(value = "id") Integer id);

    /**
     * 统计-续费统计表格数据
     *
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/waterdevice/station/waterDeviceRenewData", consumes = MediaType.APPLICATION_JSON_VALUE)
    RenewStatisticsDTO getWaterDeviceRenewData(@RequestBody StationWaterDeviceQuery waterDeviceQuery);

    /**
     * 统计-续费统计图表数据-新安装数与应续费数
     *
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/waterdevice/station/waterDeviceRenewPicData", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<RenewStatisticsDTO> getWaterDeviceRenewPicData(@RequestBody StationWaterDeviceQuery waterDeviceQuery);

    /**
     * 控制台-待办事项（设备昨日新增与总数查询）
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/waterdevice/station/deviceTotalAndNewInstallNum", consumes = MediaType.APPLICATION_JSON_VALUE)
    StationScheduleDTO getDeviceTotalAndNewInstallNum(@RequestBody ExclusiveQuery query);

    /**
     * 水机更换位置接口
     *
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/placechangerecord/{pageNum}/{pageSize}")
    PageVO<WaterDevicePlaceChangeRecordVO> pagePlaceChangeRecord(@PathVariable("pageNum") Integer pageNum,
                                                                 @PathVariable("pageSize") Integer pageSize,
                                                                 @RequestParam(value = "sn", required = false) String sn);

    /***
     * 功能描述:查询水机滤芯更换记录列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/filterChangeRecord/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WaterDeviceFilterChangeRecordDTO> waterDeviceFilterChangeRecordist(@RequestBody WaterDeviceFilterChangeRecordQueryDTO queryDTO,
                                                                              @PathVariable("pageNum") Integer pageNum,
                                                                              @PathVariable("pageSize") Integer pageSize);

    /**
     * 查询水机故障分页
     *
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/faultrecord/{pageNum}/{pageSize}")
    PageVO<WaterDeviceFaultVO> pageDeviceFault(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "sn", required = false) String sn);

    @GetMapping(value = "/costchangerecord/{pageNum}/{pageSize}")
    PageVO<WaterDeviceCostChangeRecordDTO> pageCostChangeRecord(@PathVariable("pageNum") Integer pageNum,
                                                                @PathVariable("pageSize") Integer pageSize,
                                                                @RequestParam(value = "sn", required = false) String sn);


    /**
     * 站务系统 -- 设备 -- 概况图表数据
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/waterdevice/station/generalChart", consumes = MediaType.APPLICATION_JSON_VALUE)
    DeviceGeneralSituationVO getStationWaterDeviceGeneralChart(@RequestBody ExclusiveQuery query);
}
