package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.enums.WorkOrderOperationType;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderCountDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderOperationDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderResultDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderUnfinishedRsDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.EngineerWorkOrderVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @description 工单控制层
 * @author zhilin.he
 * @date 2019/4/29 16:22
 **/
@RestController
@Slf4j
@Api(tags = "WorkOrderController")
public class WorkOrderController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 描述：云平台下载批量退单导入模板
     */
    @GetMapping(value = "/workorder/download/model")
    @ApiOperation(value = "云平台下载批量退单导入模板", notes = "云平台下载批量退单导入模板")
    public Object downloadRefundModel(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "批量退款导入模板.xls";
        String path = request.getSession().getServletContext().getRealPath("/");
        File destFile = new File(path + fileName);
        try {
            boolean result = SFTPUtil.downloadLocal("/static/excel/", fileName, destFile, response);
            if (!result) {
                throw new YimaoException("批量退款导入模板下载失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new YimaoException("批量退款导入模板下载失败！");
        }
        return ResponseEntity.noContent().build();
    }

    /** -------------------------------云平台工单后台操作----start-------------------------------------------- **/

    /**
     * 描述：云平台条件查询工单列表
     **/
    @GetMapping(value = "/workorder/{operationType}/{pageNum}/{pageSize}")
    @ApiOperation(value = "云平台条件查询工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operationType", value = "工单操作菜单类型：1、工单列表；2、退单列表；3、删除列表；", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workOrderId", value = "工单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "cancelStartTime", value = "退单开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "cancelEndTime", value = "退单结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "deleteStartTime", value = "删除开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "deleteEndTime", value = "删除结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "backOrderStatus", value = "退单状态1:退单中；2：退单成功；3：待退单；N、未退单；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "backRefundStatus", value = "退款状态:0、未退款；1、退款中；2、退款成功3、退款失败；4、预退款", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "roleLevel", value = "经销商角色状态：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "completeTime", value = "工单完成时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "payTime", value = "工单支付时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "accountMonth", value = "结算月份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "订单来源:0、经销商APP添加；1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "工单状态：-2、审核未通过；-1、审核中；0-已支付；1-未受理；2-已受理；3-处理中；4-已完成；5-待付款；6-客服拒绝；7-分配客服；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "payEndTime", value = "支付结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "finishStartTime", value = "完成开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "finishEndTime", value = "完成结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "finishMoneyStartTime", value = "完款开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "finishMoneyEndTime", value = "完款结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
    })
    public ResponseEntity<PageVO<WorkOrderResultDTO>> queryWorkOrderList(@PathVariable(value = "operationType") Integer operationType,
                                                                         @PathVariable(value = "pageNum") Integer pageNum,
                                                                         @PathVariable(value = "pageSize") Integer pageSize,
                                                                         @RequestParam(required = false) Integer payType,
                                                                         @RequestParam(required = false) String province,
                                                                         @RequestParam(required = false) String city,
                                                                         @RequestParam(required = false) String region,
                                                                         @RequestParam(required = false) Long orderId,
                                                                         @RequestParam(required = false) String workOrderId,
                                                                         @RequestParam(value = "startTime", required = false) Date startTime,
                                                                         @RequestParam(value = "endTime", required = false) Date endTime,
                                                                         @RequestParam(value = "cancelStartTime", required = false) Date cancelStartTime,
                                                                         @RequestParam(value = "cancelEndTime", required = false) Date cancelEndTime,
                                                                         @RequestParam(value = "deleteStartTime", required = false) Date deleteStartTime,
                                                                         @RequestParam(value = "deleteEndTime", required = false) Date deleteEndTime,
                                                                         @RequestParam(required = false) Integer backOrderStatus,
                                                                         @RequestParam(required = false) Integer backRefundStatus,
                                                                         @RequestParam(required = false) Integer roleLevel,
                                                                         @RequestParam(required = false) Date completeTime,
                                                                         @RequestParam(required = false) Date payTime,
                                                                         @RequestParam(required = false) String accountMonth,
                                                                         @RequestParam(required = false, defaultValue = "-1") Integer type,
                                                                         @RequestParam(required = false, defaultValue = "-3") Integer status,
                                                                         @RequestParam(required = false) Integer userId,
                                                                         @RequestParam(value = "payStartTime", required = false) Date payStartTime,
                                                                         @RequestParam(value = "payEndTime", required = false) Date payEndTime,
                                                                         @RequestParam(value = "finishStartTime", required = false) Date finishStartTime,
                                                                         @RequestParam(value = "finishEndTime", required = false) Date finishEndTime,
                                                                         @RequestParam(value = "finishMoneyStartTime", required = false) Date finishMoneyStartTime,
                                                                         @RequestParam(value = "finishMoneyEndTime", required = false) Date finishMoneyEndTime,
                                                                         @RequestParam(value = "terminal", required = false) Integer terminal,
                                                                         @RequestParam(value = "payTerminal", required = false) Integer payTerminal,
                                                                         @RequestParam(value = "chargeBackStatus", required = false) Integer chargeBackStatus,
                                                                         @RequestParam(value = "distributorId", required = false) Integer distributorId

    ) {
        return ResponseEntity.ok(orderFeign.queryWorkOrderList(operationType, pageNum, pageSize, payType, province, city, region, orderId, workOrderId, startTime, endTime, cancelStartTime, cancelEndTime, deleteStartTime, deleteEndTime, backOrderStatus, backRefundStatus, roleLevel, completeTime, payTime, accountMonth, type, status, userId, payStartTime, payEndTime, finishStartTime, finishEndTime, finishMoneyStartTime, finishMoneyEndTime, terminal, payTerminal, chargeBackStatus,distributorId));
    }

    /**
     * 描述：云平台导出工单
     **/
    @PostMapping("/workorder/export")
    @ApiOperation(value = "云平台导出工单")
    public Object exportWorkOrder(@RequestBody WorkOrderQueryDTO query) {
        //产品收益类型
        query.setIncomeType(IncomeType.PRODUCT_INCOME.value);
        query.setStatus(query.getStatus() != null ? query.getStatus() : -3);
        query.setType(query.getType() != null ? query.getType() : -1);
        query.setOperationType(query.getOperationType() == null ? WorkOrderOperationType.WORK_ORDER.value : query.getOperationType());

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/workorder/export";
        ExportRecordDTO record = exportRecordService.save(url, "工单数据");

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

    /**
     * 描述：云平台删除工单
     *
     * @param workOrderId 工单ID
     **/
    @DeleteMapping(value = "/workorder/{workOrderId}")
    @ApiOperation(value = "云平台删除工单", notes = "云平台删除工单")
    @ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "path")
    public ResponseEntity deleteWorkOrder(@PathVariable(value = "workOrderId") String workOrderId) {
        orderFeign.deleteWorkOrder(workOrderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 描述：云平台分配客服--查询该工单所在地区下的安装工列表
     *
     * @param id 工单ID
     **/
    @GetMapping(value = "/workorder/allot/engineer/{id}")
    @ApiOperation(value = "云平台分配客服")
    @ApiImplicitParam(name = "id", value = "工单ID", dataType = "String", required = true, paramType = "path")
    public ResponseEntity<List<EngineerDTO>> getAllotEngineerList(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(orderFeign.getAllotEngineerList(id));
    }

    /**
     * 云平台分配客服--即分配安装工
     *
     * @param workOrderId 工单ID
     * @param engineerId  安装工id
     **/
    @PutMapping(value = "/workorder/allot/engineer")
    @ApiOperation(value = "云平台分配客服", notes = "云平台分配客服")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "engineerId", value = "安装工id", dataType = "Long", required = true, paramType = "query")
    })
    public ResponseEntity allotEngineer(@RequestParam(value = "workOrderId") String workOrderId,
                                        @RequestParam(value = "engineerId") Integer engineerId) {
        orderFeign.allotEngineer(workOrderId, engineerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 云平台工单评价
     **/
    @PutMapping(value = "/workorder/appraise/score")
    @ApiOperation(value = "工单评价", notes = "工单评价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "score", value = "评分内容", dataType = "Integer", required = true, paramType = "query"),
            @ApiImplicitParam(name = "appraiseContent", value = "评分内容", dataType = "String", required = true, paramType = "query")
    })
    public void updateWorkOrderAppraiseScore(@RequestParam(value = "workOrderId") String workOrderId,
                                             @RequestParam(value = "score") Integer score,
                                             @RequestParam(value = "appraiseContent") String appraiseContent) {
        orderFeign.updateWorkOrderAppraiseScore(workOrderId, score, appraiseContent);
    }

    /**
     * 描述：云平台工单概况--获取某个状态工单的数量
     **/
    @GetMapping("/workorder/count/status")
    @ApiOperation(value = "云平台工单概况")
    public ResponseEntity<List<WorkOrderCountDTO>> countWorkOrderByStatus() {
        return ResponseEntity.ok(orderFeign.countWorkOrderByStatus());
    }

    /**
     * 描述：云平台工单趋势--获取两个日期间的工单数量
     **/
    @GetMapping(value = "/workorder/count/time")
    @ApiOperation(value = "云平台工单趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "days", value = "日期天数", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd")
    })
    public ResponseEntity<List<WorkOrderCountDTO>> countWorkOrderByCreateTime(@RequestParam(value = "days", required = false) String days,
                                                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                                                              @RequestParam(value = "endTime", required = false) Date endTime) {
        return ResponseEntity.ok(orderFeign.countWorkOrderByCreateTime(days, startTime, endTime));
    }


    /**
     * 描述：云平台根据条件查询安装工单支付信息
     */
    @GetMapping(value = "/workorder/pay/{pageNum}/{pageSize}")
    @ApiOperation(value = "云平台根据条件查询安装工单支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<PayRecordDTO>> getWorkOrderPayList(WorkOrderQueryDTO workOrderQueryDTO,
                                                                    @PathVariable(value = "pageNum") Integer pageNum,
                                                                    @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(orderFeign.getWorkOrderPayList(workOrderQueryDTO, pageNum, pageSize));
    }

    /**
     * 描述：云平台--工单退单
     *
     * @param id 工单ID
     **/
    @PutMapping(value = "/workorder/chargeback/{id}")
    @ApiOperation(value = "云平台退单")
    @ApiImplicitParam(name = "id", value = "工单ID", dataType = "String", required = true, paramType = "path")
    public void backOrderUpdate(@PathVariable(value = "id") String id) {
        orderFeign.backOrderUpdate(id);
    }

    /**
     * 根据条件查询工单发票列表信息
     */
    @GetMapping(value = "/workorder/invoice/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询工单发票列表信息", notes = "根据条件查询工单发票列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<WorkOrderDTO>> getWorkOrderInvoiceList(WorkOrderQueryDTO dto,
                                                                        @PathVariable("pageNum") Integer pageNum,
                                                                        @PathVariable("pageSize") Integer pageSize) {
        return ResponseEntity.ok(orderFeign.getWorkOrderInvoiceList(dto, pageNum, pageSize));
    }

    /**
     * 云平台导出工单发票信息
     */
    @PostMapping(value = "/workorder/invoice/export")
    @ApiOperation(value = "云平台导出工单发票信息", notes = "云平台导出工单发票信息")
    public Object exportWorkOrderInvoice(WorkOrderQueryDTO dto) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/workorder/invoice/export";
        ExportRecordDTO record = exportRecordService.save(url, "工单发票数据");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", dto);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());

