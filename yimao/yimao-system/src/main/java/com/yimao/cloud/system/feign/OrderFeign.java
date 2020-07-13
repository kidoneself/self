package com.yimao.cloud.system.feign;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.system.TransferAreaInfoDTO;
import com.yimao.cloud.pojo.dto.user.EngineerDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.query.order.MoveWaterDeviceOrderQuery;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.order.WorkOrderBackQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.*;
import com.yimao.cloud.pojo.vo.out.RepairWorkOrderVO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient(name = Constant.MICROSERVICE_ORDER, configuration = MultipartSupportConfig.class)
public interface OrderFeign {
    /**
     * 获取发票信息列表
     *
     * @param pageNum  第几页
     * @param pageSize 每页几条数
     * @param query    查询条件
     * @return
     */
    @RequestMapping(value = "/order/invoice/{pageNum}/{pageSize}", consumes = "application/json", method = RequestMethod.GET)
    PageVO<OrderInvoiceDTO> pageQueryInvoice(@PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize,
                                             @RequestBody OrderInvoiceQueryDTO query);

    /**
     * 根据ID获取开票信息
     *
     * @param id 开票ID
     * @return
     */
    @RequestMapping(value = "/order/invoice/{id}", method = RequestMethod.GET)
    OrderInvoiceDTO getInvoiceById(@PathVariable(value = "id") Integer id);


    /**************************************子订单接口-----start-----******************************/
    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @return java.lang.Object
     * @description 查询订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */

