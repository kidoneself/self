package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawSubDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.order.WithdrawQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提现
 *
 * @author hhf
 * @date 2019/2/28
 */
@RestController
@Slf4j
@Api(tags = "OrderWithdrawController")
public class OrderWithdrawController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 应提现订单
     *
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param orderId   订单号
     * @param userId    用户Id
     * @param phone     用户手机号
     * @param startTime 订单完成开始时间
     * @param endTime   订单完成结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/23
     */
    @GetMapping(value = "/withdraw/{pageNum}/{pageSize}")
    @ApiOperation(value = "应提现订单", notes = "应提现订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "订单号(主/子/续)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "提现状态", dataType = "Long", paramType = "query")
    })
    public ResponseEntity<PageVO<OrderWithdrawDTO>> withdrawList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                                 @RequestParam(value = "orderId", required = false) String orderId,
                                                                 @RequestParam(value = "userId", required = false) Integer userId,
                                                                 @RequestParam(value = "phone", required = false) String phone,
                                                                 @RequestParam(value = "startTime", required = false) String startTime,
                                                                 @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                                 @RequestParam(value = "status", required = false) Integer status,
                                                                 @RequestParam(value = "endTime", required = false) String endTime

    ) {
        PageVO<OrderWithdrawDTO> page = orderFeign.withdrawList(pageNum, pageSize, orderId, userId, phone, startTime, incomeType, status, endTime);
        return ResponseEntity.ok(page);
    }

    /**
     * 提现审核列表
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 提现订单号
     * @param userId         用户Id
     * @param phone          用户手机号
     * @param startTime      申请提现开始时间
     * @param endTime        申请提现结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/2/23
     */
    @GetMapping(value = "/withdraw/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现审核列表", notes = "提现审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "partnerTradeNo", value = "提现订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请提现开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请提现结束时间", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawAuditList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                                    @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                                                    @RequestParam(value = "userId", required = false) Integer userId,
                                                                    @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                                    @RequestParam(value = "phone", required = false) String phone,
                                                                    @RequestParam(value = "startTime", required = false) String startTime,
                                                                    @RequestParam(value = "endTime", required = false) String endTime) {
        PageVO<WithdrawSubDTO> page = orderFeign.withdrawAuditList(pageNum, pageSize, partnerTradeNo, userId, incomeType, phone, startTime, endTime);
        return ResponseEntity.ok(page);
    }


    /**
     * 提现审核（单/批量）
     *
     * @param ids         提现审核主键
     * @param auditStatus 1-通过 ; 2-不通过
     * @param auditReason 审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2019/3/1
     */
    @PatchMapping(value = "/withdraw/audit/batch")
    @ApiOperation(value = "提现审核（单/批量）", notes = "提现审核（单/批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "企业审核ID", required = true, dataType = "String", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditReason", value = "审核原因", dataType = "String", paramType = "query")
    })
    public ResponseEntity batchAudit(@RequestParam(name = "ids") List<String> ids,
                                     @RequestParam(name = "auditStatus") Integer auditStatus,
                                     @RequestParam(name = "auditReason", required = false) String auditReason) {
        String message = orderFeign.batchAudit(ids, auditStatus, auditReason);
        return ResponseEntity.ok(message);
    }

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/3/4
     */
    @PostMapping(value = "/withdraw/record/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现记录列表", notes = "提现记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                                     @RequestBody WithdrawQueryDTO withdrawQueryDTO) {

        PageVO<WithdrawSubDTO> page = orderFeign.withdrawRecordList(pageNum, pageSize, withdrawQueryDTO);
        return ResponseEntity.ok(page);
    }

    /**
     * 提现明细列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/3/4
     */
    @PostMapping(value = "/withdraw/record/detail/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现明细列表", notes = "提现明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordDetailList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                           @PathVariable(value = "pageSize") Integer pageSize,
                                                                           @RequestBody WithdrawQueryDTO withdrawQueryDTO) {

        PageVO<WithdrawSubDTO> page = orderFeign.withdrawRecordDetailList(pageNum, pageSize, withdrawQueryDTO);
        return ResponseEntity.ok(page);
    }


    /**
     * 提现操作日志
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 自提现单号
     * @param withdrawFlag   提现成功与否
     * @param startTime      操作开始时间
     * @param endTime        操作结束时间
     * @author hhf
     * @date 2019/3/4
     */
    @GetMapping(value = "/withdraw/record/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "提现操作日志", notes = "提现操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "partnerTradeNo", value = "子提现订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "withdrawFlag", value = "提现成功标志 1-成功 2-失败", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请提现开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请提现结束时间", dataType = "String", paramType = "query")
    })
    public ResponseEntity<PageVO<WithdrawSubDTO>> withdrawRecordLogList(@PathVariable(value = "pageNum") Integer pageNum,
                                                                        @PathVariable(value = "pageSize") Integer pageSize,
                                                                        @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                                                        @RequestParam(value = "withdrawFlag", required = false) Integer withdrawFlag,
                                                                        @RequestParam(value = "startTime", required = false) String startTime,
                                                                        @RequestParam(value = "endTime", required = false) String endTime) {

        PageVO<WithdrawSubDTO> page = orderFeign.withdrawRecordLogList(pageNum, pageSize, partnerTradeNo, withdrawFlag, startTime, endTime);
        return ResponseEntity.ok(page);
    }

    /**
     * 应提现订单导出
     *
     * @param orderId   订单号
     * @param userId    用户Id
     * @param phone     用户手机号
     * @param startTime 订单完成开始时间
     * @param endTime   订单完成结束时间
     * @author hhf
     * @date 2019/5/6
     */
    @GetMapping(value = "/withdraw/export")
    @ApiOperation(value = "应提现订单导出", notes = "应提现订单导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query")
    })
    public Object withdrawListExport(@RequestParam(value = "orderId", required = false) String orderId,
                                     @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                     @RequestParam(value = "status", required = false) Integer status,
                                     @RequestParam(value = "userId", required = false) Integer userId,
                                     @RequestParam(value = "phone", required = false) String phone,
                                     @RequestParam(value = "startTime", required = false) String startTime,
                                     @RequestParam(value = "endTime", required = false) String endTime) {




        WithdrawQuery query = new WithdrawQuery();
        query.setOrderId(orderId);
        query.setIncomeType(incomeType);
        query.setStatus(status);
//        query.setRefer(refer);
        query.setUserId(userId);
        query.setPhone(phone);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/withdraw/export";
        ExportRecordDTO record = exportRecordService.save(url, "提现管理-应提现订单");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
//        List<WithdrawExportDTO> withdrawDTOS = orderFeign.withdrawListExport(orderId, userId, phone, startTime, endTime);
//        if (CollectionUtil.isEmpty(withdrawDTOS)) {
//            throw new NotFoundException("没有数据可以导出");
//        }
//        String[] titles = {"主订单单号", "子订单号/续费单号", "产品类目", "产品数量", "订单金额", "支付方式", "订单完成时间", "可提现金额", "收益类型", "用户下单时身份", "用户姓名", "用户手机号"};
//        String[] beanProperties = {"mainOrderId", "orderId", "productCategoryName", "productNum", "orderFee", "payType", "orderCompleteTime", "withdrawFee", "incomeType", "userType", "userName",
//                "phone"};
//        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "提现管理-应提现订单";
//        boolean boo = ExcelUtil.exportSXSSF(withdrawDTOS, header, titles.length - 1, titles, beanProperties, response);
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
    }

    /**
     * 提现审核列表导出
     *
     * @param partnerTradeNo 提现订单号
     * @param userId         用户Id
     * @param phone          用户手机号
     * @param startTime      申请提现开始时间
     * @param endTime        申请提现结束时间
     * @author hhf
     * @date 2019/5/6
     */
    @GetMapping(value = "/withdraw/audit/export")
    @ApiOperation(value = "提现审核列表导出", notes = "提现审核列表导出")
    @ApiImplicitParams({@ApiImplicitParam(name = "partnerTradeNo", value = "提现订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "申请提现开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "申请提现结束时间", dataType = "String", paramType = "query")})
    public Object withdrawAuditListExport(@RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                          @RequestParam(value = "userId", required = false) Integer userId,
                                          @RequestParam(value = "phone", required = false) String phone,
                                          @RequestParam(value = "startTime", required = false) String startTime,
                                          @RequestParam(value = "endTime", required = false) String endTime) {
        WithdrawQuery query = new WithdrawQuery();
        query.setPartnerTradeNo(partnerTradeNo);
        query.setUserId(userId);
        query.setPhone(phone);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/withdraw/audit/export";
        ExportRecordDTO record = exportRecordService.save(url, "提现管理-提现审核");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
//        List<WithdrawExportDTO> withdrawExportDTOS = orderFeign.withdrawAuditListExport(partnerTradeNo, userId, phone, startTime, endTime);
//        if (CollectionUtil.isEmpty(withdrawExportDTOS)) {
//            throw new NotFoundException("没有数据可以导出");
//        }
//        String[] titles = {"主提现单号", "子提现单号", "主订单号", "子订单号/续费单号", "工单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "产品类型", "产品范围", "产品型号", "产品公司", "订单金额", "支付时间",
//                "订单完成时间", "用户身份", "用户ID", "用户姓名", "用户手机号", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号",
//                "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区"};
//        String[] beanProperties = {"mainPartnerTradeNo", "id", "mainOrderId", "orderId", "workOrderId", "withdrawFee", "applyTime", "status", "withdrawType", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "orderFee", "payTime",
//                "orderCompleteTime", "userType", "userId", "userName", "phone", "distributorType", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount",
//                "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion"};
//        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "提现管理-提现审核";
//        boolean boo = ExcelUtil.exportSXSSF(withdrawExportDTOS, header, titles.length - 1, titles, beanProperties, response);
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
    }

    /**
     * 提现记录列表导出
     *
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/5/8
     */
    @PostMapping(value = "/withdraw/record/export")
    @ApiOperation(value = "提现记录列表导出", notes = "提现记录列表导出")
    @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    public Object withdrawRecordListExport(@RequestBody WithdrawQueryDTO withdrawQueryDTO) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/withdraw/record/export";
        ExportRecordDTO record = exportRecordService.save(url, "提现记录");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", withdrawQueryDTO);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
    	
    	/* List<WithdrawExportDTO> exportDTOS = orderFeign.withdrawRecordListExport(withdrawQueryDTO);
        if (CollectionUtil.isEmpty(exportDTOS)) {
            throw new NotFoundException("没有数据可以导出");
        }
        String[] titles = {"主提现单号 ", "子提现单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "流水号", "产品公司", "提现到账时间", "用户身份", "用户ID",
                "审核时间", "审核人"};
        String[] beanProperties = {"mainPartnerTradeNo", "id", "withdrawFee", "applyTime", "status", "withdrawType", "paymentNo",
                "productCompanyName", "paymentTime", "userType", "userId", "auditTime", "updater"};
        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "提现管理-提现记录";
        boolean boo = ExcelUtil.exportSXSSF(exportDTOS, header, titles.length - 1, titles, beanProperties, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }

    /**
     * 提现明细列表导出
     *
     * @param withdrawQueryDTO 查询信息
     * @author hhf
     * @date 2019/3/4
     */
    @PostMapping(value = "/withdraw/record/detail/export", consumes = "application/json")
    @ApiOperation(value = "提现明细列表导出", notes = "提现明细列表导出")
    @ApiImplicitParam(name = "withdrawQueryDTO", value = "提现查询信息", required = true, dataType = "WithdrawQueryDTO", paramType = "body")
    public Object withdrawRecordDetailListExport(@RequestBody WithdrawQueryDTO withdrawQueryDTO) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/withdraw/record/detail/export";
        ExportRecordDTO record = exportRecordService.save(url, "提现管理-提现明细");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", withdrawQueryDTO);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());

//        List<WithdrawExportDTO> list = orderFeign.withdrawRecordDetailListExport(withdrawQueryDTO);
//        String[] titles = {"主提现单号", "子提现单号", "主订单号", "子订单号/续费单号", "工单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "流水号", "提现到账时间",
//                "产品类型", "产品范围", "产品型号", "产品公司", "用户身份", "用户ID", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区",
//                "是否有子账号", "子账号", "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "结算主体省市区", "结算主体公司名称", "付款说明", "审核时间"};
//        String[] beanProperties = {"mainPartnerTradeNo", "id", "mainOrderId", "orderId", "workOrderId", "withdrawFee", "applyTime", "status", "withdrawType", "paymentNo", "paymentTime",
//                "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "userType", "userId", "distributorType", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
//                "hasSubAccount", "subAccount", "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion", "subjectArea", "subjectCompany", "payInstructions", "auditTime"};
//        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "提现管理-提现明细";
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
    }
}