//        List<WorkOrderInvoiceExportDTO> list = orderFeign.exportWorkOrderInvoiceList(dto);
//        if (CollectionUtil.isEmpty(list)) {
//            throw new BadRequestException("导出列表数据为空！");
//        }
//        String header = "工单发票数据";
//        String[] titles = new String[]{"工单号", "省", "市", "区", "用户姓名", "用户联系方式", "客服姓名", "客服联系方式", "商品类型", "计费方式", "发票类型", "发票抬头", "公司名称", "税号", "开户行", "开户号", "地址", "电话", "下单时间", "支付时间", "开票时间", "开票金额", "完成时间"};
//        String[] beanPropertys = new String[]{"workOrderId", "province", "city", "region", "userRealName", "userPhone", "engineerName", "engineerPhone", "deviceModel", "costName", "invoiceType", "invoiceHead", "companyName", "dutyNo", "bankName", "bankAccount", "companyAddress", "companyPhone", "createTime", "payTime", "applyTime", "amountFee", "confirmTime"};
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
//        return ResponseEntity.noContent().build();
    }

    /**
     * @description 根据工单id查询工单操作记录列表
     */
    @GetMapping(value = "/workorder/operation/{id}/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据工单id查询工单操作记录列表", notes = "根据工单id查询工单操作记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "工单id", dataType = "String", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<WorkOrderOperationDTO>> getWorkOrderOperationList(@PathVariable("id") String id,
                                                                                   @PathVariable("pageNum") Integer pageNum,
                                                                                   @PathVariable("pageSize") Integer pageSize) {
        return ResponseEntity.ok(orderFeign.getWorkOrderOperationList(id, pageNum, pageSize));
    }

    /**
     * 描述：根据工单id获取工单信息
     *
     * @param workOrderId 工单ID
     **/
    @GetMapping(value = "/workorder/{workOrderId}")
    @ApiOperation(value = "根据工单id获取工单信息", notes = "根据工单id获取工单信息")
    @ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "path")
    public ResponseEntity<WorkOrderDTO> getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId) {
        return ResponseEntity.ok(orderFeign.getWorkOrderById(workOrderId));
    }
    /** -------------------------------云平台工单后台操作----end-------------------------------------------- **/

    /**
     * 获取安装工的工单列表
     *
     * @param engineerId 安装工ID
     */
    @GetMapping(value = "/workorder/engineer/{engineerId}")
    @ApiOperation(value = "获取安装工的工单列表")
    @ApiImplicitParam(name = "engineerId", value = "安装工ID", dataType = "Long", paramType = "path", required = true)
    public List<EngineerWorkOrderVO> getEngineerWorkOrder(@PathVariable Integer engineerId) {
        return orderFeign.listWorkOrderByEngineerId(engineerId);
    }

    /**
     * 根据省市区获取工单未完成和退单中的工单数量
     **/
    @PostMapping(value = "/workorder/unfinished")
    @ApiOperation(value = "根据省市区获取工单未完成和退单中的工单数量")
    @ApiImplicitParam(name = "trans", value = "服务站门店信息", dataType = "TransferAreaInfoDTO", paramType = "body")
    public ResponseEntity<List<WorkOrderUnfinishedRsDTO>> getWorkOrderForUnfinished(@RequestBody TransferAreaInfoDTO trans) {
        return ResponseEntity.ok(orderFeign.getWorkOrderForUnfinished(trans));
    }


}