    @RequestMapping(value = "/order/sub/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderSubDTO> orderSubList(@RequestBody OrderConditionDTO orderConditionDTO,
                                     @PathVariable(value = "pageNum") Integer pageNum,
                                     @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 根据订单号查询订单基本信息
     *
     * @param id 订单号
     */
    @GetMapping(value = "/order/sub/{id}/basic")
    OrderSubDTO findOrderBasicInfoById(@PathVariable(value = "id") Long id);

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号查询订单
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @GetMapping(value = "/order/sub/{id}/detail")
    OrderSubDTO findOrderDetailById(@PathVariable(value = "id") Long id);

    /**
     * @param terminal 订单来源端
     * @param id       订单号
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/sub/{id}"})
    Object deleteOrder(@PathVariable(value = "id") Long id, @RequestParam(value = "terminal") Integer terminal);


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
     * @param ids 订单id集合
     * @return java.lang.Object
     * @description 批量更新是否删除订单(逻辑删除)
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @RequestMapping(value = {"/order/sub/deleted"}, method = RequestMethod.PUT)
    Object updateOrderDeleted(@RequestParam(value = "ids") List<Long> ids);

    /**
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     * @return java.lang.Object
     * @description 查询订单对账列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @RequestMapping(value = "/order/sub/bill/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderBillVO> queryOrderBillList(@RequestBody OrderBillQuery query,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize);


    //订单对账导出
    @RequestMapping(value = "/order/sub/bill/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<OrderBillExport> exportOrderBill(@RequestBody OrderBillQuery query);

    /**************************************子订单接口-----end-----******************************/

    /**************************************续费订单接口-----start-----******************************/

    /**
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     * @return java.lang.Object
     * @description 查询续费订单对账列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @PostMapping(value = "/order/renew/reconciliation/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderRenewVO> orderRenewReconciliationList(@RequestBody RenewOrderQuery query,
                                                      @PathVariable(value = "pageNum") Integer pageNum,
                                                      @PathVariable(value = "pageSize") Integer pageSize);

    /**************************************续费订单接口-----end-----******************************/

    /**************************************售后订单接口-----end-----******************************/
    /**
     * 根据订单查询条件
     *
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO
     * @description 查询订单列表
     * @author zhilin.he
     * @date 2
     */
//    @GetMapping(value = "/order/refund/{pageNum}/{pageSize}")
//    @RequestMapping(value = "/order/refund/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/order/refund/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderSalesInfoDTO> orderSalesList(@RequestBody OrderConditionDTO orderConditionDTO,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     * @description 根据售后id查询售后订单详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     */
    @GetMapping(value = {"/order/refund/{id}"})
    Object orderRefundInfo(@PathVariable(value = "id") Long id);

    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     * @description 查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     */
    @GetMapping(value = {"/order/refund/audit/{id}"})
    Object orderRefundAuditInfo(@PathVariable(value = "id") Long id);

    /**
     * @param orderRefundAuditDTO
     * @return org.springframework.http.ResponseEntity
     * @description 售后审核（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/audit/batch"})
    ResponseEntity refundAuditBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO);

    /**
     * @param saleOrderIds 订单售后id集合
     * @param pageNum      当前页
     * @param pageSize     每页显示条数
     */
    @GetMapping(value = "/order/refund/logistic/{pageNum}/{pageSize}")
    PageVO<OrderSalesInfoDTO> orderLogisticSubmitList(@RequestParam(value = "saleOrderIds") String saleOrderIds,
                                                      @PathVariable(value = "pageNum") Integer pageNum,
                                                      @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param orderRefundAuditDTO
     * @return org.springframework.http.ResponseEntity
     * @description 提交物流（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/logistic/batch"})
    ResponseEntity refundAuditLogisticBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO);

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 关闭退货
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @PatchMapping(value = {"/order/refund/close/{id}"})
    Object orderRefundClose(@PathVariable(value = "id") Long id);

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/refund/{id}"})
    Object deleteOrderSales(@PathVariable(value = "id") Long id);

    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     */
    @GetMapping(value = "/order/refund/audit/{pageNum}/{pageSize}")
    PageVO<OrderSalesInfoDTO> orderRefundAuditList(@RequestBody OrderConditionDTO orderConditionDTO,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**************************************售后订单接口-----end-----******************************/

    /**************************************服务收益接口-----start-----******************************/
    /**
     * @param productCompanyId
     * @param orderId
     * @param productCategoryId
     * @param userId
     * @param incomeType
     * @param userType
     * @param distributorAccount
     * @param distributorName
     * @param province
     * @param city
     * @param region
     * @param settlementTime
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询产品服务收益明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/serviceIncome/{pageNum}/{pageSize}")
    PageVO<ProductIncomeVO> pageQueryServiceIncome(@RequestParam(value = "productCompanyId", required = false) Long productCompanyId,
                                                   @RequestParam(value = "orderId", required = false) Long orderId,
                                                   @RequestParam(value = "productCategoryId", required = false) Integer productCategoryId,
                                                   @RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                   @RequestParam(value = "userType", required = false) Integer userType,
                                                   @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                   @RequestParam(value = "distributorName", required = false) String distributorName,
                                                   @RequestParam(value = "province", required = false) String province,
                                                   @RequestParam(value = "city", required = false) String city,
                                                   @RequestParam(value = "region", required = false) String region,
                                                   @RequestParam(value = "settlementTime", required = false) String settlementTime,
                                                   @RequestParam(value = "startTime", required = false) String startTime,
                                                   @RequestParam(value = "endTime", required = false) String endTime,
                                                   @RequestParam(value = "productMode", required = false) Integer productMode,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                   @RequestParam(value = "ticketNo", required = false) String ticketNo);

    ;

    /**
     * @param id
     * @return
     * @description 根据id查询服务收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/serviceIncome/{id}")
    IncomeRecordResultDTO getServiceIncomeById(@PathVariable(value = "id") Integer id);

    /**
     * @param orderId
     * @return
     * @description 根据订单id查询服务收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/serviceIncome")
    List<ServiceIncomeRecordPartDTO> getServiceIncomeRecordPartList(@RequestParam(value = "orderId") Long orderId);

//    @RequestMapping(value = "/order/service/income/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<IncomeExportDTO> serviceIncomeExport(@RequestBody ProductIncomeQueryDTO query);

    /**************************************服务收益接口-----end-----******************************/

    /**************************************产品收益接口-----start-----******************************/

    /**
     * 查询产品收益明细列表
     *
     * @return page
     * @description 查询产品收益明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/{pageNum}/{pageSize}")
    PageVO<ProductIncomeVO> pageQueryProductIncome(@RequestParam(value = "productCompanyId", required = false) Long productCompanyId,
                                                   @RequestParam(value = "orderId", required = false) String orderId,
                                                   @RequestParam(value = "productCategoryId", required = false) Integer productCategoryId,
                                                   @RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                   @RequestParam(value = "userType", required = false) Integer userType,
                                                   @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                   @RequestParam(value = "distributorName", required = false) String distributorName,
                                                   @RequestParam(value = "distributorProvince", required = false) String distributorProvince,
                                                   @RequestParam(value = "distributorCity", required = false) String distributorCity,
                                                   @RequestParam(value = "distributorRegion", required = false) String distributorRegion,
                                                   @RequestParam(value = "settlementTime", required = false) String settlementTime,
                                                   @RequestParam(value = "settlementStatus", required = false) Integer settlementStatus,
                                                   @RequestParam(value = "engineerSettlementTime", required = false) String engineerSettlementTime,
                                                   @RequestParam(value = "startTime", required = false) String startTime,
                                                   @RequestParam(value = "endTime", required = false) String endTime,
                                                   @RequestParam(value = "payStartTime", required = false) String payStartTime,
                                                   @RequestParam(value = "payEndTime", required = false) String payEndTime,
                                                   @RequestParam(value = "productMode", required = false) Integer productMode,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param orderId
     * @return
     * @description 根据订单id查询收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome")
    List<ProductIncomeRecordPartDTO> getProductIncomeRecordPartList(@RequestParam(value = "orderId") Integer orderId);

    /**
     * @param id
     * @return
     * @description 根据id查询收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/{id}")
    IncomeRecordResultDTO getProductIncomeById(@PathVariable(value = "id") Integer id);

    /**
     * @param orderId
     * @return
     * @description 变更订单状态为已完成可结算
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/toComplete")
    void changeProductIncomeRecordToComplete(@RequestParam(value = "orderId") Long orderId,
                                             @RequestParam(value = "orderCompleteTime") Date orderCompleteTime);

    /**
     * @param orderId
     * @return
     * @description 退单收益变更
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/refundOrder")
    void refundOrder(@RequestParam(value = "orderId") Long orderId);

    /**
     * @param orderId
     * @return
     * @description 退货收益变更
     * @author Liu Yi
     */
    @PutMapping(value = "/order/productIncome/refundGoods")
    void refundGoods(@RequestParam(value = "orderId") Long orderId, @RequestParam(value = "num") Integer num);

    /**
     * 产品收益明细导出
     *
     * @author hhf
     * @date 2019/5/13
     */
//    @RequestMapping(value = "/order/product/income/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<IncomeExportDTO> productIncomeExport(@RequestBody ProductIncomeQueryDTO query,
//                                              @RequestParam(value = "pageNum") Integer pageNum,
//                                              @RequestParam(value = "pageSize") Integer pageSize);

//    @RequestMapping(value = "/order/product/income/export/count", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    Integer productIncomeExportCount(@RequestBody ProductIncomeQueryDTO query);
    @RequestMapping(value = "/allot/sell/income", method = RequestMethod.POST)
    void allotSellIncome(@RequestParam(value = "orderId") Long orderId);

    /**
     * 描述：设置收益结算状态
     *
     * @param id
     */
    @PatchMapping(value = "/order/income/settlement/{id}/status")
    void setSettlementIncomeStatus(@PathVariable(value = "id") Integer id,
                                   @RequestParam(value = "status") Integer status);

    /**************************************产品收益接口-----end-----******************************/

    /**************************************招商收益接口-----start-----******************************/

    /**
     * @param orderId
     * @param orderType
     * @param userId
     * @param distributorAccount
     * @param distributorName
     * @param province
     * @param city
     * @param region
     * @param recommendDistributorName
     * @param recommendDistributorAccount
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询产品服务收益明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/investmentIncome/{pageNum}/{pageSize}")
    PageVO<InvestmentIncomeVO> pageQueryInvestmentIncome(@RequestParam(value = "orderId", required = false) Long orderId,
                                                         @RequestParam(value = "orderType", required = false) Integer orderType,
                                                         @RequestParam(value = "userId", required = false) Integer userId,
                                                         @RequestParam(value = "distributorId", required = false) Integer distributorId,
                                                         @RequestParam(value = "distributorLevel", required = false) Integer distributorLevel,
                                                         @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                         @RequestParam(value = "distributorName", required = false) String distributorName,
                                                         @RequestParam(value = "province", required = false) String province,
                                                         @RequestParam(value = "city", required = false) String city,
                                                         @RequestParam(value = "region", required = false) String region,
                                                         @RequestParam(value = "refereeName", required = false) String refereeName,
                                                         @RequestParam(value = "refereeAccount", required = false) String refereeAccount,
                                                         @RequestParam(value = "startTime", required = false) String startTime,
                                                         @RequestParam(value = "endTime", required = false) String endTime,
                                                         @RequestParam(value = "settlementMonth", required = false) String settlementMonth,
                                                         @PathVariable(value = "pageNum") Integer pageNum,
                                                         @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param id
     * @return
     * @description 根据id查询招商收益
     * @author Liu Yi
     */
    @GetMapping(value = "/order/investmentIncome/{id}")
    IncomeRecordResultDTO getInvestmentIncomeRecordById(@PathVariable(value = "id") Integer id);

    /**************************************招商收益接口-----end-----******************************/

    /**************************************收益规则接口-----start-----******************************/
    /**
     * 分页查询规则模版信息
     *
     * @param name
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     * @author Liu Yi
     * @date 2019/1/17
     */
    @GetMapping(value = "/income/rule/{pageNum}/{pageSize}")
    PageVO<IncomeRuleDTO> pageQueryIncomeRule(@RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime,
                                              @PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize);

    @RequestMapping(value = "/income/rule", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveIncomeRule(@RequestBody IncomeRuleDTO dto);

    @RequestMapping(value = "/income/rule", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void update(@RequestBody IncomeRuleDTO dto);

    @RequestMapping(value = "/income/rule/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable(value = "id") Integer id);

    @GetMapping(value = "/income/rule")
    List<IncomeRuleDTO> listInvestmentIncomeRule(@RequestParam(value = "incomeType") Integer incomeType);

    @RequestMapping(value = "/income/subject", method = RequestMethod.GET)
    List<IncomeSubjectDTO> getIncomeSubject();

    /**************************************收益规则接口-----end-----******************************/

    /**************************************订单发货-----start-----******************************/

    /**
     * 添加订单收货信息
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addOrderDelivery(@RequestBody OrderDeliveryDTO orderDelivery);

    /**
     * 导入物流跟踪信息
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object importExcel(@RequestPart("multipartFile") MultipartFile multipartFile);

    /**
     * 查询物流信息
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery/query", method = RequestMethod.GET)
    Object query(@RequestParam(value = "logisticsNo") String logisticsNo,
                 @RequestParam(value = "orderId") Long orderId);

    /**
     * 发货列表查询
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery/list/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<DeliveryInfoDTO> list(@RequestBody DeliveryDTO deliveryDTO,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize);


    /**************************************订单发货-----end-----******************************/


    /**************************************提现-----start-----******************************/

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
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderWithdrawDTO>
     * @author hhf
     * @date 2019/2/28
     */
    @RequestMapping(value = "/withdraw/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<OrderWithdrawDTO> withdrawList(@PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize,
                                          @RequestParam(value = "orderId", required = false) String orderId,
                                          @RequestParam(value = "userId", required = false) Integer userId,
                                          @RequestParam(value = "phone", required = false) String phone,
                                          @RequestParam(value = "startTime", required = false) String startTime,
                                          @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "endTime", required = false) String endTime);

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
     * @date 2019/3/1
     */
    @RequestMapping(value = "/withdraw/audit/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<WithdrawSubDTO> withdrawAuditList(@PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize,
                                             @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                             @RequestParam(value = "userId", required = false) Integer userId,
                                             @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                             @RequestParam(value = "phone", required = false) String phone,
                                             @RequestParam(value = "startTime", required = false) String startTime,
                                             @RequestParam(value = "endTime", required = false) String endTime);

    /**
     * 提现审核
     *
     * @param id          提现审核主键
     * @param auditStatus 1-通过 ; 2-不通过
     * @param auditReason 审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2019/3/1
     */
    @RequestMapping(value = "/withdraw/audit", method = RequestMethod.PATCH)
    void audit(@RequestParam(value = "id") Integer id,
               @RequestParam(value = "auditStatus") Integer auditStatus,
               @RequestParam(value = "auditReason") String auditReason);

    /**
     * 提现审核（批量）
     *
     * @param ids         提现审核主键
     * @param auditStatus 1-通过 ; 2-不通过
     * @param auditReason 审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2019/3/1
     */
    @RequestMapping(value = "/withdraw/audit/batch", method = RequestMethod.PATCH)
    String batchAudit(@RequestParam(value = "ids") List<String> ids,
                      @RequestParam(value = "auditStatus") Integer auditStatus,
                      @RequestParam(value = "auditReason") String auditReason);

    /**
     * 提现记录列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/4
     */
    @RequestMapping(value = "/withdraw/record/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = "application/json")
    PageVO<WithdrawSubDTO> withdrawRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                              @PathVariable(value = "pageSize") Integer pageSize,
                                              @RequestBody WithdrawQueryDTO withdrawQueryDTO);

    /**
     * 提现明细列表
     *
     * @param pageNum          分页页数
     * @param pageSize         分页大小
     * @param withdrawQueryDTO 查询信息
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/4
     */
    @RequestMapping(value = "/withdraw/record/detail/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = "application/json")
    PageVO<WithdrawSubDTO> withdrawRecordDetailList(@PathVariable(value = "pageNum") Integer pageNum,
                                                    @PathVariable(value = "pageSize") Integer pageSize,
                                                    @RequestBody WithdrawQueryDTO withdrawQueryDTO);

    /**
     * 提现操作日志
     *
     * @param pageNum        分页页数
     * @param pageSize       分页大小
     * @param partnerTradeNo 自提现单号
     * @param withdrawFlag   提现成功与否
     * @param startTime      操作开始时间
     * @param endTime        操作结束时间
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/4
     */
    @RequestMapping(value = "/withdraw/record/log/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = "application/json")
    PageVO<WithdrawSubDTO> withdrawRecordLogList(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "partnerTradeNo", required = false) Long partnerTradeNo,
                                                 @RequestParam(value = "withdrawFlag", required = false) Integer withdrawFlag,
                                                 @RequestParam(value = "startTime", required = false) String startTime,
                                                 @RequestParam(value = "endTime", required = false) String endTime);

    /**************************************提现-----end-----******************************/


    /**************************************退款-----start-----******************************/

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
    @RequestMapping(value = "/refund/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = "application/json")
    PageVO<AfterSalesOrderDTO> refundAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestBody AfterSalesOrderQueryDTO dto);

    /**
     * 线上/线下退款复核
     *
     * @param id 售后订单主键
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @RequestMapping(value = "/refund/review", method = RequestMethod.PATCH, consumes = "application/json")
    void refundReview(@RequestParam(value = "id") Long id);

    /**
     * 线上/线下退款批量复核
     *
     * @param ids 售后订单主键
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/3/12
     */
    @RequestMapping(value = "/refund/review/batch", method = RequestMethod.PATCH, consumes = "application/json")
    void refundReviewBatch(@RequestBody RefundReviewBatchVo refundReviewBatchVo);

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
    @RequestMapping(value = "/refund/record/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = "application/json")
    PageVO<AfterSalesOrderDTO> refundRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                            @PathVariable(value = "pageSize") Integer pageSize,
                                            @RequestBody AfterSalesOrderQueryDTO dto);

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
    @RequestMapping(value = "/refund/log/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<AfterSalesOrderDTO> refundLog(@PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @RequestParam(value = "orderId", required = false) Long orderId,
                                         @RequestParam(value = "operation", required = false) Integer operation,
                                         @RequestParam(value = "startTime", required = false) String startTime,
                                         @RequestParam(value = "endTime", required = false) String endTime);


    /**************************************退款-----end-----******************************/

    /**
     * 待办事项统计
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    @RequestMapping(value = "/order/overview", method = RequestMethod.GET)
    Map<String, Long> orderOverview();

    /**
     * 概况-经营概况
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    @RequestMapping(value = "/order/overview/business", method = RequestMethod.GET)
    BusinessProfileDTO orderOverviewBusiness();


    /**
     * ==========================================订单地址设置======================================
     */

    @RequestMapping(value = "/delivery/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addDeliveryAddress(@RequestBody DeliveryAddressDTO dto);


    @RequestMapping(value = "/delivery/{id}", method = RequestMethod.PATCH)
    void delivery(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/refund/{id}", method = RequestMethod.PATCH)
    void refund(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/delivery/address/{id}", method = RequestMethod.DELETE)
    void deleteDeliveryAddress(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/delivery/address", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void editorDeliveryAddress(@RequestBody DeliveryAddressDTO dto);


    @RequestMapping(value = "/address/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<DeliveryAddressDTO> addressPage(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize);


    @RequestMapping(value = "/order/sub", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateSubOrder(@RequestBody OrderSubDTO subOrder);

    /**
     * 根据条件查询安装工单信息
     */
    @RequestMapping(value = "/workorder/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderDTO> getWorkOrderList(@RequestBody WorkOrderQueryDTO workOrderQueryDTO,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 订单列表导出
     */
//    @RequestMapping(value = "/order/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<OrderExportDTO> orderExportList(@RequestBody OrderConditionDTO query,
//                                         @RequestParam(value = "pageNum") Integer pageNum,
//                                         @RequestParam(value = "pageSize") Integer pageSize);

    /**
     * 订单列表导出总数
     */
    @RequestMapping(value = "/order/export/count", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Integer orderExportListCount(@RequestBody OrderConditionDTO query);


    /**
     * ==========================================维护工单 start======================================
     */

    /***
     * 功能描述:新增维护工单
     *
     * @param dto
     */
    @PostMapping(value = "/order/maintenanceWorkOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createWorkOrderMaintenance(@RequestBody MaintenanceWorkOrderDTO dto);

    /***
     * 功能描述: 后台编辑维护工单信息
     *
     * @param:
     * @auther: liu yi
     * @date: 2019/5/17 14:22
     * @return:
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/system")
    void editworkOrderMaintenanceBySystem(@RequestParam(value = "id") String id,
                                          @RequestParam(value = "materielDetailIds") String materielDetailIds,
                                          @RequestParam(value = "materielDetailNames") String materielDetailNames);

    /***
     * 功能描述:查询维护工单列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     */
    @PostMapping(value = "/order/maintenanceWorkOrder/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<MaintenanceWorkOrderDTO> listMaintenanceWorkOrder(@RequestBody MaintenanceWorkOrderQueryDTO queryDTO,
                                                             @PathVariable(value = "pageNum") Integer pageNum,
                                                             @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param id
     * @return
     * @description 根据工单号查询维护工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/maintenanceWorkOrder/{id}")
    MaintenanceWorkOrderDTO getWorkOrderMaintenanceById(@PathVariable(value = "id") String id);

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

    /***
     * 功能描述:根据id删除维护工单
     *
     * @param: [id]
     * @auther: liu yi
     * @date: 2019/5/17 10:30
     * @return: void
     */
    @DeleteMapping(value = "/order/maintenanceWorkOrder/{id}")
    void deleteMaintenanceWorkOrderById(@PathVariable(value = "id") String id);

    /***
     * 功能描述:审核
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:53
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/order/maintenanceWorkOrder/{id}/audit")
    void auditMaintenanceWorkOrder(@PathVariable(value = "id") String id,
                                   @RequestParam(value = "recordIds", required = false) Integer[] recordIds,
                                   @RequestParam(value = "effective", defaultValue = "1") Integer effective);

    /**
     * ==========================================维护工单 end======================================
     */

    /**
     * ==========================================维护工单操作日志 start======================================
     */
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


    /***
     * 功能描述:查询维护工单操作日志列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @GetMapping(value = "/order/maintenanceWorkOrderOperateLog")
    List<MaintenanceWorkOrderOperateLogDTO> getListByMaintenanceWorkOrderId(@RequestParam(value = "maintenanceWorkOrderId") String maintenanceWorkOrderId);

    /**
     * ==========================================维护工单操作日志 end======================================
     */

    /**
     * ==========================================维修工单 start======================================
     */

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
    PageVO<RepairWorkOrderDTO> page(@RequestParam(value = "isFather") String isFather,
                                    @RequestParam(value = "distributorId", required = false) String distributorId,
                                    @RequestParam(value = "engineerId", required = false) Integer engineerId,
                                    @RequestParam(value = "state", required = false) Integer state,
                                    @RequestParam(value = "orderStatus", required = false) String orderStatus,
                                    @RequestParam(value = "search", required = false) String search,
                                    @PathVariable(value = "pageNum") Integer pageNum,
                                    @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param id
     * @return
     * @description 根据id查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/order/repairWorkOrder/{id}")
    RepairWorkOrderVO getWorkOrderRepairById(@PathVariable(value = "id") Integer id);

    /**
     * ==========================================维修工单 end======================================
     */

    /** -------------------------------云平台工单后台操作----start-------------------------------------------- **/

    /**
     * 描述：云平台条件查询工单列表
     *
     * @param settlementTime
     * @param finishMoneyEndTime
     * @param finishMoneyStartTime
     * @param finishEndTime
     * @param finishStartTime
     * @param payEndTime
     * @param payStartTime
     * @param createEndTime
     * @param createStartTime
     * @param userId
     **/
    @GetMapping(value = "/workorder/{operationType}/{pageNum}/{pageSize}")
    PageVO<WorkOrderResultDTO> queryWorkOrderList(@PathVariable(value = "operationType") Integer operationType,
                                                  @PathVariable(value = "pageNum") Integer pageNum,
                                                  @PathVariable(value = "pageSize") Integer pageSize,
                                                  @RequestParam(value = "payType", required = false) Integer payType,
                                                  @RequestParam(value = "province", required = false) String province,
                                                  @RequestParam(value = "city", required = false) String city,
                                                  @RequestParam(value = "region", required = false) String region,
                                                  @RequestParam(value = "orderId", required = false) Long orderId,
                                                  @RequestParam(value = "workOrderId", required = false) String workOrderId,
                                                  @RequestParam(value = "startTime", required = false) Date startTime,
                                                  @RequestParam(value = "endTime", required = false) Date endTime,
                                                  @RequestParam(value = "cancelStartTime", required = false) Date cancelStartTime,
                                                  @RequestParam(value = "cancelEndTime", required = false) Date cancelEndTime,
                                                  @RequestParam(value = "deleteStartTime", required = false) Date deleteStartTime,
                                                  @RequestParam(value = "deleteEndTime", required = false) Date deleteEndTime,
                                                  @RequestParam(value = "backOrderStatus", required = false) Integer backOrderStatus,
                                                  @RequestParam(value = "backRefundStatus", required = false) Integer backRefundStatus,
                                                  @RequestParam(value = "roleLevel", required = false) Integer roleLevel,
                                                  @RequestParam(value = "completeTime", required = false) Date completeTime,
                                                  @RequestParam(value = "payTime", required = false) Date payTime,
                                                  @RequestParam(value = "accountMonth", required = false) String accountMonth,
                                                  @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
                                                  @RequestParam(value = "status", required = false, defaultValue = "-3") Integer status,
                                                  @RequestParam(value = "userId", required = false) Integer userId,
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
    );

    /**
     * 描述：云平台导出工单
     *
     * @param roleLevel2
     * @param backRefundStatus
     * @param deleteEndTime
     * @param deleteStartTime
     * @param cancelEndTime
     * @param cancelStartTime
     * @param payType2
     **/
//    @GetMapping("/workorder/export")
//    List<WorkOrderExportDTO> exportWorkOrderList(@RequestParam(value = "operationType", required = false) Integer operationType,
//                                                 @RequestParam(value = "payType", required = false) Integer payType,
//                                                 @RequestParam(value = "province", required = false) String province,
//                                                 @RequestParam(value = "city", required = false) String city,
//                                                 @RequestParam(value = "region", required = false) String region,
//                                                 @RequestParam(value = "orderId", required = false) Long orderId,
//                                                 @RequestParam(value = "workOrderId", required = false) String workOrderId,
//                                                 @RequestParam(value = "startTime", required = false) Date startTime,
//                                                 @RequestParam(value = "endTime", required = false) Date endTime,
//                                                 @RequestParam(value = "cancelStartTime", required = false) Date cancelStartTime,
//                                                 @RequestParam(value = "cancelEndTime", required = false) Date cancelEndTime,
//                                                 @RequestParam(value = "deleteStartTime", required = false) Date deleteStartTime,
//                                                 @RequestParam(value = "deleteEndTime", required = false) Date deleteEndTime,
//                                                 @RequestParam(value = "backOrderStatus", required = false) Integer backOrderStatus,
//                                                 @RequestParam(value = "backRefundStatus", required = false) Integer backRefundStatus,
//                                                 @RequestParam(value = "roleLevel", required = false) Integer roleLevel,
//                                                 @RequestParam(value = "completeTime", required = false) Date completeTime,
//                                                 @RequestParam(value = "payTime", required = false) Date payTime,
//                                                 @RequestParam(value = "accountMonth", required = false) String accountMonth,
//                                                 @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
//                                                 @RequestParam(value = "status", required = false, defaultValue = "-3") Integer status,
//                                                 @RequestParam(value = "userId", required = false) Integer userId,
//                                                 @RequestParam(value = "payStartTime", required = false) Date payStartTime,
//                                                 @RequestParam(value = "payEndTime", required = false) Date payEndTime,
//                                                 @RequestParam(value = "finishStartTime", required = false) Date finishStartTime,
//                                                 @RequestParam(value = "finishEndTime", required = false) Date finishEndTime,
//                                                 @RequestParam(value = "finishMoneyStartTime", required = false) Date finishMoneyStartTime,
//                                                 @RequestParam(value = "finishMoneyEndTime", required = false) Date finishMoneyEndTime,
//                                                 @RequestParam(value = "terminal", required = false) Integer terminal,
//                                                 @RequestParam(value = "payTerminal", required = false) Integer payTerminal);

    /**
     * 描述：云平台删除工单
     *
     * @param workOrderId 工单ID
     **/
    @DeleteMapping(value = "/workorder/{workOrderId}")
    void deleteWorkOrder(@PathVariable(value = "workOrderId") String workOrderId);

    /**
     * 描述：云平台分配客服--查询该工单所在地区下的安装工列表
     *
     * @param id 工单ID
     **/
    @GetMapping(value = "/workorder/allot/engineer/{id}")
    List<EngineerDTO> getAllotEngineerList(@PathVariable(value = "id") String id);

    /**
     * 云平台分配客服--即分配安装工
     *
     * @param workOrderId 工单ID
     * @param engineerId  安装工id
     **/
    @PutMapping(value = "/workorder/allot/engineer")
    void allotEngineer(@RequestParam(value = "workOrderId") String workOrderId,
                       @RequestParam(value = "engineerId") Integer engineerId);

    /**
     * 云平台工单评价
     **/
    @PutMapping(value = "/workorder/appraise/score")
    void updateWorkOrderAppraiseScore(@RequestParam(value = "workOrderId") String workOrderId,
                                      @RequestParam(value = "score") Integer score,
                                      @RequestParam(value = "appraiseContent") String appraiseContent);

    /**
     * 描述：云平台工单概况--获取某个状态工单的数量
     **/
    @GetMapping("/workorder/count/status")
    List<WorkOrderCountDTO> countWorkOrderByStatus();

    /**
     * 描述：云平台工单趋势--获取两个日期间的工单数量
     **/
    @GetMapping(value = "/workorder/count/time")
    List<WorkOrderCountDTO> countWorkOrderByCreateTime(@RequestParam(value = "days", required = false) String days,
                                                       @RequestParam(value = "startTime", required = false) Date startTime,
                                                       @RequestParam(value = "endTime", required = false) Date endTime);

    /**
     * 描述：云平台根据条件查询安装工单支付信息
     */
    @GetMapping(value = "/workorder/pay/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<PayRecordDTO> getWorkOrderPayList(@RequestBody WorkOrderQueryDTO workOrderQueryDTO,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 描述：云平台--工单退单
     *
     * @param id 工单ID
     **/
    @PutMapping(value = "/workorder/chargeback/{id}")
    void backOrderUpdate(@PathVariable(value = "id") String id);

    /**
     * 根据条件查询工单发票列表信息
     */
    @RequestMapping(value = "/workorder/invoice/{pageNum}/{pageSize}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderDTO> getWorkOrderInvoiceList(@RequestBody WorkOrderQueryDTO dto,
                                                 @PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 云平台导出工单发票信息
     */
//    @RequestMapping(value = "/workorder/invoice/export", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<WorkOrderInvoiceExportDTO> exportWorkOrderInvoiceList(@RequestBody WorkOrderQueryDTO dto);

    /**
     * @description 根据工单id查询工单操作记录列表
     */
    @GetMapping(value = "/workorder/operation/{id}/{pageNum}/{pageSize}")
    PageVO<WorkOrderOperationDTO> getWorkOrderOperationList(@PathVariable(value = "id") String id,
                                                            @PathVariable(value = "pageNum") Integer pageNum,
                                                            @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 描述：根据工单id获取工单信息
     *
     * @param workOrderId 工单ID
     **/
    @GetMapping(value = "/workorder/{workOrderId}")
    WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId);
    /** -------------------------------云平台工单后台操作----end-------------------------------------------- **/

    /**------------------------云平台续费工单功能-------start--------------------------------------**/

    /**
     * 订单管理-续费列表-列表
     */
    @PostMapping(value = "/order/renew/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderRenewVO> getOrderRenewList(@RequestBody RenewOrderQuery query,
                                           @PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 订单管理-续费列表-详情
     */
    @GetMapping(value = "/order/renew/{id}/detail")
    OrderRenewVO getOrderRenewDetail(@PathVariable(value = "id") String id);

    /**
     * 订单管理-续费列表-重新提交支付凭证
     */
    @PatchMapping(value = "/order/renew/{id}/resubmit")
    void resubmit(@PathVariable(value = "id") String id,
                  @RequestParam(value = "payType") Integer payType,
                  @RequestParam(value = "payCredential") String payCredential);

    /**
     * 财务管理-续费订单对账-列表
     */
    @PostMapping(value = "/order/renew/finance/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderRenewVO> orderRenewFinanceList(@RequestBody RenewOrderQuery query,
                                               @PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 订单管理-续费列表-详情
     */
    @GetMapping(value = "/order/renew/finance/{id}/detail")
    OrderRenewVO getOrderRenewFinanceDetail(@PathVariable(value = "id") String id);

    /**
     * 财务管理-支付审核-续费支付待审核-列表
     */
    @GetMapping(value = "/order/renew/paycheck/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderRenewVO> orderRenewPayCheckList(@RequestBody RenewOrderQuery query,
                                                @PathVariable(value = "pageNum") Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 续费工单--审核
     */
    @PatchMapping(value = "/order/renew/{id}/paycheck/single")
    void orderRenewPayCheckSingle(@PathVariable(value = "id") String id,
                                  @RequestParam(value = "pass") Boolean pass,
                                  @RequestParam(value = "reason", required = false) String reason);

    /**
     * ------------------------云平台续费工单功能-------end--------------------------------------
     **/

    @RequestMapping(value = "/allot/service/income", method = RequestMethod.POST)
    void allotServiceIncome(@RequestParam(value = "ticketNo") String ticketNo,
                            @RequestParam(value = "stationId") Integer stationId,
                            @RequestParam(value = "deviceId") String deviceId);

    @GetMapping(value = "/workorder/engineer/{engineerId}")
    List<EngineerWorkOrderVO> listWorkOrderByEngineerId(@PathVariable(value = "engineerId") Integer engineerId);


    @PatchMapping(value = "/order/delivery/{id}")
    void setDelivery(@PathVariable(value = "id") Long id);


    @PatchMapping(value = "/order/delivery/batch")
    void batchDelivery(@RequestParam(value = "ids") List<Long> ids);

    @RequestMapping(value = "/order/orderConfig", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void addOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO);

    @RequestMapping(value = "/order/orderConfig", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateOrderConfig(@RequestBody OrderConfigDTO orderConfigDTO);

    @GetMapping(value = "/order/orderConfig")
    OrderConfigDTO getOrderConfig();


    @GetMapping(value = "/order/delivery/record/{pageNum}/{pageSize}")
    PageVO<OrderDeliveryRecordDTO> deliveryRecordList(@PathVariable(value = "pageNum") Integer pageNum,
                                                      @PathVariable(value = "pageSize") Integer pageSize,
                                                      @RequestParam(value = "orderId", required = false) String orderId,
                                                      @RequestParam(value = "logisticsNo", required = false) String logisticsNo,
                                                      @RequestParam(value = "startTime", required = false) String startTime,
                                                      @RequestParam(value = "endTime", required = false) String endTime,
                                                      @RequestParam(value = "userId", required = false) Integer userId,
                                                      @RequestParam(value = "addreessName", required = false) String addreessName,
                                                      @RequestParam(value = "terminal", required = false) Integer terminal);

    @RequestMapping(value = "/refund/rental/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<AfterSalesOrderDTO> rentalGoodsList(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestBody AfterSalesConditionDTO dto);


    //发货列表相关导出
    @RequestMapping(value = "/order/delivery/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Object> deliveryExport(@RequestParam(value = "exportType") Integer exportType,
                                @RequestBody DeliveryConditionDTO dto);


    @RequestMapping(value = "/refund/audit/{pageNum}/{pageSize}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderAuditLogDTO> orderAuditLogList(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable(value = "pageSize") Integer pageSize,
                                               @RequestBody AfterSalesConditionDTO dto);


//    @RequestMapping(value = "/refund/rental/goods/all", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<RentalGoodsExportDTO> exportRentalGoods(@RequestBody AfterSalesConditionDTO dto);
//
//
//    @RequestMapping(value = "/refund/rental/goods/audited", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<RentalGoodsExportDTO> exportAuditedRentalGoods(@RequestBody AfterSalesConditionDTO dto);
//
//
//    @RequestMapping(value = "/refund/rental/audit/record", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<RentalGoodsExportDTO> exportAuditedRecord(@RequestBody AfterSalesConditionDTO dto);

    @GetMapping(value = "/order/delivery/record/{id}")
    DeliveryDetailInfoDTO deliveryRecordDetail(@PathVariable(value = "id") Integer id);

    //获取默认发货地址
    @GetMapping(value = "/order/delivery/address/{id}")
    DeliveryAddressDTO getDeliveryAddress(@PathVariable(value = "id") Integer id);


    //审核记录详情
    @GetMapping(value = "/refund/audit/examine/{id}")
    ExamineRecordDTO getExamineRecordDetailById(@PathVariable(value = "id") Long id);


    @PatchMapping(value = "/order/audit/record")
    void auditAdopt(@RequestParam(value = "id") Long id);

    @PatchMapping(value = "/order/batch/audit/record")
    void batchAuditAdopt(@RequestParam(value = "ids") List<Long> ids);

    @PatchMapping(value = "/order/audit/noPassage")
    void auditNoPassage(@RequestParam(value = "id") Long id,
                        @RequestParam(value = "auditReason") String auditReason,
                        @RequestParam(value = "detailReason", required = false) String detailReason);


    @PatchMapping(value = "/order/batch/audit/noPassage")
    void batchAuditNoPassage(@RequestParam(value = "ids") List<Long> ids,
                             @RequestParam(value = "auditReason") String auditReason,
                             @RequestParam(value = "detailReason") String detailReason);

    @GetMapping(value = "/refund/rental/{id}")
    AfterSalesOrderDTO getSalesDetailById(@PathVariable(value = "id") Long id);

    @GetMapping(value = "/order/main/paycheckList/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderMainDTO> orderMainPayCheckList(@PathVariable(value = "pageNum") Integer pageNum,
                                               @PathVariable("pageSize") Integer pageSize,
                                               @RequestBody OrderMainDTO query);

    @GetMapping(value = "/order/main/deliveryPaycheckList/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderDTO> orderDeliveryPayCheckList(@PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize,
                                                   @RequestBody WorkOrderQueryDTO query);

    @PatchMapping(value = "/order/main/paycheck/single")
    void orderMainPayCheckSingle(@RequestParam(value = "id") String id,
                                 @RequestParam(value = "pass") Boolean pass,
                                 @RequestParam(value = "reason", required = false) String reason,
                                 @RequestParam(value = "payTerminal") Integer payTerminal,
                                 @RequestParam(value = "userPhone", required = false) String userPhone);

    @PostMapping(value = "/order/main/deliveryPaycheckList/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object orderDeliveryPayCheckExport(@RequestBody WorkOrderQueryDTO query);

    @PostMapping(value = "/order/main/paycheckList/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object orderMainPayCheckExport(@RequestBody OrderMainDTO query);

    @GetMapping(value = "/order/main/paycheckRecord/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderPayCheckDTO> paycheckRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                            @PathVariable(value = "pageSize") Integer pageSize,
                                            @RequestBody OrderPayCheckDTO orderPayCheckDTO);

    @PostMapping(value = "/reimburse/manage/online/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<OrderSubDTO> onlineReimburseManagePage(@PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize, @RequestBody OrderSubDTO query);

    @GetMapping(value = "/order/main/paycheckRecord/{id}/info")
    OrderPayCheckDTO paycheckRecordInfo(@PathVariable("id") Integer id, @RequestParam(value = "orderType", required = true) Integer orderType);


//    @PostMapping(value = {"/reimburse/manage/export"}, consumes = MediaType.APPLICATION_JSON_VALUE)
//    List<refundManageExportDTO> exportReimburse(OrderSubDTO dto);

    @PostMapping(value = {"/refund/record/export"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<refundManageExportDTO> exportRefund(@RequestBody OrderSubDTO dto);

    @GetMapping(value = "/order/refund/detail")
    RefundDetailDTO getRefundeDetail(@RequestParam(value = "id") Long id);

    /**
     * @Author ycl
     * @Description 发货列表-发货记录相关导出
     * @Date 11:03 2019/11/13
     * @Param
     **/
    @RequestMapping(value = "/order/delivery/record/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Object> orderDeliveryRecordExport(@RequestParam(value = "exportType") Integer exportType,
                                           @RequestBody DeliveryConditionDTO dto);

    @PostMapping(value = "/renewOrder/doBy", consumes = MediaType.APPLICATION_JSON_VALUE)
    void renewOrderDoBy(@RequestBody PayRecordDTO payRecord);

    @PostMapping(value = "/renewOrder/allotRenewIncome")
    void allotRenewIncome(@RequestParam(value = "renewOrderIdStr") String renewOrderIdStr);

    @PatchMapping(value = "/workorder/changeProductAndFee")
    void changeDeviceModel(@RequestParam(name = "workOrderId") String workOrderId,
                           @RequestParam(name = "productId") Integer productId,
                           @RequestParam(name = "costId") Integer costId);

    @PostMapping(value = "/manualTool/orderPayCallback")
    void orderPayCallback(@RequestParam(name = "mainOrderId") String mainOrderId);

    @PostMapping(value = "/manualTool/workOrderPayCallback")
    void workOrderPayCallback(@RequestParam(name = "mainOrderId") String mainOrderId);

    @PostMapping(value = "/manualTool/renewOrderPayCallback")
    void renewOrderPayCallback(@RequestParam(name = "mainOrderId") String mainOrderId);

    @PostMapping(value = "/workorder/unfinished", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<WorkOrderUnfinishedRsDTO> getWorkOrderForUnfinished(@RequestBody TransferAreaInfoDTO transferAreaInfoDTO);

    @PostMapping(value = "/workorder/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    void transferOrderDevice(@RequestBody List<TransferAreaInfoDTO> transferAreaInfoDTOS);

    /**
     * 根据条件查询退机工单信息
     */
    @GetMapping(value = "/workorderBack/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<WorkOrderBackDTO> getWorkOrderBackList(@RequestBody WorkOrderBackQueryDTO query, @PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 描述：根据工单id获取退机工单信息
     *
     * @param workOrderId 退机工单ID
     **/
    @GetMapping(value = "/workorderBack/{id}")
    WorkOrderBackDTO getWorkOrderBackById(@PathVariable(value = "id") Integer id);

    /**
     * 创建退机工单
     *
     * @param WorkOrderBackDTO 退机工单
     */
    @PostMapping(value = "/workorderBack")
    void createWorkOrderBack(@RequestBody WorkOrderBackDTO workOrderBackDTO);

    /**
     * 业务管理后台 - 创建移机工单
     *
     * @param dto
     */
    @PostMapping(value = "/move/water/device/order/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    Void save(@RequestBody MoveWaterDeviceOrderDTO dto);

    @GetMapping(value = "/move/water/device/order/{id}")
    MoveWaterDeviceOrderDTO getMoveWaterDeviceOrderById(@PathVariable(value = "id") String id);


    @GetMapping(value = "/move/water/device/order/{pageNum}/{pageSize}")
    PageVO<MoveWaterDeviceOrderDTO> moveWaterOrderPage(@PathVariable(value = "pageNum") Integer pageNum, @PathVariable(value = "pageSize") Integer pageSize, @RequestBody MoveWaterDeviceOrderQuery query);
}
