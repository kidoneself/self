package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.DeductionPlanChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.DeductionPlanDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceOverviewDTO;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.SystemFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
@RestController
@Api(tags = "WaterDeviceController")
public class WaterDeviceController {

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private SystemFileService systemFileService;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 业务系统-水机物联-设备管理-查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/waterdevice/{pageNum}/{pageSize}")
    @ApiOperation(value = "业务系统-水机物联-设备管理-查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "WaterDeviceQuery", paramType = "body")
    })
    public PageVO<WaterDeviceDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody WaterDeviceQuery query) {
        return waterFeign.pageWaterDevice(pageNum, pageSize, query);
    }

    /**
     * 业务系统-水机物联-设备管理-查看详情
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/{id}/detail")
    @ApiOperation(value = "业务系统-水机物联-设备管理-查看详情")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public WaterDeviceVO getDetailById(@PathVariable Integer id) {
        return waterFeign.getWaterDeviceDetailById(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-计费方式（查询现有的计费方式）
     *
     * @param id 设备ID
     */
    @GetMapping(value = "/waterdevice/{id}/changecost")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-计费方式（查询）")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public Object getCostById(@PathVariable Integer id) {
        return waterFeign.getWaterDeviceCostById(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-计费方式（修改成新的计费方式）
     *
     * @param id        设备ID
     * @param newCostId 修改后的计费方式ID
     */
    @PatchMapping(value = "/waterdevice/{id}/changecost")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-计费方式（提交修改）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "newCostId", value = "修改后的计费方式ID", dataType = "Long", paramType = "query", required = true)
    })
    public void changeCostById(@PathVariable Integer id, @RequestParam Integer newCostId) {
        waterFeign.changeWaterDeviceCost(id, newCostId);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-激活SIM卡
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/activatingsimcard")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-激活SIM卡")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public void activatingSimCard(@PathVariable Integer id) {
        waterFeign.activatingWaterDeviceSimCard(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-解除绑定
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/unbundling")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-解除绑定")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public void unbundling(@PathVariable Integer id) {
        waterFeign.unbundlingWaterDevice(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-恢复满额
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}/fullamount")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-恢复满额")
    @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true)
    public void restoreFullAmount(@PathVariable Integer id) {
        waterFeign.restoreWaterDeviceFullAmount(id);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-修改设备
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/{id}")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-修改设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "oldSn", value = "旧SN码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newSn", value = "新SN码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "oldIccid", value = "旧ICCID", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newIccid", value = "新ICCID", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "oldBatchCode", value = "旧生产批次码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newBatchCode", value = "新生产批次码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "address", value = "设备详情地址", dataType = "String", paramType = "query")
    })
    public void update(@PathVariable Integer id, @RequestParam String oldSn, @RequestParam String newSn, @RequestParam String oldIccid,
                       @RequestParam String newIccid, @RequestParam String oldBatchCode, @RequestParam String newBatchCode, @RequestParam(required = false) String address) {
        waterFeign.updateWaterDevice(id, oldSn, newSn, oldIccid, newIccid, oldBatchCode, newBatchCode, address);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-更换设备
     *
     * @param id 设备ID
     */
    @PatchMapping(value = "/waterdevice/replace")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-更换设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "oldSn", value = "旧SN码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newSn", value = "新SN码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "oldIccid", value = "旧ICCID", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newIccid", value = "新ICCID", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "oldBatchCode", value = "旧生产批次码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newBatchCode", value = "新生产批次码", dataType = "String", paramType = "query", required = true)
    })
    public void replace(@RequestParam(required = false) Integer id, @RequestParam String oldSn, @RequestParam String newSn, @RequestParam String oldIccid,
                        @RequestParam String newIccid, @RequestParam String oldBatchCode, @RequestParam String newBatchCode) {
        waterFeign.replaceWaterDevice(id, oldSn, newSn, oldIccid, newIccid, oldBatchCode, newBatchCode);
    }

    /**
     * 业务系统-水机物联-设备管理-设备详情-续费
     *
     * @param id     设备ID
     * @param costId 计费方式ID
     */
    @PatchMapping(value = "/waterdevice/{id}/renew", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备详情-续费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "设备ID", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "costId", value = "计费方式ID", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", dataType = "Long", paramType = "query", required = true)
    })
    public void renew(@PathVariable Integer id, @RequestParam Integer costId, @RequestParam Integer payType, @RequestPart(required = false, value = "files") MultipartFile[] files) {
        if (files.length > 0) {
            if (PayType.find(payType) == null) {
                throw new NotFoundException("支付方式选择错误。");
            }
            //将文件上传至文件服务器，返回访问路径
            String filePaths = "";
            for (MultipartFile file : files) {
                String path = systemFileService.uploadAndSave(file, "renew", "管理后台操作续费上传附件");
                filePaths += path + ",";
            }
            filePaths = filePaths.substring(0, filePaths.length() - 1);
            waterFeign.renewWaterDevice(id, costId, payType, filePaths, files);
        } else {
            throw new BadRequestException("请提交支付凭证。");
        }
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
        Map map = waterFeign.getWaterDeviceGrowthTrend();
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
        WaterDeviceOverviewDTO dto = waterFeign.waterDeviceOverview();
        return ResponseEntity.ok(dto);
    }

    /**
     * 根据水机设备SN获取设备信息
     *
     * @param snCode 设备sn编码
     */
    @GetMapping(value = "/waterdevice")
    @ApiOperation(value = "根据水机设备SN获取设备信息")
    public WaterDeviceDTO getBySnCode(@RequestParam String snCode) {
        WaterDeviceDTO dto = waterFeign.getBySnCode(snCode);
        return dto;
    }

    /**
     * @Author ycl
     * @Description 业务系统-水机物联-设备管理-滤芯更换导出
     * @Date 17:26 2019/11/1
     * @Param
     **/
    @PostMapping(value = "/waterdevice/filterReplace/export")
    @ApiOperation(value = "业务系统-水机物联-设备管理-滤芯更换导出")
    @ApiImplicitParam(name = "query", value = "滤芯更换导出查询条件", dataType = "WaterDeviceQuery", paramType = "body")
    public Object filterReplaceExport(@RequestBody WaterDeviceQuery query) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/waterdevice/filterReplace/export";
        ExportRecordDTO record = exportRecordService.save(url, "滤芯更换数据");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_WATER, map);
        }
        return CommResult.ok(record.getId());

     /*   List<FilterReplaceExport> filterReplaceList = waterFeign.filterReplaceExport(query);
        if (CollectionUtil.isEmpty(filterReplaceList)) {
            throw new NotFoundException("没有数据可以导出");
        }

        String header = "滤芯更换数据";
        String[] titles = new String[]{"SN码", "滤芯名", "省份", "城市", "地区", "更换时间", "设备添加时间"};
        String[] beanPropertys = new String[]{"sn", "filterName", "province", "city", "region", "createTime", "activatingTime"};
        boolean boo = ExcelUtil.exportSXSSF(filterReplaceList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }


    /**
     * @Author ycl
     * @Description 业务系统-水机物联-设备管理-设备列表导出
     * @Date 18:46 2019/11/30
     * @Param
     **/
    @PostMapping(value = "/waterdevice/list/export")
    @ApiOperation(value = "业务系统-水机物联-设备管理-设备列表导出")
    @ApiImplicitParam(name = "query", value = "设备列表导出查询条件", dataType = "WaterDeviceQuery", paramType = "body")
    public Object deviceListExport(@RequestBody WaterDeviceQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/waterdevice/list/export";
        ExportRecordDTO record = exportRecordService.save(url, "设备列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_WATER, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * 获取设备的扣费计划
     */
    @GetMapping(value = "/waterdevice/deductionPlans")
    @ApiOperation(value = "业务系统-水机物联-设备管理-详情-扣费计划-获取设备的扣费计划")
    public List<DeductionPlanDTO> listDeductionPlan(@RequestParam Integer deviceId) {
        return waterFeign.listDeductionPlan(deviceId);
    }

    /**
     * 获取设备的扣费计划修改记录
     */
    @GetMapping(value = "/waterdevice/deductionPlan/changeRecords")
    @ApiOperation(value = "业务系统-水机物联-设备管理-详情-扣费计划-获取设备的扣费计划修改记录")
    public List<DeductionPlanChangeRecordDTO> listDeductionPlanChangeRecord(@RequestParam Integer deviceId) {
        return waterFeign.listDeductionPlanChangeRecord(deviceId);
    }

    /**
     * 手动修改扣费计划
     */
    @PostMapping(value = "/waterdevice/changePlan")
    @ApiOperation(value = "业务系统-水机物联-设备管理-详情-扣费计划-修改设备的扣费计划")
    public void changePlan(@RequestParam Integer planId) {
        waterFeign.changePlan(planId);
    }

}
