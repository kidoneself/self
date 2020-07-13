package com.yimao.cloud.system.controller;


import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.MaintenanceWorkOrderSourceEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.enums.WorkOrderStepEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维护工单
 *
 * @author Liu Yi
 * @date 2019/4/1.
 */
@RestController
@Slf4j
@Api(tags = "MaintenanceWorkOrderController")
public class MaintenanceWorkOrderController {

    @Resource
    private UserCache userCache;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/order/maintenanceWorkOrder")
    @ApiOperation(value = "新增维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sncode", value = "sn", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "materielDetailIds", value = "耗材id,多个逗号隔开", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "materielDetailNames", value = "耗材名称,多个逗号隔开", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "hasSelfChange", value = "是否自助更换 1-是 2-否", required = true, dataType = "Long", paramType = "query")
    })
    public Object create(@RequestParam String sncode, @RequestParam String materielDetailIds, @RequestParam String materielDetailNames, @RequestParam(required = false, defaultValue = "1") Integer hasSelfChange) {
        if (StringUtil.isBlank(sncode)) {
            throw new BadRequestException("参数Sncode不能为空!");
        }
        if (StringUtil.isBlank(materielDetailIds)) {
            throw new BadRequestException("请选择滤芯类型!");
        }
        if (StringUtil.isBlank(materielDetailNames)) {
            throw new BadRequestException("请选择滤芯类型!");
        }

        WaterDeviceDTO device = waterFeign.getBySnCode(sncode);
        if (device == null) {
            throw new BadRequestException("该SN设备不存在!");
        }
        WorkOrderDTO workOrder = orderFeign.getWorkOrderById(device.getWorkOrderId());
        if (workOrder == null) {
            throw new BadRequestException("安装工单不存在!");
        }

        MaintenanceWorkOrderDTO dto = new MaintenanceWorkOrderDTO();
        dto.setMaterielDetailName(materielDetailNames);
        dto.setMaterielDetailIds(materielDetailIds);
        dto.setCountdownTime(new Date());
        dto.setMaintenanceRemark("滤芯更换");
        dto.setAddrProvince(device.getProvince());
        dto.setAddrCity(device.getCity());
        dto.setAddrRegion(device.getRegion());
        dto.setAddress(device.getAddress());
        //设置经销商信息
        dto.setDistributorId(device.getDistributorId());
        dto.setDistributorName(device.getDistributorName());
        dto.setDistributorPhone(device.getDistributorPhone());
        dto.setCreateUser(userCache.getCurrentAdminRealName());
        dto.setUpdateUser(String.valueOf(userCache.getUserId()));
        dto.setUpdateTime(new Date());
        dto.setCreateTime(new Date());
        dto.setProductId(workOrder.getProductId());//产品Id
        dto.setProductName(workOrder.getProductName());// 产品名称 翼猫水机*/
        dto.setDeviceId(device.getId());// 设备Id
        dto.setKindName(device.getDeviceScope());
        //dto.setDeviceModelId(device.getId());//DeviceModel
        dto.setDeviceModelName(device.getDeviceModel());//1603T-00
        dto.setDeviceSncode(device.getSn());
        dto.setDeviceBatchCode(device.getLogisticsCode());
        dto.setDeviceSimcard(device.getIccid());

        //是否自助更换 1-是 2-否
        if (hasSelfChange == 2) {
            //设置工程师和安装工程师、服务站门店等信息
            dto.setEngineerId(device.getEngineerId());
            dto.setEngineerName(device.getEngineerName());
            dto.setEngineerPhone(device.getEngineerPhone());
        }

        dto.setConsumerId(device.getDeviceUserId());// 所属客户Id
        dto.setConsumerName(device.getDeviceUserName());
        dto.setConsumerPhone(device.getDeviceUserPhone());
        //设置步骤
        dto.setState(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
        dto.setStateText(WorkOrderStateEnum.WORKORDER_STATE_SERVING.stateText);

        dto.setCurrentStep(WorkOrderStepEnum.MAINTENANCE_WORK_ORDER_STEP_FINISH_WORK.getStep());

        dto.setAcceptStatus(StatusEnum.YES.value());
        dto.setAcceptTime(new Date());
        dto.setCostId(device.getCostId());//计费方式Id ChargeId
        dto.setCostName(device.getCostName());//计费方式名称 ChargeName
        dto.setSource(2);
        orderFeign.createWorkOrderMaintenance(dto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/order/maintenanceWorkOrder/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维护工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object listMaintenanceWorkOrder(MaintenanceWorkOrderQueryDTO queryDTO,
                                           @PathVariable Integer pageNum,
                                           @PathVariable Integer pageSize) {
        PageVO<MaintenanceWorkOrderDTO> page = orderFeign.listMaintenanceWorkOrder(queryDTO, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    @ApiOperation(value = "根据订单号查询维护工单")
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "String", paramType = "path")
    public Object getWorkOrderMaintenanceById(@PathVariable String id) {
        MaintenanceWorkOrderDTO order = orderFeign.getWorkOrderMaintenanceById(id);
        return ResponseEntity.ok(order);
    }

    /***
     * 功能描述: 编辑维护工单信息
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/5/17 14:22
     * @return:
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder")
    @ApiOperation(value = "更新维护工单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "维护工单Id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "materielDetailIds", value = "耗材id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "materielDetailNames", value = "耗材名称", dataType = "String", required = true, paramType = "query")
    })
    public Object updateWorkOrderMaintenance(@RequestParam String id, @RequestParam String materielDetailIds, @RequestParam String materielDetailNames) {
        orderFeign.editworkOrderMaintenanceBySystem(id, materielDetailIds, materielDetailNames);

        return ResponseEntity.noContent().build();
    }

    /**
     * @param sncode
     * @param state
     * @param workOrderCompleteStatus
     * @return
     * @description 根据SN查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/sn")
    @ApiOperation(value = "根据SN查询维护工单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sncode", value = "SN", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "维护工单状态：2-已受理 3-处理中 4-已完成", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workOrderCompleteStatus", value = "完成状态：Y-完成 ，N-未完成", dataType = "String", paramType = "query")
    })
    public Object getWorkOrderMaintenanceBySnCode(@RequestParam(required = false) String sncode,
                                                  @RequestParam(required = false) Integer state,
                                                  @RequestParam(required = false) String workOrderCompleteStatus) {
        List<MaintenanceWorkOrderDTO> list = orderFeign.getWorkOrderMaintenanceBySnCode(sncode, state, workOrderCompleteStatus, MaintenanceWorkOrderSourceEnum.SYSTEM_INCOME.value);
        return ResponseEntity.ok(list);
    }


    @DeleteMapping(value = "/order/maintenanceWorkOrder/{id}")
    @ApiOperation(value = "根据id删除维护工单")
    @ApiImplicitParam(name = "id", value = "维护工单信息", required = true, dataType = "String", paramType = "path")
    public Object delete(@PathVariable String id) {
        orderFeign.deleteMaintenanceWorkOrderById(id);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:审核
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:53
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/{id}/audit")
    @ApiOperation(value = "审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "维护工单id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "recordIds", value = "记录ID集合", dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "effective", value = "生效状态：0-否；1-是", defaultValue = "1", dataType = "Long", paramType = "query")
    })
    public Object auditMaintenanceWorkOrder(@PathVariable String id,
                                            @RequestParam(required = false) Integer[] recordIds,
                                            @RequestParam(defaultValue = "1") Integer effective) {
        orderFeign.auditMaintenanceWorkOrder(id, recordIds, effective);

        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:根据条件查询导出维护工单
     *
     * @param: [queryDTO, response]
     * @auther: liu yi
     * @date: 2019/5/15 14:22
     * @return: java.lang.Object
     */
    @PostMapping(value = "/order/maintenanceWorkOrder/export")
    @ApiOperation(value = "根据条件查询导出维护工单")
    @ApiImplicitParam(name = "query", value = "维护工单查询类", required = true, dataType = "MaintenanceWorkOrderQueryDTO", paramType = "body")
    public Object exportMaintenanceWorkOrder(@RequestBody MaintenanceWorkOrderQueryDTO query) {
       /* List<MaintenanceWorkOrderExportDTO> list = orderFeign.exportMaintenanceWorkOrder(queryDTO);
        String header = "维护工单_" + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] beanPropertys = new String[]{"id", "materielDetailName", "address", "consumerName", "consumerPhone", "deviceBatchCode", "kindName", "deviceModelName", "deviceSncode", "deviceSimcard", "workOrderCompleteStatus", "workOrderCompleteTime", "createTime", "auditType", "source"};
        String[] titles = new String[]{"维护工单号", "滤芯类型", "地址", "客户名称", "客户电话", "批次码", "产品范围", "设备型号", "设备SN码", "设备ICCID", "完成状态", "完成时间", "创建时间", "审核方式", "来源"};
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出滤芯维护记录失败!");
        }*/


        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_MAINTENANCEWORKORDER_URL, "维护工单");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }

        return CommResult.ok(record.getId());
    }
}
