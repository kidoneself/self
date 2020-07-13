package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.RemoteCallException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 开票信息
 *
 * @author hhf
 * @date 2019/1/17
 */
@RestController
@Slf4j
@Api(tags = "OrderInvoiceController")
public class OrderInvoiceController {

    @Resource
    private OrderFeign orderFeign;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 分页查询经销商信息
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/16
     */
    @GetMapping(value = "order/invoice/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询发票信息", notes = "分页查询发票信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageQueryInvoice(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           OrderInvoiceQueryDTO query) {

        PageVO<OrderInvoiceDTO> page = orderFeign.pageQueryInvoice(pageNum, pageSize, query);
        if (page == null) {
            throw new RemoteCallException();
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 根据主键ID查询发票信息
     *
     * @param id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/17
     */
    @GetMapping(value = "order/invoice/{id}")
    @ApiOperation(value = "根据主键ID查询发票信息", notes = "根据主键ID查询发票信息")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity getDistributorById(@PathVariable("id") Integer id) {
        OrderInvoiceDTO dto = orderFeign.getInvoiceById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * @param
     * @return org.springframework.http.ResponseEntity
     * @description 查询发票信息导出
     * @author Liu long jie
     * @date 2019/9/4
     */
    @PostMapping(value = "/order/invoice/export", consumes = "application/json")
    @ApiOperation(value = "查询发票信息导出", notes = "查询发票信息导出")
    public Object invoiceExport(@RequestBody OrderInvoiceQueryDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/invoice/export";
        ExportRecordDTO record = exportRecordService.save(url, "发票信息列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
/*
        List<OrderInvoiceExportDTO> list = orderFeign.invoiceExport(query);
        String header = "开票管理导出";
        String[] beanProperties = new String[]{"workOrderId", "isTake", "province", "city", "region", "count", "orderTimeStr",
                "payTypeStr", "statusStr", "realName", "distributorPhone", "distributorAddress", "distributorStation", "recommendName", "recommendAddress", "recommendStation", "serviceEngineerName", "serviceEngineerPhone", "stationName", "acceptTimeStr", "tradeNo"
                , "costName", "modelPrice", "openAccountFee", "deviceModel", "snCode", "deviceActiveTimeStr", "payTimeStr", "userName", "userPhone", "dispatchTypeStr", "payTerminalStr", "completeTimeStr", "pickTimeStr",
                "logisticsCode", "billEmail", "isBilling", "invoiceTypeStr", "invoiceHeadStr", "dutyNo", "bankName", "bankAccount", "billFee", "companyName", "billAddress", "billPhone", "billTimeStr", "distributorTypeName", "paymentType"};
        String[] titles = new String[]{"工单号", "提货状态", "省", "市", "区", "水机数量", "下单时间",
                "支付方式", "状态", "经销商姓名", "经销商联系方式", "经销商归属地", "经销商服务站", "经销商推荐人", "推荐人归属地", "推荐人服务站", "客服姓名", "客服联系方式", "服务站", "客服接单时间", "交易单号"
                , "计费方式", "计费金额", "开户费", "商品类型", "SN码", "设备添加时间", "支付时间", "用户姓名", "用户联系方式", "派送方式", "支付端", "完成时间", "提货时间",
                "物流编码", "客户邮箱", "是否开票", "发票类型", "发票抬头", "税号", "开户行", "开户号", "开票金额", "公司名称", "地址", "电话", "开票时间", "经销商类型", "付费类型"};
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/
    }
}
