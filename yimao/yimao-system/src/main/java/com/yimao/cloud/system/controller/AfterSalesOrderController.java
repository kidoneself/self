package com.yimao.cloud.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.RefundReviewBatchVo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = "AfterSalesOrderController")
public class AfterSalesOrderController {

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 线上/线下退款审核列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PostMapping(value = "/refund/{pageNum}/{pageSize}")
    @ApiOperation(value = "线上退款审核列表", notes = "线上退款审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "待审核提现订单查询条件", dataType = "AfterSalesOrderQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                                                  @PathVariable(value = "pageSize") Integer pageSize,
                                                                  @RequestBody AfterSalesOrderQueryDTO dto) {
        PageVO<AfterSalesOrderDTO> page = orderFeign.refundAudit(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }

    /**
     * 线上/线下退款复核
     *
     * @param id 售后订单主键
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PatchMapping(value = "/refund/review")
    @ApiOperation(value = "线上退款复核", notes = "线上退款复核")
    @ApiImplicitParam(name = "id", value = "售后订单主键", required = true, dataType = "Long", paramType = "query")
    public ResponseEntity refundReview(@RequestParam(name = "id") Long id) {
        orderFeign.refundReview(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 线上/线下退款批量复核
     *
     * @param ids 售后订单主键
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PatchMapping(value = "/refund/review/batch")
    @ApiOperation(value = "线上退款批量复核", notes = "线上退款批量复核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "售后单号", dataType = "Long", required = true, paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "pass", value = "审核状态", dataType = "Boolean", required = true, paramType = "query"),
            @ApiImplicitParam(name = "reason", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public ResponseEntity refundReviewBatch(@RequestParam(name = "ids") List<Long> ids, @RequestParam(name = "pass") Boolean pass,
                                            @RequestParam(value = "reason", required = false) String reason) {
        log.info("===============refundReviewReq==ids=" + JSONObject.toJSON(ids) + ",pass=" + pass + ",reason" + reason);
        RefundReviewBatchVo refundReviewBatchVo = new RefundReviewBatchVo();
        refundReviewBatchVo.setIds(ids);
        refundReviewBatchVo.setPass(pass);
        refundReviewBatchVo.setReason(reason);
        orderFeign.refundReviewBatch(refundReviewBatchVo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 线上/线下退款记录列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @PostMapping(value = "/refund/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "线上/线下退款记录列表", notes = "线上/线下退款记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "待审核提现订单查询条件", dataType = "AfterSalesOrderQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                                   @RequestBody AfterSalesOrderQueryDTO dto) {
        PageVO<AfterSalesOrderDTO> page = orderFeign.refundRecord(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }

    /**
     * 操作日志
     *
     * @param orderId   查询信息
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param operation 操作
     * @param startTime 操作开始时间
     * @param endTime   操作结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @GetMapping(value = "/refund/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "操作日志", notes = "操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "子订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "operation", value = "操作", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "操作开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "操作结束时间", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<AfterSalesOrderDTO>> refundLog(@PathVariable(value = "pageNum") Integer pageNum,
                                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                                @RequestParam(value = "orderId", required = false) Long orderId,
                                                                @RequestParam(value = "operation", required = false) Integer operation,
                                                                @RequestParam(value = "startTime", required = false) String startTime,
                                                                @RequestParam(value = "endTime", required = false) String endTime) {
        PageVO<AfterSalesOrderDTO> page = orderFeign.refundLog(pageNum, pageSize, orderId, operation, startTime, endTime);
        return ResponseEntity.ok(page);
    }


    /**
     * @Author ycl
     * @Description 仅退款-租赁商品/实物商品
     * @Date 18:19 2019/8/21
     * @Param
     **/
    @PostMapping(value = "/refund/rental/{pageNum}/{pageSize}")
    @ApiOperation(value = "仅退款-租赁商品/实物商品", notes = "仅退款-租赁商品/实物商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", required = true, paramType = "body")
    })
    public Object rentalGoodsList(@PathVariable(value = "pageNum") Integer pageNum,
                                  @PathVariable(value = "pageSize") Integer pageSize,
                                  @RequestBody AfterSalesConditionDTO dto) {
        PageVO<AfterSalesOrderDTO> page = orderFeign.rentalGoodsList(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }


    /**
     * @Author ycl
     * @Description 租赁商品/实物商品 详情
     * @Date 10:24 2019/8/30
     * @Param
     **/
    @GetMapping(value = "/refund/rental/{id}")
    @ApiOperation(value = "租赁商品/实物商品 详情", notes = "租赁商品/实物商品 详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后单号", dataType = "Long", required = true, paramType = "path"),
    })
    public ResponseEntity<AfterSalesOrderDTO> getSalesDetailById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(orderFeign.getSalesDetailById(id));
    }


    /**
     * @Author ycl
     * @Description 售后订单审核记录列表
     * @Date 17:53 2019/8/23
     * @Param
     **/
    @PostMapping(value = "/refund/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "售后订单审核记录列表", notes = "售后订单审核记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", required = true, paramType = "body")
    })
    public ResponseEntity<PageVO<OrderAuditLogDTO>> orderAuditLogList(
            @PathVariable(value = "pageNum") Integer pageNum,
            @PathVariable(value = "pageSize") Integer pageSize,
            @RequestBody AfterSalesConditionDTO dto) {
        PageVO<OrderAuditLogDTO> page = orderFeign.orderAuditLogList(pageNum, pageSize, dto);
        return ResponseEntity.ok(page);
    }


    /**
     * @Author ycl
     * @Description 仅退款租赁商品全部-导出
     * @Date 11:11 2019/8/26
     * @Param
     **/
    @PostMapping(value = "/refund/rental/goods/export")
    @ApiOperation(value = "仅退款租赁商品导出-1：全部 2：待审核 3：审核记录", notes = "仅退款租赁商品导出-1：全部 2：待审核 3：审核记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", paramType = "body")
    })
    public Object exportRentalGoods(@RequestParam(value = "type") Integer type,
                                    @RequestBody AfterSalesConditionDTO dto) {
        dto.setType(type);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/refund/rental/goods/export";
        String title;
        if (type == 1) {
            title = "仅退款租赁商品-导出";
        } else if (type == 2) {
            title = "仅退款租赁商品-待审核";
        } else if (type == 3) {
            title = "仅退款租赁商品-审核列表";
        } else {
            throw new BadRequestException("参数错误");
        }
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", dto);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
        /*List<RentalGoodsExportDTO> list = orderFeign.exportRentalGoods(dto);
        if (CollectionUtil.isEmpty(list)) {
            throw new NotFoundException("未找到数据");
        }

        String header = "仅退款租赁商品全部-导出";
        String[] beanPropertys = new String[]{"salesId", "refer", "orderId", "mainOrderId",
                "terminal", "productCategoryName", "userId", "addresseeName", "distributorAccount", "distributorName",
                "engineerName", "salesTerminal", "num", "createTime", "cancelReason", "status", "auditee", "remark"};

        String[] titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "订单来源", "产品类目名称", "用户id",
                "收件人", "经销商账号", "经销商姓名", "安装工姓名", "售后申请端", "申请数量", "申请时间", "申请原因",
                "售后状态", "审核方", "备注"};

        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
        if (boo) {
            return ResponseEntity.noContent().build();
        }
        throw new YimaoException("导出失败");*/
    }


    /**
     * @Author ycl
     * @Description 仅退款租赁商品待审核-导出
     * @Date 11:38 2019/8/26
     * @Param
     **/
