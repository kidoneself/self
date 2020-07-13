package com.yimao.cloud.system.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.water.DeductionPlanChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.DeductionPlanDTO;
import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.dto.water.ManualPadCostDTO;
import com.yimao.cloud.pojo.dto.water.SimCardAccountDTO;
import com.yimao.cloud.pojo.dto.water.SimCardNumberDTO;
import com.yimao.cloud.pojo.dto.water.TdsUploadRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceOverviewDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.export.water.ManualPadCostExport;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.pojo.query.water.ManualPadCostQuery;
import com.yimao.cloud.pojo.query.water.SimCardNumberQuery;
import com.yimao.cloud.pojo.query.water.TdsUploadRecordQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.ManualPadCostVO;
import com.yimao.cloud.pojo.vo.water.SimCardNumberVO;
import com.yimao.cloud.pojo.vo.water.TdsUploadRecordVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceFaultVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述：水机微服务的接口列表
 *
 * @author liu yi
 * @date 2019/5/5.
 */
@FeignClient(name = Constant.MICROSERVICE_WATER, configuration = MultipartSupportConfig.class)
public interface WaterFeign {

    /**
     * 根据水机设备SN获取设备信息
     *
     * @param sn 设备sn编码
     */
    @GetMapping(value = "/waterdevice")
    WaterDeviceDTO getBySnCode(@RequestParam("sn") String sn);

