package com.yimao.cloud.order.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderProductCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubListDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.order.WxOrderDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.station.StationOrderGeneralSituationQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderBillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhilin.he
 * @description 订单管理
 * @date 2019/1/24 14:12
 */
@RestController
@Slf4j
@Api(tags = "OrderSubController")
public class OrderSubController {

    @Resource
    private OrderSubService orderSubService;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    @Resource
    private ProductIncomeRecordService productIncomeRecordService;

    /**
     * @param query    订单查询条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     * @description 查询订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @PostMapping(value = "/order/sub/{pageNum}/{pageSize}")
    public Object orderSubList(@RequestBody OrderConditionDTO query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderSubService.orderList(query, pageNum, pageSize);
    }

    /**
     * 经销商app-我的订单列表（单独写）
     *
     * @param operationType 操作类型
     * @param pageNum       当前页
     * @param pageSize      每页显示条数
     * @description 经销商app-我的订单列表,根据订单状态
     * @author zhilin.he
     * @date 2019/09/15::30
     */
    @GetMapping(value = "/order/sub/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的订单", notes = "查询订单列表根据订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operationType", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public PageVO<OrderSubListDTO> myOrderSubList(@RequestParam(value = "operationType") Integer operationType,
                                                  @RequestParam(value = "timeType", required = false) Integer timeType,
                                                  @RequestParam(value = "productName", required = false) String productName,
                                                  @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                  @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                  @PathVariable(value = "pageNum") Integer pageNum,
                                                  @PathVariable(value = "pageSize") Integer pageSize) {
        Integer userId = userCache.getUserId();
        OrderConditionDTO orderConditionDTO = new OrderConditionDTO();
        orderConditionDTO.setOperationType(operationType);
        orderConditionDTO.setUserId(userId);
        orderConditionDTO.setTimeType(timeType);
        orderConditionDTO.setProductName(productName);
        orderConditionDTO.setReceiver(addresseeName);
        orderConditionDTO.setReceiveMobile(addresseePhone);
        return orderSubService.myOrderSubList(orderConditionDTO, pageNum, pageSize);
    }

    /**
     * 客户订单 =>根据产品一级类目和订单状态
     */
    @GetMapping(value = "/order/sub/app/customer/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的客户订单", notes = "查询客户订单列表根据产品一级类目和订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productFirstCategoryId", value = "产品一级类目id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "产品模式：1-实物；2-虚拟；3-租赁；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "operationType", value = "操作类型:  -1-全部 0-待付款 1-待发货/待接单 2-待收货 3-已完成 4-退款/退货", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部（默认） 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "subDistributorId", value = "经销商子账号ID，当queryType=2时，此为必传", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")
    })
    public PageVO<OrderSubListDTO> myCustomerOrderSubList(@RequestParam(value = "userId", required = false) Integer userId,
                                                          @RequestParam(value = "productFirstCategoryId", required = false) Integer productFirstCategoryId,
                                                          @RequestParam(value = "productMode", required = false) Integer productMode,
                                                          @RequestParam(value = "operationType", required = false) Integer operationType,
                                                          @RequestParam(value = "timeType", required = false) Integer timeType,
                                                          @RequestParam(value = "productName", required = false) String productName,
                                                          @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                          @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                          @RequestParam(value = "source", required = false) Integer source,
                                                          @RequestParam(value = "queryType", required = false) Integer queryType,
                                                          @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId,
                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize) {
        Integer currentUserId = userCache.getUserId();
        OrderConditionDTO orderConditionDTO = new OrderConditionDTO();
        orderConditionDTO.setProductFirstCategoryId(productFirstCategoryId);
        orderConditionDTO.setOperationType(operationType);
        if (Objects.isNull(userId)) {
            orderConditionDTO.setUserId(currentUserId);
        } else {
            orderConditionDTO.setUserId(userId);
        }
        orderConditionDTO.setSource(source); //1-公众号查询 2-经销商APP查询
        orderConditionDTO.setTimeType(timeType);
        orderConditionDTO.setProductName(productName);
        orderConditionDTO.setReceiver(addresseeName);
        orderConditionDTO.setReceiveMobile(addresseePhone);
        orderConditionDTO.setProductType(productMode);
        if (queryType == null) {
            queryType = 0;
        }
        orderConditionDTO.setQueryType(queryType);
        orderConditionDTO.setSubDistributorId(subDistributorId);
        return orderSubService.myCustomerOrderList(orderConditionDTO, pageNum, pageSize);
    }