//    @PostMapping(value = "/refund/rental/goods/audited")
//    @ApiOperation(value = "仅退款租赁商品待审核-导出", notes = "仅退款租赁商品待审核-导出")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", paramType = "body")
//    })
//    public Object exportAuditedRentalGoods(@RequestBody AfterSalesConditionDTO dto,
//                                           HttpServletResponse response) {
//        List<RentalGoodsExportDTO> list = orderFeign.exportAuditedRentalGoods(dto);
//        if (CollectionUtil.isEmpty(list)) {
//            throw new NotFoundException("未找到数据");
//        }
//
//        String header = "仅退款租赁商品待审核-导出";
//        String[] beanPropertys = new String[]{"salesId", "refer", "orderId", "mainOrderId",
//                "terminal", "productCategoryName", "userId", "addresseeName", "distributorAccount", "distributorName",
//                "engineerName", "salesTerminal", "num", "createTime", "cancelReason", "remark"};
//
//        String[] titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "订单来源", "产品类目名称", "用户id",
//                "收件人", "经销商账号", "经销商姓名", "安装工姓名", "售后申请端", "申请数量", "申请时间", "申请原因", "备注"};
//
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
//        if (boo) {
//            return ResponseEntity.noContent().build();
//        }
//        throw new YimaoException("导出失败");
//    }


