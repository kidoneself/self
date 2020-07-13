package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.order.service.OrderRefundService;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderRefundAuditDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderRefundController")
public class OrderRefundController {

    @Resource
    private OrderRefundService orderRefundService;
    @Resource
    private UserCache userCache;


    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @description 查询订单列表, 根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @PostMapping(value = "/order/refund/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件", notes = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderConditionDTO", value = "订单查询条件", dataType = "OrderConditionDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")})
    public Object orderSalesList(@RequestBody OrderConditionDTO orderConditionDTO,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderRefundService.orderSalesList(orderConditionDTO, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }


    /**
     * @param id
     * @description 根据售后id查询售后订单详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     */
    @GetMapping(value = {"/order/refund/{id}"})
    @ApiOperation(value = "根据售后id查询售后订单详情", notes = "根据售后id查询售后订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderRefundService.orderRefundInfo(id));
    }

    /**
     * @param id
     * @description 查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     */
    @GetMapping(value = {"/order/refund/audit/{id}"})
    @ApiOperation(value = "根据售后审核id查询售后订单审核或处理记录详情", notes = "根据售后审核id查询售后订单审核或处理记录详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundAuditInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderRefundService.orderRefundAuditInfo(id));
    }

    /**
     * @param salesId
     * @param businessAuditStatus
     * @param reason
     * @description 业务部门审核状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     */
    @PutMapping(value = "/order/refund/audit/business")
    @ApiOperation(value = "业务部门审核", notes = "业务部门审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "salesId", value = "售后id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "businessAuditStatus", value = "业务部门审核状态", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "reason", value = "审核原因", dataType = "String", paramType = "query")})
    public Object updateBusinessAuditStatus(@RequestParam(value = "salesId") Long salesId,
                                            @RequestParam(value = "businessAuditStatus") Integer businessAuditStatus,
                                            @RequestParam(value = "reason", required = false) String reason) {
        //获取当前登录的用户信息【翼猫健康e家】【健康风险评估】【小猫店】
        String adminName = userCache.getCurrentAdminRealName();
        orderRefundService.updateBusinessAuditStatus(salesId, businessAuditStatus, adminName, reason);
        return ResponseEntity.notFound().build();
    }

    /**
     * @param orderRefundAuditDTO
     * @description 售后审核（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/audit/batch"})
    @ApiOperation(value = "售后审核（批量）", notes = "售后审核（批量）")
    @ApiImplicitParam(name = "orderRefundAuditDTO", value = "订单售后审核条件", dataType = "OrderRefundAuditDTO", paramType = "body", required = true)
    public ResponseEntity refundAuditBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO) {
        //获取当前登录的用户信息【翼猫健康e家】【健康风险评估】【小猫店】
        String adminName = userCache.getCurrentAdminRealName();
        orderRefundService.refundAuditBatch(orderRefundAuditDTO, adminName);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param saleOrderIds 订单售后id集合
     * @param pageNum      当前页
     * @param pageSize     每页显示条数
     * @description 查询提交物流列表根据订单售后id集合
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @GetMapping(value = "/order/refund/logistic/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询提交物流列表根据订单售后id集合", notes = "查询提交物流列表根据订单售后id集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "saleOrderIds", value = "订单售后id集合", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")})
    public Object orderLogisticSubmitList(@RequestParam(value = "saleOrderIds") String saleOrderIds,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderRefundService.orderLogisticSubmitList(saleOrderIds, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }

    /**
     * @param orderRefundAuditDTO
     * @description 提交物流（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/logistic/batch"})
    @ApiOperation(value = "提交物流（批量）", notes = "提交物流（批量）")
    @ApiImplicitParam(name = "orderRefundAuditDTO", value = "订单售后审核条件", dataType = "OrderRefundAuditDTO", paramType = "body", required = true)
    public ResponseEntity refundAuditLogisticBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO) {
        //获取当前登录的用户信息【翼猫健康e家】【健康风险评估】【小猫店】
        String adminName = userCache.getCurrentAdminRealName();
        orderRefundService.refundAuditLogisticBatch(orderRefundAuditDTO, adminName);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id 订单号
     * @description 关闭退货
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/refund/close/{id}"})
    @ApiOperation(value = "关闭退货", notes = "关闭退货")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundClose(@PathVariable("id") Long id) {
        String userName = userCache.getCurrentAdminRealName();
        orderRefundService.orderRefundClose(id, userName);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @description 查询售后订单审核记录或处理记录列表
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @GetMapping(value = "/order/refund/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件", notes = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderConditionDTO", value = "订单查询条件", dataType = "OrderConditionDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", required = true, paramType = "path")})
    public Object orderRefundAuditList(@RequestBody OrderConditionDTO orderConditionDTO,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderRefundService.orderRefundAuditList(orderConditionDTO, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }

}
