package com.yimao.cloud.out.feign;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * 描述：订单微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

    /**
     * 描述：获取安装工某个状态的工单数量
     *
     * @param engineerId 安装工ID
     * @param status     工单状态
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     **/
    @RequestMapping(value = "/workorder/count/engineer/{engineerId}/status", method = RequestMethod.GET)
    Integer countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
                                       @RequestParam(value = "status", required = false) Integer status);

    /**
     * 查询已完成的安装工单信息
     */
    @GetMapping(value = "/workorder/complete")
    List<WorkOrderDTO> getWorkOrderCompleteList(@RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                @RequestParam(value = "consumerName", required = false) String consumerName,
                                                @RequestParam(value = "consumerPhone", required = false) String consumerPhone,
                                                @RequestParam(value = "deviceSncode", required = false) String deviceSncode);

    /**
     * 描述：获取安装工某段时间的工单数量
     *
     * @param engineerId 安装工ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     */
    @RequestMapping(value = "/workorder/count/engineer/{engineerId}/time", method = RequestMethod.GET)
    Integer countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
                                       @RequestParam(value = "startTime") Date startTime,
                                       @RequestParam(value = "endTime") Date endTime);

    /**
     * 描述：获取安装工某段时间某类产品的工单数量
     *
     * @param engineerId 安装工ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param productIds 产品ID集合
     * @Creator Zhang Bo
     * @CreateTime 2019/3/9 12:03
     */
    @RequestMapping(value = "/workorder/count/engineer/{engineerId}/status/time/product", method = RequestMethod.GET)
    Integer countWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId,
                                       @RequestParam(value = "status") Integer status,
                                       @RequestParam(value = "startTime") Date startTime,
                                       @RequestParam(value = "endTime") Date endTime,
                                       @RequestParam(value = "productIds") List<Integer> productIds);


    /** ----------------------------创建工单 start-----------------------------*/

    /**
     * 描述：根据工单id获取工单信息
     *
     * @param workOrderId 工单ID
     **/
    @GetMapping(value = "/workorder/{workOrderId}")
    WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId);

    /**
     * 修改安装工单
     *
     * @param workOrderDTO 工单对象
     */
    @PutMapping(value = "/workorder", consumes = MediaType.APPLICATION_JSON_VALUE)
    WorkOrderDTO updateWorkOrder(@RequestBody WorkOrderDTO workOrderDTO);

    /**
     * 修改安装工单局部字段
     *
     * @param workOrderDTO 工单对象
     */
    @PatchMapping(value = "/workorder", consumes = MediaType.APPLICATION_JSON_VALUE)
    WorkOrderDTO updateWorkOrderPart(@RequestBody WorkOrderDTO workOrderDTO);

    /**
     * 根据条件查询安装工单信息
     */
    @RequestMapping(value = "/workorder/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderDTO> getWorkOrderList(@RequestBody WorkOrderQueryDTO workOrderQueryDTO, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param payId
     * @description 根据支付记录id查询支付记录信息
     */
    @GetMapping(value = {"/order/pay/record/{payId}"})
    PayRecordDTO findPayRecordById(@PathVariable("payId") Integer payId);

    /**
     * 根据订单id查询订单信息
     */
    @GetMapping(value = "/order/sub/{orderId}")
    OrderSubDTO getOrder(@PathVariable(value = "orderId") Long orderId);

    /**
     * 工单评分内容修改
     *
     * @param workOrderId     工单ID
     * @param appraiseContent 评分内容
     **/
    @PutMapping(value = "/workorder/appraise")
    WorkOrderDTO updateWorkOrderAppraise(@RequestParam(value = "workOrderId") String workOrderId,
                                         @RequestParam(value = "appraiseContent") String appraiseContent);

    /**
     * 工单评分等级修改
     *
     * @param workOrderId 工单ID
     * @param levelId     评分等级
     **/
    @PutMapping(value = "/workorder/appraise/distributor")
    WorkOrderDTO updateDistributorApprise(@RequestParam(value = "workOrderId") String workOrderId,
                                          @RequestParam(value = "levelId") Integer levelId);

    /**
     * 拒单后重新指派安装工
     *
     * @param workOrderId 工单ID
     * @param engineerId  安装工id
     **/
    @PutMapping(value = "/workorder/engineer")
    WorkOrderDTO updateWorkOrderEngineer(@RequestParam(value = "workOrderId") String workOrderId,
                                         @RequestParam(value = "engineerId") Integer engineerId);

//    @RequestMapping(value = "/workorder/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    void insert(@RequestBody WorkOrderDTO workOrderDTO);


    /**
     * @param cancelReason 取消订单原因
     * @param cancelDetail 取消订单详情
     * @param ids          订单id集合
     * @return java.lang.Object
     * @description 批量更新订单状态（批量取消订单）
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @PutMapping(value = {"/order/sub/batch/status"})
    JSONObject updateOrderStatusBatch(@RequestParam(value = "cancelReason") String cancelReason,
                                      @RequestParam(value = "cancelDetail", required = false) String cancelDetail,
                                      @RequestParam(value = "ids") List<Long> ids);

    /**
     * 创建开票信息
     *
     * @param dto 开票信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/17
     */
    @RequestMapping(value = "/order/invoice", consumes = "application/json", method = RequestMethod.POST)
    void saveInvoice(@RequestBody OrderInvoiceDTO dto);

    /**
     * 描述：获取安装工某个状态的安装工单数量
     *
     * @param engineerId 安装工ID
     * @param status     工单状态
     **/
    @RequestMapping(value = "/workorder/count/engineer/{engineerId}/status", method = RequestMethod.GET)
    Integer getEngineerWorkOrderInstallCount(@PathVariable(value = "engineerId") Integer engineerId,
                                             @RequestParam(value = "status", required = false) Integer status);

    /**
     * @description 更新安装工工单最后完成时间
     */
    @PatchMapping(value = "/workorder/{hour}/finishTime")
    void updateLasteFinishTimeInstall(@PathVariable(value = "hour") Integer hour);

    /** ----------------------------创建工单 end-----------------------------*/

    /**
     * ----------------------------维修工单 start-----------------------------
     */
    /***
     * 功能描述:新增维修工单
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/7/15 10:23
     * @return: void
     */
    @RequestMapping(value = "/order/repairWorkOrder", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void createWorkOrderRepair(@RequestBody RepairWorkOrderDTO dto);

    /***
     * 功能描述:编辑维修工单
     *
     * @param: [waterDeviceWorkOrderRepairDTO]
     * @auther: liu yi
     * @date: 2019/7/15 10:24
     * @return: void
     */
    @RequestMapping(value = {"/order/repairWorkOrder"}, method = {RequestMethod.PUT}, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWorkOrderRepair(@RequestBody RepairWorkOrderDTO waterDeviceWorkOrderRepairDTO);

    /**
     * @param workCode
     * @return
     * @description 根据workCode查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder")
    RepairWorkOrderDTO getWorkOrderRepairByWorkCode(@RequestParam("workCode") String workCode);

    /***
     * 功能描述:编辑维修工单
     *
     * @param: [waterDeviceWorkOrderRepairDTO]
     * @auther: liu yi
     * @date: 2019/7/15 10:24
     * @return: void
     */
    @RequestMapping(value = {"/order/repairWorkOrder"}, method = {RequestMethod.PUT}, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWaterDeviceWorkOrderRepair(@RequestBody RepairWorkOrderDTO waterDeviceWorkOrderRepairDTO);

    /**
     * @param isFather
     * @param distributorId
     * @param engineerId
     * @param state
     * @param orderStatus
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询维修工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{pageNum}/{pageSize}")
    PageVO<RepairWorkOrderDTO> waterDeviceWorkOrderRepairPage(@RequestParam(value = "isFather") String isFather,
                                                              @RequestParam(value = "distributorId", required = false) String distributorId,
                                                              @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                              @RequestParam(value = "state", required = false) Integer state,
                                                              @RequestParam(value = "orderStatus", required = false) String orderStatus,
                                                              @RequestParam(value = "search", required = false) String search,
                                                              @PathVariable("pageNum") Integer pageNum,
                                                              @PathVariable("pageSize") Integer pageSize);

    /**
     * 描述：获取安装工某个状态的维修工单数量
     *
     * @param engineerId 安装工ID
     * @param status     工单状态
     * @Creator Liu Yi
     * @CreateTime 2019/3/9 12:03
     **/
    @RequestMapping(value = "/order/repairWorkOrder/count/status", method = RequestMethod.GET)
    Integer getWorkOrderRepairEngineerCount(@RequestParam(value = "engineerId") String engineerId,
                                            @RequestParam(value = "status", required = false) Integer status);


    /**
     * 描述： 根据签约单号获取工单
     *
     * @param signOrderId 签约单号
     */
    @GetMapping(value = "/workorder/sign")
    WorkOrderDTO getWorkOrderBySignOrderId(@RequestParam(value = "signOrderId") String signOrderId);

    /**
     * 维护工单信息------------------start--------------------------
     */
    /***
     * 功能描述:新增维护工单
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/7/15 10:25
     * @return: void
     */
    @PostMapping(value = "/order/maintenanceWorkOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createWorkOrderMaintenance(@RequestBody MaintenanceWorkOrderDTO dto);

    /**
     * @param distributorId
     * @param engineerId
     * @param state         维护工单状态：2-已受理 3-处理中 4-已完成
     * @param search
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询维护工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/app/{pageNum}/{pageSize}")
    PageVO<MaintenanceWorkOrderDTO> getWorkOrderMaintenancePage(@RequestParam(value = "distributorId", required = false) String distributorId,
                                                                @RequestParam(value = "engineerId", required = false) String engineerId,
                                                                @RequestParam(value = "state", required = false) Integer state,
                                                                @RequestParam(value = "search", required = false) String search,
                                                                @PathVariable("pageNum") Integer pageNum,
                                                                @PathVariable("pageSize") Integer pageSize);

    /**
     * @param id
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceById(@PathVariable("id") String id);

    /**
     * 功能描述:根据工单号查询维护工单
     *
     * @param: [workCode]
     * @auther: liu yi
     * @date: 2019/7/15 10:26
     * @return: com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO
     */
    @GetMapping(value = "/order/maintenanceWorkOrder")
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceByWorkCode(@RequestParam("workCode") String workCode);

    /***
     * 功能描述:更新维护工单信息
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/7/15 10:25
     * @return: void
     */
    @PutMapping(value = "/order/maintenanceWorkOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWorkOrderMaintenance(@RequestBody MaintenanceWorkOrderDTO dto);

    /***
     * 功能描述:根据订单号查询维护工单
     *
     * @param: [sncode, state, workOrderCompleteStatus]
     * @auther: liu yi
     * @date: 2019/7/15 10:25
     * @return: java.util.List<com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderDTO>
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/sn")
    List<MaintenanceWorkOrderDTO> getWorkOrderMaintenanceBySnCode(@RequestParam(value = "sncode", required = false) String sncode,
                                                                  @RequestParam(value = "state", required = false) Integer state,
                                                                  @RequestParam(value = "workOrderCompleteStatus", required = false) String workOrderCompleteStatus,
                                                                  @RequestParam(value = "source", required = false) Integer source);

    /**
     * 描述：获取安装工某个状态的维修工单数量(统计)
     *
     * @param engineerId
     * @param state
     * @return
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/count")
    Integer getWorkOrderMaintenanceEngineerCount(@RequestParam(value = "engineerId", required = false) String engineerId, @RequestParam(value = "state", required = false) Integer state);

    /***
     * 功能描述:检查没有完成的维护工单
     *
     * @param: [deviceSncode, materiels, engineerId]
     * @auther: liu yi
     * @date: 2019/4/10 15:46
     * @return: java.lang.Boolean
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/getNotComplete")
    List<MaintenanceWorkOrderDTO> getNotCompleteWorkOrderMaintenance(@RequestParam(value = "deviceSncode", required = false) String deviceSncode,
                                                                     @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                                                     @RequestParam(value = "source", required = false) Integer source);

    /***
     * 功能描述:同步更新最后完成时间
     *
     * @param: [hour]
     * @auther: liu yi
     * @date: 2019/5/30 17:30
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/{hour}/finishTime")
    void updateLasteFinishTime(@PathVariable(value = "hour") Integer hour);

    /** 维护工单信息------------------end--------------------------*/

    /**
     * 根据条件查询安装工单信息
     */
    @GetMapping(value = "/workorder/list")
    List<WorkOrderDTO> getWorkOrderInfo(
            @RequestParam(value = "engineerId") Integer engineerId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "payStatus", required = false) Integer payStatus,
            @RequestParam(value = "completeType", required = false) Integer completeType
    );

    /**
     * 支付宝统一扫码下单
     */
    @PostMapping(value = "/alipay/tradeprecreate", consumes = MediaType.APPLICATION_JSON_VALUE)
    String aliScanCodePay(@RequestBody AliPayRequest payRequest);

    @PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> aliAppPay(@RequestBody AliPayRequest payRequest);

    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    String wechatScanCodePay(@RequestBody WechatPayRequest payRequest);

    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    SortedMap<String, Object> wechatAppPay(@RequestBody WechatPayRequest payRequest);

    @PatchMapping(value = "/workorder/refuse")
    void refuseWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
                         @RequestParam(value = "engineerId") Integer engineerId,
                         @RequestParam(value = "engineerName") String engineerName,
                         @RequestParam(value = "reason") String reason);

    @PatchMapping(value = "/order/sub/detail/part", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateSubOrderDetailPart(@RequestBody SubOrderDetailDTO detail);

    @PatchMapping(value = "/workorder/accept")
    void acceptWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
                         @RequestParam(value = "engineerId") Integer engineerId,
                         @RequestParam(value = "nextStep", required = false) Integer nextStep);

    @PatchMapping(value = "/workorder/discontinue")
    void discontinueWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
                              @RequestParam(value = "reason") String reason,
                              @RequestParam(value = "remark") String remark,
                              @RequestParam(value = "reasonNum") Integer reasonNum);

    @PatchMapping(value = "/workorder/changedevice")
    void changedeviceWorkOrder(@RequestParam(value = "workOrderId") String workOrderId);

    @PatchMapping(value = "/workorder/continue")
    void continueWorkOrder(@RequestParam(value = "workOrderId") String workOrderId);

    @PatchMapping(value = "/workorder/chargeback")
    void chargebackWorkOrder(@RequestParam(value = "workOrderId") String workOrderId,
                             @RequestParam(value = "reason") String reason,
                             @RequestParam(value = "remark") String remark,
                             @RequestParam(value = "reasonNum") Integer reasonNum);

    @PatchMapping(value = "/workorder/complete")
    void completeWorkOrder(@RequestParam(value = "workOrderId") String workOrderId);

    @GetMapping(value = "/workorder/exists/logisticsCode")
    boolean existsWorkOrderWithLogisticsCode(@RequestParam(value = "logisticsCode") String logisticsCode);

    /**
     * 线下支付提交支付凭证
     */
    @PostMapping(value = "/otherpay/submitcredential")
    void otherPaySubmitCredential(@RequestParam(value = "subOrderId") Long subOrderId,
                                  @RequestParam(value = "workOrderId") String workOrderId,
                                  @RequestParam(value = "payType") Integer payType,
                                  @RequestParam(value = "payCredential") String payCredential);

    /**
     * 获取支付账号
     *
     * @param companyId
     * @param platform
     * @param clientType
     * @param receiveType
     * @return
     */
    @PostMapping(value = "/payaccount")
    PayAccountDetail getPayAccount(@RequestParam(value = "companyId") Integer companyId, @RequestParam(value = "platform") Integer platform, @RequestParam(value = "clientType") Integer clientType, @RequestParam(value = "receiveType") Integer receiveType);

    /**
     * 安装工操作更换设备型号
     */
    @PatchMapping(value = "/workorder/changeProductAndFee")
    void changeWorkOrderProductAndCostByEngineer(@RequestParam(name = "workOrderId") String workOrderId,
                                                 @RequestParam(name = "productId") Integer productId,
                                                 @RequestParam(name = "costId") Integer costId);

    @GetMapping(value = "/order/invoice/checkExist")
    Boolean checkExistByOrderId(@RequestParam("mainOrderId") Long mainOrderId,
                                @RequestParam("orderId") Long orderId);
}
