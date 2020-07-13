package com.yimao.cloud.water.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.AliPayRequest;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderOperateLogDTO;
import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import com.yimao.cloud.pojo.dto.order.WechatPayRequest;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述：ORDER微服务远程调用类。
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:44
 */
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

    @GetMapping(value = "/workorder/{workOrderId}")
    WorkOrderDTO getWorkOrderById(@PathVariable("workOrderId") String workOrderId);

    @PostMapping(value = "/order/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveRenewWorkOrder(@RequestBody OrderRenewDTO renewOrder);

    @GetMapping(value = "/order/renew/{id}")
    OrderRenewDTO getRenewWorkOrder(@PathVariable("id") String id);

    /**
     * @param dto
     * @return
     * @description 更新维护工单信息
     * @author Liu Yi
     */
    @PutMapping(value = "/order/maintenanceWorkOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWorkOrderMaintenance(@RequestBody MaintenanceWorkOrderDTO dto);

    /**
     * @param sncode
     * @param state
     * @param workOrderCompleteStatus
     * @return
     * @description 根据SN查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/sn")
    List<MaintenanceWorkOrderDTO> getWorkOrderMaintenanceBySnCode(@RequestParam(value = "sncode", required = false) String sncode,
                                                                  @RequestParam(value = "state", required = false) Integer state,
                                                                  @RequestParam(value = "workOrderCompleteStatus", required = false) String workOrderCompleteStatus,
                                                                  @RequestParam(value = "source", required = false) Integer source);

    /**
     * @param id
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceById(@PathVariable("id") String id);

    /***
     * 功能描述:新增维护工单操作日志
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/order/maintenanceWorkOrderOperateLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createMaintenanceWorkOrderOperateLog(@RequestBody MaintenanceWorkOrderOperateLogDTO dto);

    /**
     * 微信扫码支付-统一下单
     */
    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    String wechatScanCodePay(@RequestBody WechatPayRequest payRequest);

    @PostMapping(value = "/alipay/tradeprecreate", consumes = MediaType.APPLICATION_JSON_VALUE)
    String aliScanCodePay(@RequestBody AliPayRequest payRequest);

    @GetMapping(value = "/workorder/checkcompletepay")
    boolean checkWorkOrder180ComplatePay(@RequestParam(value = "completeOutTradeNo") String completeOutTradeNo);

    @GetMapping(value = "/order/renew/currentrenewtimes")
    Integer getCurrentRenewTimes(@RequestParam(value = "deviceId") Integer deviceId);

    @PatchMapping(value = "/workorder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWorkOrderPart(@RequestBody WorkOrderDTO workOrderDTO);

    @PatchMapping(value = "/order/renew/changeSn")
    void updateRenewOrderSn(@RequestParam(value = "deviceId") Integer deviceId, @RequestParam(value = "oldSn") String oldSn, @RequestParam(value = "newSn") String newSn);
}
