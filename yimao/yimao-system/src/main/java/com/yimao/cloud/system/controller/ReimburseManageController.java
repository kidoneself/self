package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019-09-18
 */
@RestController
@Slf4j
@Api(tags = "ReimburseManageController")
public class ReimburseManageController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;


    @PostMapping("/reimburse/manage/online/{pageNum}/{pageSize}")
    @ApiOperation(value = "线上退款审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页显数", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "dto", value = "子订单dto", dataType = "OrderSubDTO", paramType = "body")

    })
    public ResponseEntity<PageVO<OrderSubDTO>> onlineReimburseManage(@PathVariable("pageNum") Integer pageNum,
                                                                     @PathVariable("pageSize") Integer pageSize,
                                                                     @RequestBody OrderSubDTO dto) {
        PageVO<OrderSubDTO> pages = orderFeign.onlineReimburseManagePage(pageNum, pageSize, dto);
        return ResponseEntity.ok(pages);


    }


    /**
     * 退款管理导出
     *
     * @param dto
     * @param response
     * @return
     */
    @PostMapping(value = {"/reimburse/manage/export"})
    @ApiOperation(value = "退款管理导出", notes = "退款管理导出")
    @ApiImplicitParam(name = "dto", value = "子订单dto", dataType = "OrderSubDTO", paramType = "body")
    public Object exportReimburse(@RequestBody OrderSubDTO dto, HttpServletResponse response) {
        String url = "/reimburse/manage/export";
        String title;
        if (dto.getQueryType() == 1) {
            title = "线上退款审核";
        } else {
            title = "线下退款审核";
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

//        List<refundManageExportDTO> resultList = orderFeign.exportReimburse(dto);
//        if (CollectionUtil.isEmpty(resultList)) {
//            throw new NotFoundException("未找到数据");
//        }
//        String header;
//        if (dto.getQueryType() == 1) {
//            header = "线上退款审核";
//        } else {
//            header = "线下退款审核";
//        }
//        String[] beanProperties = new String[]{
//                "afterSalesOrderId", "id", "mainOrderId", "terminal",
//                "productOneCategoryName", "productCategoryName", "productTwoCategoryName", "productCompanyName",
//                "payType", "userId", "addresseeName", "distributorAccount",
//                "distributorName", "cancelTime", "cancelReason", "count",
//                "fee", "formalitiesFee", "shouldReturn", "subStatus"};
//        String[] titles = new String[]{
//                "售后单号", "子订单号", "主订单号", "订单来源",
//                "产品类型", "产品型号", "型号范围", "产品公司",
//                "支付方式", "用户ID", "收货人", "经销商账户",
//                "经销商姓名", "售后申请时间", "申请原因", "申请数量",
//                "应退金额", "手续费", "可退金额", "售后状态"};
//        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
//        if (boo) {
//            return ResponseEntity.noContent().build();
//        }
//        throw new YimaoException("导出失败");
    }

    /**
     * 退款记录导出
     *
     * @param dto
     * @param response
     * @return
     */
    @PostMapping(value = {"/refund/record/export"})
    @ApiOperation(value = "退款记录导出", notes = "退款记录导出")
    @ApiImplicitParam(name = "dto", value = "子订单dto", dataType = "OrderSubDTO", paramType = "body")
    public Object exportRefundRecord(@RequestBody OrderSubDTO dto) {

		//保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/refund/record/export";
        ExportRecordDTO record = exportRecordService.save(url, "退款管理-退款记录");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", dto);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
//        List<refundManageExportDTO> resultList = orderFeign.exportRefund(dto);
//        if (CollectionUtil.isEmpty(resultList)) {
//            throw new NotFoundException("未找到数据");
//        }
//        String header = "退款记录";
//        String[] beanProperties = new String[]{
//                "afterSalesOrderId", "id", "mainOrderId", "terminal",
//                "productOneCategoryName", "productCategoryName", "productTwoCategoryName", "productCompanyName",
//                "payType", "userId", "addresseeName", "distributorAccount",
//                "distributorName", "cancelTime", "cancelReason", "count",
//                "fee", "formalitiesFee", "shouldReturn", "subStatus", "refundTradeNo", "financeTime", "financer"};
//        String[] titles = new String[]{
//                "售后单号", "子订单号", "主订单号", "订单来源",
//                "产品类型", "产品型号", "型号范围", "产品公司",
//                "支付方式", "用户ID", "收货人", "经销商账户",
//                "经销商姓名", "售后申请时间", "申请原因", "申请数量",
//                "应退金额", "手续费", "可退金额", "售后状态", "流水号", "退款审核时间", "审核人"};
//        boolean boo = ExcelUtil.exportSXSSF(resultList, header, titles.length - 1, titles, beanProperties, response);
//        if (boo) {
//            return ResponseEntity.noContent().build();
//        }
//        throw new YimaoException("导出失败");
    }


}
