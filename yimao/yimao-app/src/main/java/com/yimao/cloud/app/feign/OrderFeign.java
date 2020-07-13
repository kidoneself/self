package com.yimao.cloud.app.feign;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.MoveWaterDeviceOrderVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeGrandTotalVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhilin.he
 * @description 订单服务调用
 * @date 2019/1/9 14:16
 **/
@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

    /**************************************购物车接口-----start-----******************************/
    /**
     * 添加购物车
     *
     * @param dto 购物车信息
     */
    @PostMapping(value = "/cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveShopCart(@RequestBody ShopCartDTO dto);

    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车ID
     */
    @DeleteMapping(value = "/cart")
    void deleteShopCart(@RequestParam(value = "cartIds") List<Integer> cartIds);

    /**
     * 修改购物车
     *
     * @param dto 购物车信息
     */
    @PatchMapping(value = "/cart", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateShopCart(@RequestBody ShopCartDTO dto);

    /**
     * 购物车数量添加
     *
     * @param cartId   购物车ID
     * @param addCount 添加数量
     */
    @PatchMapping(value = "/cart")
    void updateCount(@RequestParam(value = "cartId") Integer cartId, @RequestParam(value = "addCount") Integer addCount);

    /**
     * 查询购物车列表
     *
     * @param terminal          1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    @GetMapping(value = "/cart")
    JSONObject listShopCart(@RequestParam(value = "terminal") Integer terminal,
                            @RequestParam(value = "productCategoryId", required = false) Integer productCategoryId);

    /**
     * 查询购物车产品数量
     *
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @GetMapping(value = "/cart/count")
    Integer sumCount(@RequestParam("terminal") Integer terminal);

    /**
     * 查询加购的产品前台一级类目列表
     *
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @GetMapping(value = "/cart/productCategory")
    JSONObject listShopCartProductCategory(@RequestParam("terminal") Integer terminal);

    /**
     * 购物车商品结算
     *
     * @param orderDTO 前端传递的购物车订单信息
     */
    @PostMapping(value = "/order/settlement", consumes = MediaType.APPLICATION_JSON_VALUE)
    JSONObject orderSettlement(@RequestBody OrderDTO orderDTO);

    /**
     * 填写订单--发票须知
     */
    @GetMapping(value = "/order/invoiceNotes")
    JSONObject orderInvoiceNotes();

    /**************************************购物车接口-----end-----******************************/

    /**************************************主订单接口-----start-----******************************/

//    /**
//     * @param orderDTO
//     * @param type
//     * @return java.lang.Object
//     * @description 下单
//     * @author zhilin.he
//     * @date 2019/1/9 14:41
//     */
//    @RequestMapping(value = "/order/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    Integer insert(@RequestBody(required = false) OrderDTO orderDTO,
//                   @PathVariable("type") String type);

    /**
     * @param id
     * @return org.springframework.http.ResponseEntity
     * @description 根据id查询主订单
     * @author zhilin.he
     * @date 2019/1/9 14:43
     */
    @RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
    OrderMainDTO findById(@PathVariable("id") Long id);

    /**************************************主订单接口-----end-----******************************/


    /**************************************子订单接口-----start-----******************************/
    /**
     * @param operationType 操作类型
     * @param pageNum       当前页
     * @param pageSize      每页显示条数
     * @return java.lang.Object
     * @description 查询经销商app-我的订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @RequestMapping(value = "/order/sub/app/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<OrderSubListDTO> myOrderList(@RequestParam(value = "operationType") Integer operationType,
                                        @RequestParam(value = "timeType", required = false) Integer timeType,
                                        @RequestParam(value = "productName", required = false) String productName,
                                        @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                        @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                        @PathVariable(value = "pageNum") Integer pageNum,
                                        @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * 查询客户订单列表根据产品一级类目和订单状态
     */
    @GetMapping(value = "/order/sub/app/customer/{pageNum}/{pageSize}")
    PageVO<OrderSubListDTO> myCustomerOrderSubList(@RequestParam(value = "userId", required = false) Integer userId,
                                                   @RequestParam(value = "productFirstCategoryId") Integer productFirstCategoryId,
                                                   @RequestParam(value = "operationType", required = false) Integer operationType,
                                                   @RequestParam(value = "productMode", required = false) Integer productMode,
                                                   @RequestParam(value = "timeType", required = false) Integer timeType,
                                                   @RequestParam(value = "productName", required = false) String productName,
                                                   @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                   @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                   @RequestParam(value = "source", required = false) Integer source,
                                                   @RequestParam(value = "queryType", required = false) Integer queryType,
                                                   @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId,
                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                   @PathVariable(value = "pageSize") Integer pageSize);

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号查询订单
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @RequestMapping(value = "/order/sub/{id}/{operationType}", method = RequestMethod.GET)
    OrderSubListDTO findOrderInfoById(@PathVariable("id") Long id, @PathVariable("operationType") Integer operationType, @RequestParam(value = "ids") List<Long> ids);

    /**
     * 客户端客户订单详情
     */
    @GetMapping(value = "/order/sub/customer/{id}/{operationType}")
    OrderSubListDTO getMyCustomerOrderDetailById(@PathVariable("id") Long id, @PathVariable("operationType") Integer operationType, @RequestParam(value = "ids") List<Long> ids);

    /**
     * @param mainOrderId
     * @param status
     * @return java.util.Map
     * @description 我的订单-查询主订单信息和统计水机状态数量
     * @author zhilin.he
     * @date 2019/1/16 16:49
     */
    @RequestMapping(value = "/order/sub/count", method = RequestMethod.GET)
    Map<String, Object> orderNumInfo(@RequestParam(value = "mainOrderId") Long mainOrderId,
                                     @RequestParam(value = "status") Integer status);

    /**
     * @param id       订单号
     * @param terminal 订单来源端
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @RequestMapping(value = "/order/sub/{id}", method = RequestMethod.DELETE)
    Object deleteOrder(@PathVariable("id") Long id, @RequestParam(value = "terminal") Integer terminal);

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
     * @description 批量更新是否删除订单(逻辑删除)
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @DeleteMapping(value = {"/order/sub/deleted"})
    void updateOrderDeleted(@RequestParam(value = "ids") List<Long> ids);

    /**
     * 当前用户的不同订单状态下的订单数量
     */
    @GetMapping(value = "/order/count/check/{userId}")
    List<OrderCountDTO> checkUserOrderByStatus(@PathVariable(value = "userId") Integer userId);

    /**
     * 当前用户的客户订单数量
     */
    @GetMapping(value = "/order/count/check/customer/{userId}")
    Integer selectCustomerOrderCount(@PathVariable(value = "userId") Integer userId);
    /**************************************子订单接口-----end-----******************************/

    /**
     * 获取经销商及其下属客户订单总数
     *
     * @param distributorId 经销商Id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/26
     */
    @RequestMapping(value = "/order/sub/statistics/{distributorId}", method = RequestMethod.GET)
    Integer getOrderCountByUserId(@PathVariable(value = "distributorId") Integer distributorId);


    /*****************************翼猫榜************************************************/

    @RequestMapping(value = "/ranking/dealer/self", method = RequestMethod.GET)
    DealerSalesDTO dealerNationalRanking();

    @RequestMapping(value = "/ranking/dealer/top", method = RequestMethod.GET)
    List<DealerSalesDTO> topDealerNationalRanking();

    @RequestMapping(value = "/ranking/dealer/{rankingId}", method = RequestMethod.GET)
    void rankingDealerLink(@PathVariable(value = "rankingId") Integer rankingId);

    @RequestMapping(value = "/ranking/station/self", method = RequestMethod.GET)
    StationSalesDTO stationNationalRanking();

    @RequestMapping(value = "/ranking/station/top", method = RequestMethod.GET)
    List<StationSalesDTO> topStationNationalRanking();

    @RequestMapping(value = "/ranking/station/{rankingId}", method = RequestMethod.GET)
    void rankingStationLink(@PathVariable(value = "rankingId") Integer rankingId);


    /*****************************订单************************************************/

    /**
     * app我的订单
     *
     * @param status    订单状态
     * @param beginTime 下单开始时间
     * @param endTime   下单结束时间
     * @param keys      收件人/手机号
     * @param orderId   订单号
     * @param pageNum
     * @param pageSize
     */
    @RequestMapping(value = "/order/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<WxOrderDTO> wxOrderList(@RequestParam(value = "userId") Integer userId,
                                   @RequestParam(required = false, value = "status") Integer status,
                                   @RequestParam(required = false, value = "beginTime") String beginTime,
                                   @RequestParam(required = false, value = "endTime") String endTime,
                                   @RequestParam(required = false, value = "keys") String keys,
                                   @RequestParam(required = false, value = "orderId") String orderId,
                                   @PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize);


//    /**
//     * app上传支付凭证
//     *
//     * @author hhf
//     * @date 2019/7/13
//     */
//    @RequestMapping(value = "/pay/audit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//    void addPaymentVoucher(@RequestBody PayAuditRecordDTO dto);


    /**
     * 下单
     *
     * @param type 1-单件商品下单；2-购物车下单
     */
    @RequestMapping(value = "/order/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    JSONObject createOrder(@RequestBody OrderDTO orderDTO, @PathVariable(value = "type") String type);


    //==============================下单支付============================================================

    @PostMapping(value = "/wxpay/unified/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object unifiedorder(@RequestBody WechatPayRequest payRequest);

    @PostMapping(value = "/wxpay/query/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object orderQuery(@RequestBody WechatPayRequest payRequest);

    @PostMapping(value = "/alipay/tradeapp", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object tradeapp(@RequestBody AliPayRequest payRequest);

    @PostMapping(value = "/alipay/tradequery/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object tradequery(@RequestBody AliPayRequest payRequest);

    @PostMapping(value = "/otherpay/submitcredential")
    void otherPaySubmitCredential(@RequestParam(value = "mainOrderId") Long mainOrderId,
                                  @RequestParam(value = "payType") Integer payType,
                                  @RequestParam(value = "payCredential") String payCredential);

    //==============================下单支付============================================================

    /**
     * @return
     * @description 获取经销商收益统计
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/grandTotal")
    ProductIncomeGrandTotalVO productIncomeGrandTotalforApp(@RequestParam("incomeType") Integer incomeType);

    /**
     * @param dateTime
     * @param type
     * @description app经销商收益概况
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/reportOverview")
    Map<String, Object> productIncomeReportOverviewforApp(@RequestParam("dateTime") Date dateTime,
                                                          @RequestParam("type") Integer type,
                                                          @RequestParam("incomeType") Integer incomeType);

    /**
     * @param startTime
     * @param endTime
     * @description app经销商收益金额明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/reportOverviewList/{pageNum}/{pageSize}")
    Map<String, Object> productIncomeReportOverviewListforApp(@RequestParam("dateTime") Date dateTime,
                                                              @RequestParam("type") Integer type,
                                                              @RequestParam("incomeType") Integer incomeType,
                                                              @PathVariable("pageNum") Integer pageNum,
                                                              @PathVariable("pageSize") Integer pageSize);

    /**
     * @description app经销商收益金额明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/list/{pageNum}/{pageSize}")
    Map<String, Object> productIncomeListForApp(@RequestParam(value = "terminal", required = false) Integer terminal,
                                                @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                @RequestParam(value = "distributorId", required = false) Integer distributorId,
                                                @RequestParam(value = "queryType", required = false) Integer queryType,
                                                @RequestParam(value = "status", required = false) Integer status,
                                                @RequestParam(value = "timeType", required = false) Integer timeType,
                                                @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                                @RequestParam(value = "beginDate", required = false) Date beginDate,
                                                @RequestParam(value = "endDate", required = false) Date endDate,
                                                @PathVariable("pageNum") Integer pageNum,
                                                @PathVariable("pageSize") Integer pageSize);

    /**
     * @description app经销商收益收益详情
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/{id}/detail")
    Map<String, Object> productIncomeDetailForApp(@PathVariable("id") Integer id, @RequestParam("incomeType") Integer incomeType);

    /**
     * 查询物流信息
     *
     * @return
     */
    @RequestMapping(value = "/order/delivery/query", method = RequestMethod.GET)
    Object query(@RequestParam(value = "logisticsNo") String logisticsNo,
                 @RequestParam(value = "orderId") Long orderId);

    /**
     * @param id
     * @param auditStatus
     * @return java.lang.Object
     * @description
     * @author zhilin.he  我的订单--经销商退单审核
     * @date 2019/8/22 14:15
     */
    @PutMapping(value = "/refund/audit/distributor")
    Object updateAuditStatus(@RequestParam(value = "id") Long id,
                             @RequestParam(value = "auditStatus") Integer auditStatus);

    /**
     * 翼猫APP-我的-水机续费-续费记录
     */
    @GetMapping(value = "/order/renew/sn")
    Object renewRecord(@RequestParam(value = "snCode") String snCode, @RequestParam(value = "distributorId") Integer distributorId);

    /**
     * 查询订单配置信息
     */
    @GetMapping(value = "/order/orderConfig")
    OrderConfigDTO getOrderConfig();

    /**
     * 客户订单数量： 根据E家和类型，查询不同状态下的数量
     *
     * @param userId 用户e家号
     * @return map
     * @author liuhao
     */
    @GetMapping(value = "/order/customer/count/{userId}/type")
    List<OrderProductCountDTO> customerOrderCountByType(@PathVariable(value = "userId") Integer userId,
                                                        @RequestParam(value = "queryType", required = false) Integer queryType,
                                                        @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId);

    @PostMapping(value = "/payaccount")
    PayAccountDetail getPayAccount(@RequestParam(value = "outTradeNo") String outTradeNo, @RequestParam(value = "companyId") Integer companyId, @RequestParam(value = "platform") Integer platform, @RequestParam(value = "clientType") Integer clientType, @RequestParam(value = "receiveType") Integer receiveType);

    /***
     * 统计用户日期范围内的收益
     * @param query
     * @return
     */
    @PostMapping(value = "/order/productIncome/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<IncomeStatsResultDTO> productIncomeStats(@RequestBody IncomeStatsQueryDTO query);

    /***
     * 获取招商统计排行
     * @param query
     * @return
     */
    @PostMapping(value = "/order/sale/perform/rank", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<SalePerformRankDTO> getSalePerformRankData(@RequestBody SalePerformRankQuery query);

    /***
     * 统计公司日期范围内的续费销售额
     * @param query
     * @return
     */
    @PostMapping(value = "/order/renewSale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<SalesStatsDTO> getRenewSaleStats(@RequestBody SalesStatsQueryDTO query);

    /***
     * 统计公司日期范围内的产品销售额
     * @param query
     * @return
     */
    @PostMapping(value = "/order/prodSale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    SalesStatsResultDTO getProductSaleStats(@RequestBody SalesStatsQueryDTO query);

    /***
     * 经销商app-统计产品销售额信息切换年/月/日
     * @param query
     * @return
     */
    @PostMapping(value = "/order/prodSale/amount/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getProductSaleAmountStats(@RequestBody SalesStatsQueryDTO query);

    /***
     * 经销商app-交易成功订单增长数据切换月/日
     * @param query
     * @return
     */
    @PostMapping(value = "/order/trade/suc/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getTradeSucOrderStats(@RequestBody SalesStatsQueryDTO query);


    /**
     * @param
     * @description 经销商app-经营报表-汇总统计
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/agent/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    AgentSalesOverviewDTO getOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query);

    /**
     * @param
     * @description 经销商app-经营报表-累计销售金额统计表
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/agent/trend/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<SalesStatsDTO> getOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query);


    /**
     * @param
     * @description 经销商app-经营报表-汇总统计
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/renew/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    AgentSalesOverviewDTO getRenewOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query);

    /**
     * @param
     * @description 经销商app-经营报表-累计销售金额统计表
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/renew/trend/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<SalesStatsDTO> getRenewOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query);

    /**
     * 净水设备订单分类名称
     *
     * @return
     */
    @GetMapping(value = "/order/renew/type/name")
    List<String> getRenewOrderTypeNames();


}