    /**
     * @param status    0-待付款 2-待发货 4-待收货 5-交易成功
     * @param beginTime 下单开始时间
     * @param endTime   下单结束时间
     * @param keys      收货人姓名/手机号
     * @param orderId   订单号
     * @param pageNum
     * @param pageSize
     * @Description: 公众号查询我的订单
     * @author ycl
     * @Create: 2019/4/11 9:30
     */
    @GetMapping("/order/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "订单状态:0-待付款 2-待发货 4-待收货 5-交易成功", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginTime", value = "下单开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keys", value = "收货人姓名/手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")})
    public PageVO<WxOrderDTO> wxOrderList(@RequestParam(value = "userId") Integer userId,
                                          @RequestParam(required = false, value = "status") Integer status,
                                          @RequestParam(required = false) String beginTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false) String keys,
                                          @RequestParam(required = false) String orderId,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {

        return orderSubService.wxOrderList(userId, status, beginTime, endTime, keys, orderId, pageNum, pageSize);
    }


    /**
     * 根据订单号查询订单基本信息
     *
     * @param id 订单号
     */
    @GetMapping(value = "/order/sub/{id}/basic")
    @ApiOperation(value = "根据订单号查询订单基本信息")
    @ApiImplicitParam(name = "id", value = "订单号", dataType = "Long", required = true, paramType = "path")
    public Object findOrderBasicInfoById(@PathVariable Long id) {
        return orderSubService.findOrderById(id);
    }


    /**
     * @param id 订单号
     * @description 根据订单号查询订单
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @GetMapping(value = "/order/sub/{id}")
    @ApiOperation(value = "根据订单号查询订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object findOrderInfoById(@PathVariable Long id) {
        return orderSubService.findFullOrderById(id);
    }

    /**
     * 后台管理系统查询订单详情
     *
     * @param id 订单号
     */
    @GetMapping(value = "/order/sub/{id}/detail")
    @ApiOperation(value = "根据订单号查询订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object getOrderDetailById(@PathVariable Long id) {
        return orderSubService.getOrderDetailById(id);
    }

    /**
     * @param id
     * @param operationType
     * @return java.lang.Object
     * @description 客户端我的订单详情
     * @author zhilin.he
     * @date 2019/8/5 9:49
     */
    @GetMapping(value = "/order/sub/{id}/{operationType}")
    @ApiOperation(value = "客户端我的订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", dataType = "Long", allowMultiple = true, required = true, paramType = "query")
    })
    public OrderSubListDTO getMyOrderDetailById(@PathVariable Long id, @PathVariable Integer operationType, @RequestParam(value = "ids") List<Long> ids) {
        return orderSubService.getMyOrderDetailById(id, operationType, ids, 1);
    }

    /**
     * 客户端客户订单详情
     */
    @GetMapping(value = "/order/sub/customer/{id}/{operationType}")
    @ApiOperation(value = "客户端客户订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", dataType = "Long", allowMultiple = true, required = true, paramType = "query")
    })
    public OrderSubListDTO getMyCustomerOrderDetailById(@PathVariable Long id, @PathVariable Integer operationType, @RequestParam(value = "ids") List<Long> ids) {
        return orderSubService.getMyOrderDetailById(id, operationType, ids, 2);
    }

    /**
     * @param mainOrderId
     * @param status
     * @description 我的订单-查询主订单信息和统计水机状态数量
     * @author zhilin.he
     * @date 2019/1/16 16:49
     */
    @GetMapping(value = "/order/sub/count")
    @ApiOperation(value = "查询主订单信息和统计水机状态数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "主订单号", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "订单状态:0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消", required = true, dataType = "Long", paramType = "query")})
    public Object orderNumInfo(@RequestParam(value = "mainOrderId") Long mainOrderId,
                               @RequestParam(value = "status") Integer status) {
        Integer userId = userCache.getUserId();
        OrderSubDTO orderSubDTO = new OrderSubDTO();
        orderSubDTO.setUserId(userId);
        orderSubDTO.setMainOrderId(mainOrderId);
        orderSubDTO.setStatus(status);
        Map<String, Object> map = orderSubService.orderNumInfo(orderSubDTO);
        return map;
    }


    /**
     * @param terminal 订单来源端
     * @param id       订单号
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/sub/{id}"})
    @ApiOperation(value = "根据订单号删除订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "子订单ID", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "订单来源端", dataType = "Long", required = true, paramType = "query")
    })
    public void deleteOrder(@PathVariable("id") Long id, @RequestParam(value = "terminal") Integer terminal) {
        orderSubService.deleteOrderSub(userCache.getCurrentAdminRealName(), id);
    }


    /**
     * @param subOrder 订单信息
     * @return void
     * @description 修改订单型号和地址信息(修改订单信息)
     * @author zhilin.he
     * @date 2019/1/25 14:09
     */
    @PatchMapping(value = "/order/sub")
    @ApiOperation(value = "修改订单")
    @ApiImplicitParam(name = "subOrder", value = "订单信息", required = true, dataType = "OrderSubDTO", paramType = "body")
    public void updateOrderInfo(@RequestBody OrderSubDTO subOrder) {
        orderSubService.updateOrderInfo(subOrder);
    }

    /**
     * @param cancelReason 取消订单原因
     * @param cancelDetail 取消订单详情
     * @param ids          订单id集合
     * @description 批量更新订单状态（批量取消订单）
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @PutMapping(value = {"/order/sub/batch/status"})
    @ApiOperation(value = "批量取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cancelReason", value = "取消订单原因", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cancelDetail", value = "取消订单详情", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", dataType = "Long", allowMultiple = true, required = true, paramType = "query")})
    public CommResult<JSONObject> updateOrderStatusBatch(@RequestParam(value = "cancelReason") String cancelReason,
                                                         @RequestParam(value = "cancelDetail", required = false) String cancelDetail,
                                                         @RequestParam(value = "ids") List<Long> ids) {
        OrderSubDTO orderSubDTO = new OrderSubDTO();
        orderSubDTO.setCancelReason(cancelReason);
        orderSubDTO.setRemark(cancelDetail);

        JSONObject json = orderSubService.updateOrderStatusBatch(orderSubDTO, ids);
        int failureSize = ((JSONArray) json.get("failureArray")).size();
        int successSize = ((JSONArray) json.get("successArray")).size();
        if (failureSize > 0 && successSize > 0) {
            return CommResult.dialogError("取消订单，部分成功，部分失败。", json);
        } else if (failureSize > 0) {
            return CommResult.msgError("取消订单失败。", json);
        } else {
            return CommResult.ok(json);
        }
    }

    /**
     * @param ids 订单id集合
     * @description 批量更新是否删除订单(逻辑删除)
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @DeleteMapping(value = {"/order/sub/deleted"})
    @ApiOperation(value = "批量删除订单")
    @ApiImplicitParam(name = "ids", value = "子订单id集合", dataType = "Long", allowMultiple = true, required = true, paramType = "query")
    public Object updateOrderDeleted(@RequestParam(value = "ids") List<Long> ids) {
        orderSubService.updateOrderDeleted(ids, userCache.getCurrentAdminRealName());
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询订单对账列表
     *
     * @param query    查询条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     */
    @PostMapping(value = "/order/sub/bill/{pageNum}/{pageSize}")
    public PageVO<OrderBillVO> queryOrderBillList(@RequestBody OrderBillQuery query,
                                                  @PathVariable Integer pageNum,
                                                  @PathVariable Integer pageSize) {
        return orderSubService.queryOrderBillList(query, pageNum, pageSize);
    }


    /**
     * 订单对账：汇总导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/order/sub/bill/export")
    public List<OrderBillExport> exportOrderBill(@RequestBody OrderBillQuery query) {
        return orderSubService.exportOrderBill(query);
    }

    /**
     * 获取经销商及其下属客户订单总数
     *
     * @param distributorId 经销商Id
     * @author hhf
     * @date 2019/1/26
     */
    @GetMapping(value = "/order/sub/statistics/{distributorId}")
    @ApiOperation(value = "获取经销商及其下属客户订单总数")
    @ApiImplicitParam(name = "distributorId", value = "经销商Id", dataType = "Long", required = true, paramType = "path")
    public Integer getOrderCountByUserId(@PathVariable(value = "distributorId") Integer distributorId) {
        Integer count = orderSubService.getOrderCountByUserId(distributorId);
        return count;
    }

    /**
     * @param
     * @Description: 我的订单：待发货数量、待收货数、收货地址数、客户订单总数
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/4/11 14:15
     */
    @GetMapping(value = "/order/count/{id}")
    @ApiOperation(value = "我的订单：待发货数量、待收货数、收货地址数、客户订单总数")
    public Map<String, Integer> getOrderCount(@PathVariable Integer id) {
        UserDTO userDTO = userFeign.getUserById(id);
        Map<String, Integer> shopCount = new HashMap<>(8);
        //待发货数量
        Integer fCount = orderSubService.selectOrderCount(id, 2);
        //待收货数量
        Integer sCount = orderSubService.selectOrderCount(id, 4);
        //客户订单数量
        Integer kCount = orderSubService.auditCount(userDTO);
        //收货地址个数
        Integer addressCount = userFeign.getAddressCountByUserId(userDTO.getId());
        shopCount.put("fCount", fCount);
        shopCount.put("sCount", sCount);
        shopCount.put("kCount", kCount);
        shopCount.put("addressCount", addressCount);
        return shopCount;
    }


    /**
     * @param userId
     * @param orderKeys
     * @param beginTime
     * @param endTime
     * @param keys
     * @param orderId
     * @param pageNum
     * @param pageSize
     * @Description: 客户订单列表
     * @author ycl
     * @Return: com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.WxOrderDTO>
     * @Create: 2019/4/22 14:56
     */
    @GetMapping("/order/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "客户订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "下单开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "keys", value = "收货人姓名/手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderKeys", value = "下单人(e家号/手机号/昵称)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public PageVO<WxOrderDTO> wxOrderList(@RequestParam(value = "userId") Integer userId,
                                          @RequestParam(required = false, value = "orderKeys") String orderKeys,
                                          @RequestParam(required = false, value = "beginTime") String beginTime,
                                          @RequestParam(required = false, value = "endTime") String endTime,
                                          @RequestParam(required = false, value = "keys") String keys,
                                          @RequestParam(required = false, value = "orderId") String orderId,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {

        return orderSubService.auditList(userId, orderKeys, beginTime, endTime, keys, orderId, pageNum, pageSize);
    }


    /**
     * @param query
     * @Description: 订单列表导出
     * @author ycl
     */
//    @PostMapping(value = "/order/export")
//    @ApiOperation(value = "订单列表导出")
//    public List<OrderExportDTO> orderExportList(@RequestBody OrderConditionDTO query,
//                                                @RequestParam(value = "pageNum") Integer pageNum,
//                                                @RequestParam(value = "pageSize") Integer pageSize) {
//        return orderSubService.orderExportList(query, pageNum, pageSize);
//    }

    /**
     * 订单列表导出总数
     */
    @PostMapping(value = "/order/export/count")
    public Integer orderExportListCount(@RequestBody OrderConditionDTO query) {
        return orderSubService.orderExportListCount(query);
    }


    @GetMapping(value = "/sale/order/count/{userId}")
    @ApiOperation(value = "分销用户的已分销数量")
    public Integer countSaleOrder(@PathVariable(value = "userId") Integer userId) {
        return productIncomeRecordService.countSaleOrder(userId);
    }


    /**
     * @Author ycl
     * @Description 根据手机号查询是否存在订单
     * @Date 17:10 2019/7/12
     * @Param
     **/
    @GetMapping(value = "/mobile/order/{mobile}")
    public boolean checkOrderByMobile(@PathVariable(value = "mobile") String mobile) {
        return orderSubService.checkOrderByMobile(mobile);
    }

    /**
     * 当前用户的不同订单状态下的订单数量
     */
    @GetMapping(value = "/order/count/check/{userId}")
    @ApiOperation(value = "我的--我的订单--订单状态下小红点的是否显示")
    public List<OrderCountDTO> checkUserOrderByStatus(@PathVariable(value = "userId") Integer userId) {
        return orderSubService.checkUserOrderByStatus(userId);
    }

    /**
     * 当前用户的客户订单数量
     */
    @GetMapping(value = "/order/count/check/customer/{userId}")
    @ApiOperation(value = "我的--客户订单--客户订单数量")
    public Integer selectCustomerOrderCount(@PathVariable(value = "userId") Integer userId) {
        return orderSubService.selectCustomerOrderCount(userId);
    }

    /**
     * 根据累计的水机、健康产品已完成的工单数，发放HRA优惠卡
     *
     * @param distributorId 经销商e家号
     * @param phone         手机号
     * @param userType      用户类型
     */
    @GetMapping(value = "/order/countcompletedorderfromdate")
    public Integer countCompletedOrderFromDate(@RequestParam(required = false) Integer distributorId,
                                               @RequestParam String phone,
                                               @RequestParam Integer userType) {
        Integer count = orderSubService.countCompletedOrderFromDate(distributorId, phone, userType);
        return count == null ? 0 : count;
    }

    /**
     * 客户订单数量： 根据E家和类型，查询不同状态下的数量
     *
     * @return map
     * @author liuhao
     */
    @GetMapping(value = "/order/customer/count/{userId}/type")
    @ApiOperation(value = "客户订单数量： 根据E家和类型，查询不同状态下的数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部（默认） 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "subDistributorId", value = "经销商子账号ID，当queryType=2时，此为必传", dataType = "Long", paramType = "query")
    })
    public List<OrderProductCountDTO> customerOrderCountByType(@PathVariable(value = "userId") Integer userId,
                                                               @RequestParam(value = "queryType", required = false) Integer queryType,
                                                               @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId) {
        return orderSubService.customerOrderCountByType(userId, queryType, subDistributorId);
    }

    /**
     * 站务系统-统计-流水统计-产品和hra
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/productAndHraSaleData")
    public FlowStatisticsDTO getProductAndHraSaleData(@RequestBody StatisticsQuery query) {

        return orderSubService.getProductAndHraSaleData(query);
    }

    /**
     * 站务系统-统计-商品统计 -汇总数据（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/getProductTabulateData")
    public Object getProductStatisticsInfoToStation(@RequestBody StatisticsQuery query) {

        return ResponseEntity.ok(orderSubService.getProductTabulateData(query));
    }

    /**
     * 站务系统-统计-商品统计-根据一级类目名称获取二级分类图表以及商品销售情况（站务系统调用）
     *
     * @param query
     * @param categoryName
     * @return
     */
    @GetMapping(value = "/order/station/getProductSalesStatusAndTwoCategoryPicRes")
    public Object getProductSalesStatusAndTwoCategoryPicRes(@RequestBody StatisticsQuery query, @RequestParam("categoryName") String categoryName) {

        return ResponseEntity.ok(orderSubService.getProductSalesStatusAndTwoCategoryPicRes(query, categoryName));
    }

    /**
     * 站务系统-订单-概况（站务系统调用）
     *
     * @param query
     * @return
     */
    @GetMapping(value = "/order/station/generalSituation")
    public Object getOrderGeneralSituation(@RequestBody StationOrderGeneralSituationQuery query) {

        return ResponseEntity.ok(orderSubService.getOrderGeneralSituation(query.getAreas(), query.getEngineerIds(), query.getDistributorIds(), query.getType()));
    }

    /**
     * 下架商品时需要将所有【待付款】的订单设置为【已取消】
     */
    @GetMapping(value = "/order/sub/cancelWhenOffshelf")
    public void cancelOrderWhenOffshelf(@RequestParam("productId") Integer productId) {
        orderSubService.cancelOrderWhenOffshelf(productId);
    }

    /***
     * 经销商app-统计产品销售额信息
     * @param query
     * @return
     */
    @PostMapping(value = "/order/prodSale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
   	public SalesStatsResultDTO getProductSaleStats(@RequestBody SalesStatsQueryDTO query){
		return orderSubService.getProductSaleStats(query);

    }

    /***
     * 经销商app-统计产品销售额信息切换年/月/日
     * @param query
     * @return
     */
    @PostMapping(value = "/order/prodSale/amount/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
   	public List<SalesStatsDTO> getProductSaleAmountStats(@RequestBody SalesStatsQueryDTO query){
		return orderSubService.getProductSaleAmountStats(query);

    }

    /***
     * 经销商app-交易成功订单增长数据切换月/日
     * @param query
     * @return
     */
    @PostMapping(value = "/order/trade/suc/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
   	public List<SalesStatsDTO> getTradeSucOrderStats(@RequestBody SalesStatsQueryDTO query){
		return orderSubService.getTradeSucOrderStats(query);

    }

    /**
     * @param
     * @description 经销商app-经营报表-汇总统计
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/agent/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AgentSalesOverviewDTO getOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query) {
        return orderSubService.getOrderSalesHomeReport(query);
    }

    /**
     * @param
     * @description 经销商app-经营报表-累计销售金额统计表
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    @PostMapping(value = "/order/agent/trend/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query) {
        return orderSubService.getOrderSalesTotalReport(query);
    }

}
