package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.StationScheduleDTO;
import com.yimao.cloud.pojo.dto.water.*;
import com.yimao.cloud.pojo.query.station.ExclusiveQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.handler.ProductFeignHandler;
import com.yimao.cloud.water.mapper.WaterDeviceMapper;
import com.yimao.cloud.water.po.DeductionPlan;
import com.yimao.cloud.water.po.DeductionPlanChangeRecord;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.DeductionPlanService;
import com.yimao.cloud.water.service.WaterDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询MYSQL数据库设备相关信息。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@Slf4j
@Api(tags = "WaterDeviceController")
@RestController
public class WaterDeviceController {

    @Resource
    private ProductFeignHandler productFeignHandler;

    @Resource
    private WaterDeviceMapper waterDeviceMapper;
    @Resource
    private WaterDeviceService waterDeviceService;
    @Resource
    private DeductionPlanService deductionPlanService;

    /**
     * 创建水机设备信息
     *
     * @param dto 水机设备信息
     */
    @PostMapping(value = "/waterdevice")
    public WaterDeviceDTO save(@RequestBody WaterDeviceDTO dto) {
        WaterDevice device = new WaterDevice(dto);
        return waterDeviceService.save(device);
    }

    /**
     * 修改水机设备信息
     *
     * @param dto 水机设备信息
     */
    @PatchMapping(value = "/waterdevice")
    public void update(@RequestBody WaterDeviceDTO dto) {
        if (dto.getId() == null) {
            throw new BadRequestException("修改对象ID不能为空。");
        } else {
            waterDeviceService.update(dto);
        }
    }

