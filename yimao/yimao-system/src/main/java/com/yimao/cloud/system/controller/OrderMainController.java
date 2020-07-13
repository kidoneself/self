package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = "OrderMainController")
public class OrderMainController {
    @Resource
    private OrderFeign orderFeign;

    @Resource
    private ExportRecordService exportRecordService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 线下订单审核(立即支付)
     */
    @PostMapping(value = "/order/main/paycheckList/{pageNum}/{pageSize}")
    @ApiOperation(value = "线下订单审核-立即支付-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "OrderMainDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<OrderMainDTO>> orderMainPayCheckList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody OrderMainDTO query) {
        PageVO<OrderMainDTO> vo = orderFeign.orderMainPayCheckList(pageNum, pageSize, query);
        return ResponseEntity.ok(vo);
    }

    /**
     * 线下订单审核(货到付款)
     */
    @PostMapping(value = "/order/main/deliveryPaycheckList/{pageNum}/{pageSize}")
    @ApiOperation(value = "线下订单审核-货到付款-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "WorkOrderQueryDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<WorkOrderDTO>> orderDeliveryPayCheckList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody WorkOrderQueryDTO query) {
        PageVO<WorkOrderDTO> vo = orderFeign.orderDeliveryPayCheckList(pageNum, pageSize, query);
        return ResponseEntity.ok(vo);
    }

    /**
     * 线下商品支付审核
     *
     * @param ids
     * @param pass
     * @param reason
     */
    @PatchMapping(value = "/order/main/paycheck/single")
    @ApiOperation(value = "财务管理-支付审核-线下商品支付待审核-审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单或工单id", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "pass", value = "审核通过：true-审核通过；false-审核不通过；", dataType = "Boolean", paramType = "query", required = true),
            @ApiImplicitParam(name = "reason", value = "取消订单原因", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payTerminal", value = "支付类型：1-立即支付；2-货到付款", dataType = "Integer", paramType = "query")
    })
    public Object orderMainPayCheckSingle(@RequestParam String id, @RequestParam Boolean pass, @RequestParam(required = false) String reason, @RequestParam(defaultValue = "1") Integer payTerminal, @RequestParam(required = false) String userPhone) {
        orderFeign.orderMainPayCheckSingle(id, pass, reason, payTerminal, userPhone);

        return ResponseEntity.noContent().build();


    }

    @PostMapping(value = "/order/main/paycheckRecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "线下订单审核-审核记录-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "orderPayCheckDTO", value = "筛选参数", required = false, dataType = "OrderPayCheckDTO", paramType = "body")
    })
    public ResponseEntity<PageVO<OrderPayCheckDTO>> paycheckRecord(@PathVariable Integer pageNum, @PathVariable Integer pageSize, @RequestBody OrderPayCheckDTO orderPayCheckDTO) {

        PageVO<OrderPayCheckDTO> vo = orderFeign.paycheckRecord(pageNum, pageSize, orderPayCheckDTO);
        return ResponseEntity.ok(vo);
    }

    @GetMapping(value = "/order/main/paycheckRecord/{id}/info")
    @ApiOperation(value = "线下订单审核-审核记录-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审核id", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "orderType", value = "订单类型：1-普通订单(立即支付) 2-水机续费订单 3-工单(货到付款)", dataType = "Integer", paramType = "query")
    })
    public ResponseEntity<OrderPayCheckDTO> paycheckRecordInfo(@PathVariable("id") Integer id, @RequestParam(value = "orderType") Integer orderType) {

        OrderPayCheckDTO vo = orderFeign.paycheckRecordInfo(id, orderType);
        return ResponseEntity.ok(vo);
    }


    /**
     * 线下订单审核(立即支付导出)
     */
    @PostMapping(value = "/order/main/paycheckList/export")
    @ApiOperation(value = "线下订单审核-立即支付-导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "OrderMainDTO", paramType = "body")
    public Object orderMainPayCheckExport(@RequestBody OrderMainDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/main/paycheckList/export";
        ExportRecordDTO record = exportRecordService.save(url, "线下立即支付订单审核");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());

//	    	Object obj=orderFeign.orderMainPayCheckExport(query);
//	    	
//	    	List<OrderCheckExport> exportList=JSONObject.parseArray(JSONObject.toJSONString(obj), OrderCheckExport.class);
//	    	
//			if (CollectionUtil.isEmpty(exportList)) {
//		           throw new NotFoundException("未找到数据");
//		       }
//		    String header = "线下订单审核（立即支付）";
//
//		    String[] beanProperties = new String[]{"id", "productCompanyName", "count", "orderAmountFee", "payStatus", "payType", "payCredentialSubmitTime",
//	        		"payTerminal"};
//	        String[] titles = new String[]{"主订单号", "产品公司", "数量", "实付款", "支付状态", "支付方式", "提交凭证时间", "支付类型"};
//	        
//	        boolean boo = ExcelUtil.exportSXSSF(exportList, header, titles.length - 1, titles, beanProperties, response);
//	        if (boo) {
//	        	 return ResponseEntity.noContent().build(); 
//	        }
//	        throw new YimaoException("导出失败");  

    }

    /**
     * 线下订单审核(货到付款导出)
     */
    @PostMapping(value = "/order/main/deliveryPaycheckList/export")
    @ApiOperation(value = "线下订单审核-货到付款-导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "WorkOrderQueryDTO", paramType = "body")
    public Object orderDeliveryPayCheckExport(@RequestBody WorkOrderQueryDTO query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/main/deliveryPaycheckList/export";
        ExportRecordDTO record = exportRecordService.save(url, "线下货到付款订单审核");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());

//	    	Object obj=orderFeign.orderDeliveryPayCheckExport(query);
//	    	
//	    	List<OrderCheckExport> exportList=JSONObject.parseArray(JSONObject.toJSONString(obj), OrderCheckExport.class);
//	    	
//			if (CollectionUtil.isEmpty(exportList)) {
//		           throw new NotFoundException("未找到数据");
//		       }
//			
//			String header = "线下订单审核（货到付款）";
//			
//		    String[] beanProperties = new String[]{"id", "subOrderId", "count", "fee", "status", "payType", "createTime","payTerminal"};
//	        String[] titles = new String[]{"工单号", "子订单号", "数量", "实付款", "状态", "支付方式", "下单时间", "支付类型"};
//	        
//	        boolean boo = ExcelUtil.exportSXSSF(exportList, header, titles.length - 1, titles, beanProperties, response);
//	        if (boo) {
//	        	 return ResponseEntity.noContent().build(); 
//	        }
//	        throw new YimaoException("导出失败");  
//	       
    }

    @PostMapping(value = "/order/main/paycheckRecordExport")
    @ApiOperation(value = "线下订单审核-审核记录-列表导出")
    @ApiImplicitParam(name = "query", value = "筛选参数", dataType = "OrderPayCheckDTO", paramType = "body")
    public Object paycheckRecordExport(@RequestBody OrderPayCheckDTO query) {
        String url = "/order/main/paycheckRecordExport";
        ExportRecordDTO record = exportRecordService.save(url, "线下订单审核记录");

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