//    /**
//     * @Author ycl
//     * @Description 仅退款租赁商品审核记录-导出
//     * @Date 14:17 2019/8/26
//     * @Param
//     **/
//    @PostMapping(value = "/refund/rental/audit/record")
//    @ApiOperation(value = "仅退款租赁商品审核记录-导出", notes = "仅退款租赁商品审核记录-导出")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", paramType = "body")
//    })
//    public Object exportAuditedRecord(@RequestBody AfterSalesConditionDTO dto, HttpServletResponse response) {
//        List<RentalGoodsExportDTO> list = orderFeign.exportAuditedRecord(dto);
//        if (CollectionUtil.isEmpty(list)) {
//            throw new NotFoundException("未找到数据");
//        }
//
//        String header = "仅退款租赁商品审核记录-导出";
//        String[] beanPropertys = new String[]{"salesId", "refer", "orderId", "mainOrderId",
//                "createTime", "cancelReason", "buyer", "handleTime", "operationStatus", "auditReason",
//                "remark"};
//
//        String[] titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "售后申请时间", "申请原因", "审核人",
//                "审核处理时间", "审核状态", "审核不通过原因", "备注"};
//
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanPropertys, response);
//        if (boo) {
//            return ResponseEntity.noContent().build();
//        }
//        throw new YimaoException("导出失败");
//    }

    /**
     * @Author ycl
     * @Description 仅退款实物商品-1：全部 2：待审核 3：审核记录
     * @Date 15:15 2019/8/26
     * @Param
     **/
    @PostMapping(value = "/refund/matter/goods/export")
    @ApiOperation(value = "仅退款实物商品-1：全部 2：待审核 3：审核记录")
    @ApiImplicitParam(name = "dto", value = "查询实体类", dataType = "AfterSalesConditionDTO", paramType = "body")
    public Object exportMatterGoods(@RequestParam(value = "type") Integer type,
                                    @RequestBody AfterSalesConditionDTO dto) {
        dto.setType(type);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/refund/matter/goods/export";
        String title;
        if (type == 1) {
            title = "仅退款实物商品-导出";
        } else if (type == 2) {
            title = "仅退款实物商品-待审核";
        } else if (type == 3) {
            title = "仅退款实物商品-审核列表";
        } else {
            throw new BadRequestException("参数错误");
        }
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", dto);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * @Author ycl
     * @Description 审核记录详情
     * @Date 11:03 2019/8/28
     * @Param
     **/
    @GetMapping(value = "/refund/audit/examine/{id}")
    @ApiOperation(value = "审核记录详情", notes = "审核记录详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审核记录id", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<ExamineRecordDTO> getExamineRecordDetailById(@PathVariable Long id) {
        ExamineRecordDTO recordDTO = orderFeign.getExamineRecordDetailById(id);
        return ResponseEntity.ok(recordDTO);
    }


    /**
     * @Author ycl
     * @Description 退款订单-审核通过
     * @Date 14:12 2019/8/28
     * @Param
     **/
    @PatchMapping(value = "/order/audit/record")
    @ApiOperation(value = "退款订单-审核通过", notes = "退款订单-审核通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后单号id", dataType = "Long", paramType = "query")
    })
    public Object auditAdopt(@RequestParam(name = "id") Long id) {
        orderFeign.auditAdopt(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 仅退款订单批量-审核通过
     * @Date 14:18 2019/8/28
     * @Param
     **/
    @PatchMapping(value = "/order/batch/audit/record")
    @ApiOperation(value = "仅退款订单批量-审核通过", notes = "仅退款订单批量-审核通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "售后订单主键", required = true, dataType = "Long", paramType = "query", allowMultiple = true)
    })
    public Object batchAuditAdopt(@RequestParam(name = "ids") List<Long> ids) {
        orderFeign.batchAuditAdopt(ids);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 退款订单-审核不通过
     * @Date 14:19 2019/8/28
     * @Param
     **/
    @PatchMapping(value = "/order/audit/noPassage")
    @ApiOperation(value = "仅退款订单批量-审核不通过", notes = "仅退款订单批量-审核不通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后单号id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditReason", value = "不通过原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "detailReason", value = "详细描述", dataType = "String", paramType = "query")
    })
    public Object auditNoPassage(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "auditReason") String auditReason,
                                 @RequestParam(name = "detailReason", required = false) String detailReason) {
        if (StringUtil.isEmpty(auditReason)) {
            throw new BadRequestException("不通过原因不能为空");
        }
        orderFeign.auditNoPassage(id, auditReason, detailReason);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Author ycl
     * @Description 仅退款订单批量-审核不通过
     * @Date 14:22 2019/8/28
     * @Param
     **/
    @PatchMapping(value = "/order/batch/audit/noPassage")
    @ApiOperation(value = "仅退款订单批量-审核不通过", notes = "仅退款订单批量-审核不通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "售后订单主键", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "auditReason", value = "不通过原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "detailReason", value = "详细描述", dataType = "String", paramType = "query")
    })
    public Object batchAuditNoPassage(@RequestParam(name = "ids") List<Long> ids,
                                      @RequestParam(name = "auditReason") String auditReason,
                                      @RequestParam(name = "detailReason") String detailReason) {
        orderFeign.batchAuditNoPassage(ids, auditReason, detailReason);
        return ResponseEntity.noContent().build();
    }

    /***
     * 退款记录详情
     * @param id
     * @return
     */
    @GetMapping(value = "/refund/detail")
    @ApiOperation(value = "退款记录详情", notes = "退款记录详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后单号", dataType = "Long", required = true, paramType = "query")
    })
    public ResponseEntity<RefundDetailDTO> refundedetail(@RequestParam(name = "id") Long id) {
        RefundDetailDTO recordDTO = orderFeign.getRefundeDetail(id);
        return ResponseEntity.ok(recordDTO);
    }


}