    /**
     * 业务系统-水机物联-设备管理-查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/waterdevice/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody WaterDeviceQuery query) {
        return waterDeviceService.page(pageNum, pageSize, query);
    }

    /**
     * 站务系统-设备-设备管理(站务系统调用)
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/water/device/station/list/{pageNum}/{pageSize}")
    public Object stationPageWaterDeviceInfo(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody StationWaterDeviceQuery query) {
        return ResponseEntity.ok(waterDeviceService.stationPageWaterDeviceInfo(pageNum, pageSize, query));
    }

    /**
     * @return java.lang.Object
     * @description 安装工app-设备管理-查询
     * @author Liu Yi
     * @date 2019/12/13 9:31
     */
    @GetMapping(value = "/waterdevice/engineerApp/{pageNum}/{pageSize}")
    @ApiOperation(value = "安装工app-设备管理-查询", notes = "安装工app-设备管理-查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "关键词搜索", dataType = "String", paramType = "query")
    })
    public Object pageDeviceForEngineerApp(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                           @RequestParam(value = "search", required = false) String search) {
        return waterDeviceService.pageDeviceForEngineerApp(pageNum, pageSize, engineerId, search);
    }

    /**
     * 根据水机设备ID获取设备信息
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/{id}")
    public WaterDeviceDTO get(@PathVariable Integer id) {
        WaterDevice device = waterDeviceService.getById(id);
        if (device == null) {
            return null;
        }
        WaterDeviceDTO dto = new WaterDeviceDTO();
        device.convert(dto);
        return dto;
    }

    /**
     * 业务系统-水机物联-设备管理-查看详情
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/{id}/detail")
    public WaterDeviceVO getDetailById(@PathVariable Integer id) {
        return waterDeviceService.getDetailById(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-计费方式（查询现有的计费方式）
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/{id}/changecost")
    public Object getCostById(@PathVariable Integer id) {
        //获取设备上计费方式相关信息
        WaterDeviceDTO dto = waterDeviceMapper.selectByIdForChangeCost(id);
        //变更套餐后，前端展示的套餐应该是变更后的套餐信息
        if (dto.getNewCostId() != null) {
            dto.setCostId(dto.getNewCostId());
        }
        //获取可以变更的计费方式列表（含原来的计费方式）
        List<ProductCostDTO> costList = productFeignHandler.listProductCostByOldCostId(dto.getCostId());
        Map<String, Object> result = new HashMap<>();
        result.put("deviceInfo", dto);
        result.put("newCostList", costList);
        return result;
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-计费方式（修改成新的计费方式）
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/changecost")
    public void changeCostById(@PathVariable Integer id, @RequestParam Integer newCostId) {
        waterDeviceService.changeCost(id, newCostId);
    }

    /**
     * 激活SIM卡
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/activatingsimcard")
    public void activatingSimCard(@PathVariable Integer id, @RequestParam(required = false) String iccid) {
        waterDeviceService.activatingSimCard(id, iccid);
    }

    /**
     * 停用SIM卡
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/deactivatedsimcard")
    public void deactivatedSimCard(@PathVariable Integer id) {
        waterDeviceService.deactivatedSimCard(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-解除绑定
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/unbundling")
    public void unbundling(@PathVariable Integer id) {
        waterDeviceService.unbundling(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-恢复满额
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/fullamount")
    public void restoreFullAmount(@PathVariable Integer id) {
        waterDeviceService.restoreFullAmount(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-修改设备
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}")
    public void update(@PathVariable Integer id, @RequestParam String oldSn, @RequestParam String newSn, @RequestParam String oldIccid,
                       @RequestParam String newIccid, @RequestParam String oldBatchCode, @RequestParam String newBatchCode, @RequestParam(required = false) String address) {
        waterDeviceService.update(id, oldSn, newSn, oldIccid, newIccid, oldBatchCode, newBatchCode, address);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-更换设备
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/replace")
    public void replace(@RequestParam(required = false) Integer id, @RequestParam String oldSn, @RequestParam String newSn,
                        @RequestParam String oldIccid, @RequestParam String newIccid,
                        @RequestParam String oldBatchCode, @RequestParam String newBatchCode) {
        waterDeviceService.replace(id, oldSn, newSn, oldIccid, newIccid, oldBatchCode, newBatchCode);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-续费
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/renew", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "costId", value = "计费方式ID", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "filePaths", value = "附件文件路径", dataType = "String", paramType = "query", required = true),
    })
    public void renew(@PathVariable Integer id, @RequestParam Integer costId, @RequestParam(value = "payType") Integer payType, @RequestParam String filePaths, @RequestPart(required = false, value = "files") MultipartFile[] files) {
        waterDeviceService.renew(id, costId, payType, filePaths, files);
    }

    /**
     * 根据水机设备SN获取设备信息
     *
     * @param snCode 设备sn编码
     */
    @GetMapping(value = "/waterdevice")
    public WaterDeviceDTO getBySnCode(@RequestParam String sn) {
        WaterDevice device = waterDeviceService.getBySnCode(sn);
        if (device == null) {
            return null;
        }
        WaterDeviceDTO dto = new WaterDeviceDTO();
        device.convert(dto);
        return dto;
    }

    /**
     * 根据水机设备iccid获取设备信息
     *
     * @param iccid 设备sim卡号
     */
    @GetMapping(value = "/waterdevice/iccid")
    public WaterDeviceDTO getByIccid(@RequestParam("iccid") String iccid) {
        WaterDevice device = waterDeviceService.getByIccid(iccid);
        if (device == null) {
            return null;
        }
        WaterDeviceDTO dto = new WaterDeviceDTO();
        device.convert(dto);
        return dto;
    }

    /**
     * 检查sn是否存在
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/checkSnExists")
    public Object checkSnExists(@RequestParam("id") Integer id, @RequestParam("sncode") String sncode) {
        return waterDeviceService.checkSnExists(id, sncode);
    }

    /**
     * 检查sim是否存在
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/checkIccidExists")
    public Object checkIccidExists(@RequestParam Integer id, @RequestParam String iccid) {
        return waterDeviceService.checkIccidExists(id, iccid);
    }

    /**
     * 根据水机设备ID删除设备
     *
     * @param id 设备ID
     */
    @DeleteMapping(value = "/waterdevice/{id}")
    public void delete(@PathVariable(value = "id") Integer id) {
        waterDeviceService.delete(id);
    }

    /***
     * 功能描述:获取设备增长趋势
     *
     * @auther: liu yi
     * @date: 2019/7/31 15:45
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/growthTrendOverview")
    @ApiOperation(value = "获取设备增长趋势", notes = "获取设备增长趋势")
    public Object getWaterDeviceGrowthTrend() {
        Map map = waterDeviceService.getWaterDeviceGrowthTrend();
        return ResponseEntity.ok(map);
    }

    /***
     * 功能描述:获取设备概况
     *
     * @param: []
     * @auther: liu yi
     * @date: 2019/7/31 15:44
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/overview")
    @ApiOperation(value = "获取设备概况", notes = "获取设备概况")
    public Object waterDeviceOverview() {
        WaterDeviceOverviewDTO dto = waterDeviceService.waterDeviceOverview();
        return ResponseEntity.ok(dto);
    }

    /**
     * 站务系统--设备--概况图表数据
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/waterdevice/station/generalChart")
    public Object getStationWaterDeviceGeneralChart(@RequestBody ExclusiveQuery query) {
        if (query.getEngineerIds() == null) {
            return null;
        }
        return ResponseEntity.ok(waterDeviceService.getStationWaterDeviceGeneralChart(query.getEngineerIds()));
    }


    /**
     * 更新水机设备续费信息
     */
    @PatchMapping(value = "/waterdevice/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void renewProcessor(@RequestBody WaterDeviceDTO device) {
        if (StringUtil.isBlank(device.getSn())) {
            throw new BadRequestException("设备SN码不能为空。");
        } else {
            waterDeviceService.renewProcessor(device);
        }
    }

    /**
     * 获取设备的扣费计划
     */
    @GetMapping(value = "/waterdevice/deductionPlans")
    public List<DeductionPlanDTO> listDeductionPlan(@RequestParam Integer deviceId) {
        List<DeductionPlan> list = deductionPlanService.listByDeviceId(deviceId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        List<DeductionPlanDTO> dtoList = CollectionUtil.batchConvert(list, DeductionPlan.class, DeductionPlanDTO.class);
        if (CollectionUtil.isNotEmpty(dtoList)) {
            for (DeductionPlanDTO dto : dtoList) {
                ProductCostDTO cost = productFeignHandler.getProductCostById(dto.getCostId());
                if (cost != null) {
                    dto.setCostName(cost.getName());
                }
            }
        }
        return dtoList;
    }

    /**
     * 获取设备的扣费计划修改记录
     */
    @GetMapping(value = "/waterdevice/deductionPlan/changeRecords")
    public List<DeductionPlanChangeRecordDTO> listDeductionPlanChangeRecord(@RequestParam Integer deviceId) {
        List<DeductionPlanChangeRecord> list = deductionPlanService.listChangeRecordByDeviceId(deviceId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return CollectionUtil.batchConvert(list, DeductionPlanChangeRecord.class, DeductionPlanChangeRecordDTO.class);
    }

    /**
     * 手动修改扣费计划
     */
    @PostMapping(value = "/waterdevice/changePlan")
    public void changePlan(@RequestParam Integer planId) {
        deductionPlanService.changePlan(planId);
    }

    /**
     * 根据经销商id查询经销商所售水机数量
     *
     * @param distributorId
     * @return
     */
    @GetMapping(value = "/waterdevice/count/{distributorId}")
    public Object getWaterDeviceCountByDistributorId(@PathVariable("distributorId") Integer distributorId) {
        WaterDevice query = new WaterDevice();
        query.setDistributorId(distributorId);
        return ResponseEntity.ok(waterDeviceMapper.selectCount(query));
    }

    /**
     * 站务系统-统计-续费统计（表格数据）
     *
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/waterdevice/station/waterDeviceRenewData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RenewStatisticsDTO getWaterDeviceRenewData(@RequestBody StationWaterDeviceQuery waterDeviceQuery) {
        return waterDeviceService.getWaterDeviceRenewData(waterDeviceQuery);

    }

    /**
     * 站务系统-统计-续费统计-图表数据（新安装+应续费）
     * 这里应续费指代当前为止还未续费的水机
     *
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/waterdevice/station/waterDeviceRenewPicData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<RenewStatisticsDTO> getWaterDeviceRenewPicData(@RequestBody StationWaterDeviceQuery waterDeviceQuery) {
        return waterDeviceService.getWaterDeviceRenewPicData(waterDeviceQuery);
    }

    /**
     * 站务系统-控制台-待办事项（设备昨日新增与总数查询）
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/waterdevice/station/deviceTotalAndNewInstallNum")
    public StationScheduleDTO getDeviceTotalAndNewInstallNum(@RequestBody ExclusiveQuery query) {
        if (query.getEngineerIds() == null) {
            return null;
        }
        Integer totalNum = waterDeviceService.getDeviceTotalNum(query.getEngineerIds());

        Integer newInstallNum = waterDeviceService.getYesterdayNewInstallNum(query.getEngineerIds());

        StationScheduleDTO dto = new StationScheduleDTO();

        dto.setTotalDeviceNum(totalNum);
        dto.setYesterdayInstallNum(newInstallNum);

        return dto;
    }

    /****
     * 安装工转让，更新水机上的安装工信息
     * @param wdds
     */
    @PostMapping(value = "/waterdevice/engineer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateWaterDeviceForEngineer(@RequestBody List<WaterDeviceDTO> wdds) {
        waterDeviceService.updateWaterDeviceForEngineer(wdds);
    }

    /****
     *业务系统更新安装工手机号-根据安装工id更新设备上的安装工手机号
     * @param update
     */
    @PostMapping(value = "/waterdevice/engineerphone", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateDeviceForEngineerPhone(@RequestBody WaterDeviceDTO update) {
        waterDeviceMapper.updateDeviceForEngineerPhone(update);
    }


    /****
     * 根据省市区获取水机设备
     * @param wdd
     */
    @ApiOperation(value = "根据省市区获取水机设备", notes = "根据省市区获取水机设备")
    @ApiImplicitParam(name = "wdd", value = "查询参数", required = true, dataType = "WaterDeviceDTO", paramType = "body")
    @PostMapping(value = "/waterdevice/prc", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> getWaterDeviceListByPrc(@RequestBody WaterDeviceDTO wdd) {
        return waterDeviceMapper.selectWaterDeviceByPrc(wdd);
    }

    /***
     * 公司水机设备续费率统计
     * @param query
     * @return
     */
    @GetMapping(value = "/company/waterdevice/renew/statsInfo")
    List<SalesStatsDTO> getDeviceRenewPropList(@RequestParam("ids") List<Integer> ids) {
        return waterDeviceService.getDeviceRenewPropList(ids);

    }

    /**
     * 安装工app-创建维修工单-根据关键字及安装工服务区域选择水机设备列表
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/water/device/engineer/getWaterDeviceList")
    public List<WaterDeviceDTO> getWaterDeviceList(@RequestBody WaterDeviceQuery query) {

        if (CollectionUtil.isEmpty(query.getServiceAreaList())) {
            return null;
        }

        return waterDeviceMapper.getWaterDeviceList(query);

    }

    /**
     * 设备iccid变更页面
     *
     * @param pageNum
     * @param pageSize
     * @param sn
     * @return
     */
    @GetMapping(value = "/water/device/{sn}/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceReplaceRecordDTO> getWaterDeviceReplaceBySn(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @PathVariable String sn) {
        return waterDeviceService.getWaterDeviceReplaceBySn(sn, pageNum, pageSize);
    }



    /**
     * 查询已完成工单列表
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    @GetMapping(value = "/workorder/complete/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceCompleteDTO> getCompleteWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                                   @RequestParam(value = "key",required = false) String key,
                                                                   @RequestParam(value = "engineerId") Integer engineerId){
        return waterDeviceService.getCompleteWorkOrderList(pageNum,pageSize,key,engineerId);
    }
}