    /**
     * 查询水机耗材（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/consumable/{pageNum}/{pageSize}")
    PageVO<WaterDeviceConsumableDTO> getConsumablePageList(@PathVariable(value = "pageNum") Integer pageNum,
                                                           @PathVariable(value = "pageSize") Integer pageSize,
                                                           @RequestParam(value = "type", required = false) Integer type,
                                                           @RequestParam(value = "model", required = false) String model);

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
     * @param id
     * @return
     * @description 根据id查询水机滤芯更换记录
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/{id}")
    WaterDeviceFilterChangeRecordDTO getWaterDeviceFilterChangeRecordById(@PathVariable(value = "id") Integer id);


    /***
     * 功能描述:批量记录生效状态改变
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:53
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/waterdevice/filterChangeRecord")
    void forbiddenWaterDeviceFilterChangeRecord(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "effective", defaultValue = "1") Integer effective);


    /***
     * 功能描述:根据SN和时间查询水机滤芯更换记录
     *
     * @param: [deviceSncode, createTime]
     * @auther: liu yi
     * @date: 2019/5/18 10:04
     * @return:
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/sncode")
    List<WaterDeviceFilterChangeRecordDTO> getWaterDeviceFilterChangeRecordBySnCode(@RequestParam("deviceSncode") String deviceSncode,
                                                                                    @RequestParam("createTime") Date createTime,
                                                                                    @RequestParam("source") Integer source);

    @PostMapping(value = "/simcard/account", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveSimCardAccount(@RequestBody SimCardAccountDTO dto);

    @PutMapping(value = "/simcard/account", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateSimCardAccount(@RequestBody SimCardAccountDTO dto);

    @GetMapping(value = "/simcard/account/{pageNum}/{pageSize}")
    PageVO<SimCardAccountDTO> pageSimCardAccount(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize);

    @PostMapping(value = "/simcard/number", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveSimCardNumber(@RequestBody SimCardNumberDTO dto);

    @PutMapping(value = "/simcard/number", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateSimCardNumber(@RequestBody SimCardNumberDTO dto);

    @PostMapping(value = "/simcard/number/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<SimCardNumberVO> pageSimCardNumber(@PathVariable("pageNum") Integer pageNum,
                                              @PathVariable("pageSize") Integer pageSize,
                                              @RequestBody SimCardNumberQuery query);

    @GetMapping(value = "/simcard/account/all")
    List<SimCardAccountDTO> listSimCardAccount();

    @PostMapping(value = "/tds/record", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveTdsUploadRecord(@RequestBody TdsUploadRecordDTO dto);

    @PutMapping(value = "/tds/record", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateTdsUploadRecord(@RequestBody TdsUploadRecordDTO dto);

    @PostMapping(value = "/tds/record/reset")
    void resetTdsUploadRecord(@RequestParam("sn") String sn);

    @PostMapping(value = "/tds/record/{id}/verify")
    void verifyTdsUploadRecord(@PathVariable("id") Integer id,
                               @RequestParam("verifyResult") String verifyResult,
                               @RequestParam(value = "verifyReason", required = false) String verifyReason);

    @PostMapping(value = "/tds/record/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<TdsUploadRecordVO> pageTdsUploadRecord(@PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize,
                                                  @RequestBody TdsUploadRecordQuery query);

    @GetMapping(value = "/tds/record/{id}/detail")
    TdsUploadRecordVO getTdsUploadRecordDetail(@PathVariable("id") Integer id);

    @GetMapping(value = "/placechangerecord/{pageNum}/{pageSize}")
    PageVO<WaterDevicePlaceChangeRecordVO> pagePlaceChangeRecord(@PathVariable("pageNum") Integer pageNum,
                                                                 @PathVariable("pageSize") Integer pageSize,
                                                                 @RequestParam(value = "sn", required = false) String sn);

    @GetMapping(value = "/costchangerecord/{pageNum}/{pageSize}")
    PageVO<WaterDeviceCostChangeRecordDTO> pageCostChangeRecord(@PathVariable("pageNum") Integer pageNum,
                                                                @PathVariable("pageSize") Integer pageSize,
                                                                @RequestParam(value = "sn", required = false) String sn);

    @PostMapping(value = "/waterdevice/replacerecord/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WaterDeviceReplaceRecordDTO> pageWaterDeviceReplaceRecord(@PathVariable("pageNum") Integer pageNum,
                                                                     @PathVariable("pageSize") Integer pageSize,
                                                                     @RequestBody WaterDeviceReplaceRecordQuery query);

    @GetMapping(value = "/waterdevice/replacerecord/{id}/detail")
    WaterDeviceReplaceRecordDTO getWaterDeviceReplaceRecordDetail(@PathVariable("id") Integer id);

    @PostMapping(value = "/waterdevice/replacerecord/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<DeviceReplaceRecordExport> exportWaterDeviceReplaceRecord(@RequestBody WaterDeviceReplaceRecordQuery query);

    @PostMapping(value = "/waterdevice/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WaterDeviceDTO> pageWaterDevice(@PathVariable("pageNum") Integer pageNum,
                                           @PathVariable("pageSize") Integer pageSize,
                                           @RequestBody WaterDeviceQuery query);

    @GetMapping(value = "/waterdevice/{id}/detail")
    WaterDeviceVO getWaterDeviceDetailById(@PathVariable("id") Integer id);

    @GetMapping(value = "/waterdevice/{id}/changecost")
    Object getWaterDeviceCostById(@PathVariable("id") Integer id);

    @PatchMapping(value = "/waterdevice/{id}/changecost")
    void changeWaterDeviceCost(@PathVariable("id") Integer id, @RequestParam(value = "newCostId") Integer newCostId);

    @PatchMapping(value = "/waterdevice/{id}/activatingsimcard")
    void activatingWaterDeviceSimCard(@PathVariable("id") Integer id);

    @PatchMapping(value = "/waterdevice/{id}/unbundling")
    void unbundlingWaterDevice(@PathVariable("id") Integer id);

    @PatchMapping(value = "/waterdevice/{id}/fullamount")
    void restoreWaterDeviceFullAmount(@PathVariable("id") Integer id);

    @PatchMapping(value = "/waterdevice/{id}")
    void updateWaterDevice(@PathVariable("id") Integer id, @RequestParam(value = "oldSn") String oldSn, @RequestParam(value = "newSn") String newSn,
                           @RequestParam(value = "oldIccid") String oldIccid, @RequestParam(value = "newIccid") String newIccid,
                           @RequestParam(value = "oldBatchCode") String oldBatchCode, @RequestParam(value = "newBatchCode") String newBatchCode,
                           @RequestParam(value = "address") String address);

    @PatchMapping(value = "/waterdevice/replace")
    void replaceWaterDevice(@RequestParam("id") Integer id, @RequestParam(value = "oldSn") String oldSn, @RequestParam(value = "newSn") String newSn,
                            @RequestParam(value = "oldIccid") String oldIccid, @RequestParam(value = "newIccid") String newIccid,
                            @RequestParam(value = "oldBatchCode") String oldBatchCode, @RequestParam(value = "newBatchCode") String newBatchCode);

    @PatchMapping(value = "/waterdevice/{id}/renew", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void renewWaterDevice(@PathVariable("id") Integer id, @RequestParam(value = "costId") Integer costId, @RequestParam(value = "payType") Integer payType, @RequestParam(value = "filePaths") String filePaths, @RequestPart(required = false, value = "files") MultipartFile[] files);

    @PostMapping(value = "/filtersetting", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveFilterSetting(@RequestBody FilterSettingDTO dto);

    @DeleteMapping(value = "/filtersetting/{id}")
    void deleteFilterSetting(@PathVariable("id") Integer id);

    @PutMapping(value = "/filtersetting", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateFilterSetting(@RequestBody FilterSettingDTO dto);

    @PostMapping(value = "/filtersetting/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<FilterSettingDTO> pageFilterSetting(@PathVariable("pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize,
                                               @RequestBody FilterSettingQuery query);

    @GetMapping(value = "/filtersetting/{id}/detail")
    FilterSettingDTO getFilterSettingById(@PathVariable("id") Integer id);

    @GetMapping(value = "/filtersetting/consumables")
    List<WaterDeviceConsumableDTO> listConsumableByDeviceModelForFilterSetting(@RequestParam(value = "deviceModel") String deviceModel);

    @PostMapping(value = "/padcost", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveManualPadCost(@RequestBody ManualPadCostDTO dto);

    @PutMapping(value = "/padcost", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateManualPadCost(ManualPadCostDTO dto);

    @PostMapping(value = "/padcost/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<ManualPadCostVO> pageManualPadCost(@PathVariable("pageNum") Integer pageNum,
                                              @PathVariable("pageSize") Integer pageSize,
                                              @RequestBody ManualPadCostQuery query);

    @PostMapping(value = "/padcost/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<ManualPadCostExport> listManualPadCostExport(@RequestBody ManualPadCostQuery query);


    /***
     * 功能描述:获取设备增长趋势
     *
     * @auther: liu yi
     * @date: 2019/7/31 15:45
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/growthTrendOverview")
    Map getWaterDeviceGrowthTrend();

    /***
     * 功能描述:获取设备概况
     *
     * @param: []
     * @auther: liu yi
     * @date: 2019/7/31 15:44
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/overview")
    WaterDeviceOverviewDTO waterDeviceOverview();


    @GetMapping(value = "/faultrecord/{pageNum}/{pageSize}")
    PageVO<WaterDeviceFaultVO> pageDeviceFault(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "sn", required = false) String sn);


    @PostMapping(value = "/waterdevice/changePlan")
    void changePlan(@RequestParam(value = "planId") Integer planId);

    @GetMapping(value = "/waterdevice/deductionPlans")
    List<DeductionPlanDTO> listDeductionPlan(@RequestParam(value = "deviceId") Integer deviceId);

    @GetMapping(value = "/waterdevice/deductionPlan/changeRecords")
    List<DeductionPlanChangeRecordDTO> listDeductionPlanChangeRecord(@RequestParam(value = "deviceId") Integer deviceId);
}
