package com.yimao.cloud.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.app.feign.HraFeign;
import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.OrderFrom;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import com.yimao.cloud.pojo.dto.order.OrderCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderProductCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubListDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhilin.he
 * @description 子订单接口
 * @date 2019/1/16 9:17
 **/
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Api(tags = "OrderSubController")
public class OrderSubController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private HraFeign hraFeign;

    /**
     * @param operationType 操作类型
     * @param pageNum       当前页
     * @param pageSize      每页显示条数
     * @return java.lang.Object
     * @description 经销商app-我的订单列表根据订单状态
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @GetMapping(value = "/order/sub/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的订单", notes = "查询订单列表根据订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public ResponseEntity<PageVO<OrderSubListDTO>> myOrderList(@RequestParam(value = "operationType") Integer operationType,
                                                               @RequestParam(value = "timeType", required = false) Integer timeType,
                                                               @RequestParam(value = "productName", required = false) String productName,
                                                               @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                               @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                               @PathVariable(value = "pageNum") Integer pageNum,
                                                               @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSubListDTO> map = orderFeign.myOrderList(operationType, timeType, productName, addresseeName, addresseePhone, pageNum, pageSize);
        return ResponseEntity.ok(map);
    }


    /**
     * 查询客户订单列表:根据产品一级类目和订单状态
     */
    @GetMapping(value = "/order/sub/app/customer/{pageNum}/{pageSize}")
    @ApiOperation(value = "我的客户订单", notes = "查询客户订单列表根据产品一级类目和订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productFirstCategoryId", value = "产品一级类目id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型:  -1-全部 0-待付款 1-待发货/待接单 2-待收货 3-已完成 4-退款/退货", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "产品模式：1-实物；2-虚拟；3-租赁；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、上周；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productName", value = "产品名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseeName", value = "收货人姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "addresseePhone", value = "手机号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部（默认） 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "subDistributorId", value = "经销商子账号ID，当queryType=2时，此为必传", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")
    })
    public ResponseEntity<PageVO<OrderSubListDTO>> myCustomerOrderSubList(@RequestParam(value = "userId", required = false) Integer userId,
                                                                          @RequestParam(value = "productFirstCategoryId") Integer productFirstCategoryId,
                                                                          @RequestParam(value = "operationType") Integer operationType,
                                                                          @RequestParam(value = "productMode") Integer productMode,
                                                                          @RequestParam(value = "timeType", required = false) Integer timeType,
                                                                          @RequestParam(value = "productName", required = false) String productName,
                                                                          @RequestParam(value = "addresseeName", required = false) String addresseeName,
                                                                          @RequestParam(value = "addresseePhone", required = false) String addresseePhone,
                                                                          @RequestParam(value = "queryType", required = false) Integer queryType,
                                                                          @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId,
                                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                                          @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSubListDTO> map = orderFeign.myCustomerOrderSubList(userId, productFirstCategoryId, operationType, productMode, timeType, productName, addresseeName, addresseePhone, 2, queryType, subDistributorId, pageNum, pageSize);
        return ResponseEntity.ok(map);
    }

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号查询订单
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @GetMapping(value = "/order/sub/{id}/{operationType}")
    @ApiOperation(value = "客户端我的订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "operationType", defaultValue = "-1", value = "操作类型: -1-全部 0-待付款 1-待发货 2-待收货 3-已完成 4-退款/退货/待审核 5-待退货 6-待退款 7-取消记录", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", dataType = "Long", allowMultiple = true, required = true, paramType = "query")
    })
    public ResponseEntity<OrderSubListDTO> findOrderInfoById(@PathVariable("id") Long id, @PathVariable("operationType") Integer operationType, @RequestParam(value = "ids") List<Long> ids) {
        return ResponseEntity.ok(orderFeign.findOrderInfoById(id, operationType, ids));
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
    public ResponseEntity<OrderSubListDTO> getMyCustomerOrderDetailById(@PathVariable Long id, @PathVariable Integer operationType, @RequestParam(value = "ids") List<Long> ids) {
        return ResponseEntity.ok(orderFeign.getMyCustomerOrderDetailById(id, operationType, ids));
    }

    /**
     * @param mainOrderId
     * @param status
     * @return java.util.Map<java.lang.String>
     * @description 我的订单-查询主订单信息和统计水机状态数量
     * @author zhilin.he
     * @date 2019/1/16 16:49
     */
    @GetMapping(value = "/order/sub/count")
    @ApiOperation(value = "查询主订单信息和统计水机状态数量", notes = "查询主订单信息和统计水机状态数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "主订单号", dataType = "Long", required = true, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "订单状态:0-待付款 1-待审核 2-待发货 3-待出库  4-待收货 5-交易成功 6-售后中 7-交易关闭 8-已取消", required = true, dataType = "Long", paramType = "query")})
    public Object orderNumInfo(@RequestParam(value = "mainOrderId") Long mainOrderId,
                               @RequestParam(value = "status") Integer status) {
        Map<String, Object> map = orderFeign.orderNumInfo(mainOrderId, status);
        return ResponseEntity.ok(map);
    }

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/sub/{id}"})
    @ApiOperation(value = "根据订单号删除订单", notes = "根据订单号删除订单")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object deleteOrder(@PathVariable("id") Long id) {
        orderFeign.deleteOrder(id, OrderFrom.DIS_APP.value);
        return ResponseEntity.noContent().build();
    }

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
    @ApiOperation(value = "批量取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cancelReason", value = "取消订单原因", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cancelDetail", value = "取消订单详情", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", allowMultiple = true, dataType = "Long", required = true, paramType = "query")})
    public JSONObject updateOrderStatusBatch(@RequestParam(value = "cancelReason") String cancelReason,
                                             @RequestParam(value = "cancelDetail", required = false) String cancelDetail,
                                             @RequestParam(value = "ids") List<Long> ids) {
        return orderFeign.updateOrderStatusBatch(cancelReason, cancelDetail, ids);
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
        orderFeign.updateOrderDeleted(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取经销商及其下属客户订单总数
     *
     * @param distributorId 经销商Id
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2019/1/26
     */
    @GetMapping(value = "/order/sub/statistics/{distributorId}")
    @ApiOperation(value = "获取经销商及其下属客户订单总数", notes = "获取经销商及其下属客户订单总数")
    @ApiImplicitParam(name = "distributorId", value = "经销商Id", dataType = "Long", required = true, paramType = "path")
    public ResponseEntity getOrderCountByUserId(@PathVariable(value = "distributorId") Integer distributorId) {
        Integer count = orderFeign.getOrderCountByUserId(distributorId);
        return ResponseEntity.ok(count);
    }

    /**
     * 当前用户的不同订单状态下的订单数量
     */
    @GetMapping(value = "/order/count/check")
    @ApiOperation(value = "我的--我的订单--订单状态下小红点的是否显示")
    public List<OrderCountDTO> checkUserOrderByStatus() {
        Integer userId = userCache.getUserId();
        return orderFeign.checkUserOrderByStatus(userId);
    }

    /**
     * 当前用户的客户订单数量
     */
    @GetMapping(value = "/order/count/check/customer")
    @ApiOperation(value = "我的--客户订单--客户订单数量")
    public JSONObject selectCustomerOrderCount() {
        JSONObject object = new JSONObject();
        Integer userId = userCache.getUserId();
        object.put("count", orderFeign.selectCustomerOrderCount(userId));
        return object;
    }

    /**
     * 查询物流信息
     *
     * @return
     */
    @GetMapping(value = "/order/delivery/query")
    @ApiOperation(value = "查询物流信息", notes = "查询物流信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logisticsNo", value = "物流单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "子订单号", required = true, dataType = "Long", paramType = "query"),
    })
    public Object query(@RequestParam(value = "logisticsNo") String logisticsNo,
                        @RequestParam(value = "orderId") Long orderId) {
        return ResponseEntity.ok(orderFeign.query(logisticsNo, orderId));
    }

    /**
     * @param id
     * @param auditStatus
     * @return java.lang.Object
     * @description
     * @author zhilin.he  我的订单--经销商退单审核
     * @date 2019/8/22 14:15
     */
    @PutMapping(value = "/refund/audit/distributor")
    @ApiOperation(value = "经销商退单审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态：1、同意退单；0、继续服务", required = true, dataType = "Long", paramType = "query")})
    public Object updateAuditStatus(@RequestParam(value = "id") Long id,
                                    @RequestParam(value = "auditStatus") Integer auditStatus) {
        return orderFeign.updateAuditStatus(id, auditStatus);
    }


    @GetMapping(value = "/order/orderConfig")
    @ApiOperation(value = "查询订单配置信息")
    public OrderConfigDTO getOrderConfig() {
        return orderFeign.getOrderConfig();
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
    public Object customerOrderCountByType(@PathVariable(value = "userId") Integer userId,
                                           @RequestParam(value = "queryType", required = false) Integer queryType,
                                           @RequestParam(value = "subDistributorId", required = false) Integer subDistributorId) {
        List<OrderProductCountDTO> mapList = orderFeign.customerOrderCountByType(userId, queryType, subDistributorId);
        return ResponseEntity.ok(mapList);
    }

    @RequestMapping(value = "/order/card/detail", method = RequestMethod.GET)
    @ApiOperation(value = "订单详情:查看卡号", notes = "订单详情：根据订单号查询评估卡")
    @ApiImplicitParam(name = "orderId", value = "订单ID(子订单)", dataType = "Long", required = true, paramType = "query")
    public List<HraTicketDTO> cardDetailByOrderId(@RequestParam("orderId") Long orderId) {
        userCache.getUserId();
        return hraFeign.cardDetailByOrderId(orderId);
    }
}
