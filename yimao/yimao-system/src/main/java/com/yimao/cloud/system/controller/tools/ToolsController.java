package com.yimao.cloud.system.controller.tools;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.WaterFeign;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/30
 */
@RestController
public class ToolsController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/manualTool/createDevicePlanAndIncome")
    public Object renewOrderCallbackFail(@RequestBody PayRecordDTO payRecord) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.renewOrderDoBy(payRecord);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping(value = "/manualTool/allotRenewIncome")
    public Object allotRenewIncome(@RequestParam String renewOrderIdStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.allotRenewIncome(renewOrderIdStr);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping(value = "/manualTool/changeDeviceModel")
    public Object changeDeviceModel(@RequestParam String workOrderId, @RequestParam Integer productId, @RequestParam Integer costId) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.changeDeviceModel(workOrderId, productId, costId);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    /**
     * 手动修改扣费计划
     */
    @PostMapping(value = "/manualTool/changePlan")
    public Object changePlan(@RequestParam Integer deviceId) {
        Map<String, Object> map = new HashMap<>();
        try {
            waterFeign.changePlan(deviceId);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    /**
     * 手动进行订单支付回调操作
     */
    @PostMapping(value = "/manualTool/orderPayCallback")
    public Object orderPayCallback(@RequestParam String mainOrderId) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.orderPayCallback(mainOrderId);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    /**
     * 手动进行工单支付回调操作
     */
    @PostMapping(value = "/manualTool/workOrderPayCallback")
    public Object workOrderPayCallback(@RequestParam String mainOrderId) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.workOrderPayCallback(mainOrderId);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    /**
     * 手动进行续费回调操作
     */
    @PostMapping(value = "/manualTool/renewOrderPayCallback")
    public Object renewOrderPayCallback(@RequestParam String mainOrderId) {
        Map<String, Object> map = new HashMap<>();
        try {
            orderFeign.renewOrderPayCallback(mainOrderId);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping(value = "/manualTool/syncWorkOrderToBaide")
    public Object syncWorkOrderToBaide(@RequestParam String workOrderIdStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            String[] split = workOrderIdStr.split(",");
            for (String workOrderId : split) {
                rabbitTemplate.convertAndSend(RabbitConstant.SYNC_WORK_ORDER, workOrderId);
            }
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping(value = "/manualTool/allotHra")
    public Object allotHra(@RequestParam String deviceId, @RequestParam String ticketNo, @RequestParam String stationId, @RequestParam String orderId) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, String> map1 = new HashMap<>();
            map1.put("deviceId", deviceId);
            map1.put("ticketNo", ticketNo);
            map1.put("stationId", stationId);
            map1.put("orderId", orderId);
            rabbitTemplate.convertAndSend(RabbitConstant.HRA_ALLOT, map1);
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
        }
        return map;
    }

}
