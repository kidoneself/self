package com.yimao.cloud.system.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.SystemFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
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
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderRenewController")
public class OrderRenewController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private SystemFileService systemFileService;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 订单管理-续费列表-列表
     */
    @PostMapping(value = "/order/renew/{pageNum}/{pageSize}")
    @ApiOperation(value = "订单管理-续费列表-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true)
    })
    public Object getOrderRenewList(@RequestBody RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderFeign.getOrderRenewList(query, pageNum, pageSize);
    }

    /**
     * 订单管理-续费列表-详情
     */
    @GetMapping(value = "/order/renew/{id}/detail")
    @ApiOperation(value = "订单管理-续费列表-详情")
    @ApiImplicitParam(name = "id", value = "续费id", dataType = "String", paramType = "path", required = true)
    public Object getOrderRenewDetail(@PathVariable String id) {
        return orderFeign.getOrderRenewDetail(id);
    }

    /**
     * 订单管理-续费列表-重新提交支付凭证
     */
    @PatchMapping(value = "/order/renew/{id}/resubmit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "订单管理-续费列表-重新提交支付凭证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "续费id", dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "payType", value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；", dataType = "Long", paramType = "query", required = true)
    })
    public void resubmit(@PathVariable String id, @RequestParam Integer payType, @RequestPart(value = "attach") MultipartFile attach) {
        if (PayType.find(payType) == null) {
            throw new BadRequestException("支付方式选择错误。");
        }
        String payCredential = systemFileService.uploadAndSave(attach, "renew", "管理后台操作续费上传附件");
        if (StringUtil.isEmpty(payCredential)) {
            throw new YimaoException("保存支付凭证时出错。");
        }
        orderFeign.resubmit(id, payType, payCredential);
    }

    /**
     * 订单管理-续费列表-导出
     */
    @PostMapping(value = "/order/renew/export")
    @ApiOperation(value = "订单管理-续费列表-导出")
    public Object orderRenewExport(@RequestBody RenewOrderQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/renew/export";
        ExportRecordDTO record = exportRecordService.save(url, "续费列表");

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
     * 财务管理-续费订单对账-列表
     */
    @GetMapping(value = "/order/renew/finance/{pageNum}/{pageSize}")
    @ApiOperation(value = "财务管理-续费订单对账-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true)
    })
    public Object orderRenewFinanceList(RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderFeign.orderRenewFinanceList(query, pageNum, pageSize);
    }

    /**
     * 财务管理-续费订单对账-查看
     */
    @GetMapping(value = "/order/renew/finance/{id}/detail")
    @ApiOperation(value = "财务管理-续费订单对账-查看")
    @ApiImplicitParam(name = "id", value = "续费id", dataType = "String", paramType = "path", required = true)
    public Object getOrderRenewFinanceDetail(@PathVariable String id) {
        return orderFeign.getOrderRenewFinanceDetail(id);
    }

    /**
     * 财务管理-续费订单对账-导出
     */
    @PostMapping(value = "/order/renew/finance/export")
    @ApiOperation(value = "财务管理-续费订单对账-导出")
    public Object orderRenewFinanceExport(@RequestBody RenewOrderQuery query) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/renew/finance/export";
        ExportRecordDTO record = exportRecordService.save(url, "续费订单对账");

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
     * 财务管理-支付审核-续费支付待审核-列表
     */
    @GetMapping(value = "/order/renew/paycheck/{pageNum}/{pageSize}")
    @ApiOperation(value = "财务管理-支付审核-续费支付待审核-列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", paramType = "path", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", paramType = "path", required = true)
    })
    public Object orderRenewPayCheck(RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        log.info("查询 参数=" + JSON.toJSONString(query));
        return orderFeign.orderRenewPayCheckList(query, pageNum, pageSize);
    }

    /**
     * 财务管理-支付审核-续费支付待审核-列表导出
     */
    @PostMapping(value = "/order/renew/paycheckExport")
    @ApiOperation(value = "财务管理-支付审核-续费支付待审核-列表导出")
    @ApiImplicitParam(name = "query", value = "查询信息", dataType = "RenewOrderQuery", paramType = "query")
    public Object orderRenewPayCheckExport(@RequestBody RenewOrderQuery query) {

        String url = "/order/renew/paycheckExport";
        ExportRecordDTO record = exportRecordService.save(url, "续费支付待审核");

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
     * 财务管理-支付审核-续费支付待审核-审核（单个）
     */
    @PatchMapping(value = "/order/renew/{id}/paycheck/single")
    @ApiOperation(value = "财务管理-支付审核-续费支付待审核-审核（单个）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "续费id", dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "pass", value = "审核通过：true-审核通过；false-审核不通过；", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "reason", value = "取消订单原因", dataType = "String", paramType = "query")
    })
    public void orderRenewPayCheckSingle(@PathVariable String id, @RequestParam Boolean pass, @RequestParam(required = false) String reason) {
        orderFeign.orderRenewPayCheckSingle(id, pass, reason);
    }

}
