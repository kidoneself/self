package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.utils.IpUtil;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderRefundAuditDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderRefundController")
public class OrderRefundController {

    @Resource
    private OrderFeign orderFeign;


    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @return OrderSalesInfoDTO
     * @description 查询订单列表, 根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @PostMapping(value = "/order/refund/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件", notes = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderConditionDTO", value = "订单查询条件", dataType = "OrderConditionDTO", required = true, paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public Object orderSalesList(@RequestBody OrderConditionDTO orderConditionDTO,
                                 @PathVariable(value = "pageNum") Integer pageNum,
                                 @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderFeign.orderSalesList(orderConditionDTO, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }


    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     * @description 根据售后id查询售后订单详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     */
    @GetMapping(value = {"/order/refund/{id}"})
    @ApiOperation(value = "根据售后id查询售后订单详情", notes = "根据售后id查询售后订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderFeign.orderRefundInfo(id));
    }

    /**
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     * @description 查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     */
    @GetMapping(value = {"/order/refund/audit/{id}"})
    @ApiOperation(value = "根据售后审核id查询售后订单审核或处理记录详情", notes = "根据售后审核id查询售后订单审核或处理记录详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundAuditInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderFeign.orderRefundAuditInfo(id));
    }

    /**
     * @param orderRefundAuditDTO
     * @return org.springframework.http.ResponseEntity
     * @description 售后审核（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/audit/batch"})
    @ApiOperation(value = "售后审核（批量）", notes = "售后审核（批量）")
    @ApiImplicitParam(name = "orderRefundAuditDTO", value = "订单售后审核条件", dataType = "OrderRefundAuditDTO", paramType = "body", required = true)
    public ResponseEntity refundAuditBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO, HttpServletRequest request) {
        orderRefundAuditDTO.setIp(IpUtil.getIp(request));
        orderFeign.refundAuditBatch(orderRefundAuditDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param saleOrderIds 订单售后id集合
     * @param pageNum      当前页
     * @param pageSize     每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO>
     * @description 查询提交物流列表根据订单售后id集合
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @GetMapping(value = "/order/refund/logistic/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询提交物流列表根据订单售后id集合", notes = "查询提交物流列表根据订单售后id集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "saleOrderIds", value = "订单售后id集合", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public Object orderLogisticSubmitList(@RequestParam(value = "saleOrderIds") String saleOrderIds,
                                          @PathVariable(value = "pageNum") Integer pageNum,
                                          @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderFeign.orderLogisticSubmitList(saleOrderIds, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }

    /**
     * @param orderRefundAuditDTO
     * @return org.springframework.http.ResponseEntity
     * @description 提交物流（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     */
    @PatchMapping(value = {"/order/refund/logistic/batch"})
    @ApiOperation(value = "提交物流（批量）", notes = "提交物流（批量）")
    @ApiImplicitParam(name = "orderRefundAuditDTO", value = "订单售后审核条件", dataType = "OrderRefundAuditDTO", paramType = "body", required = true)
    public ResponseEntity refundAuditLogisticBatch(@RequestBody OrderRefundAuditDTO orderRefundAuditDTO, HttpServletRequest request) {
        orderRefundAuditDTO.setIp(IpUtil.getIp(request));
        orderFeign.refundAuditLogisticBatch(orderRefundAuditDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 关闭退货
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @PatchMapping(value = {"/order/refund/close/{id}"})
    @ApiOperation(value = "关闭退货", notes = "关闭退货")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object orderRefundClose(@PathVariable("id") Long id) {
        orderFeign.orderRefundClose(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO>
     * @description 查询售后订单审核记录或处理记录列表
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    @GetMapping(value = "/order/refund/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件", notes = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderConditionDTO", value = "订单查询条件", dataType = "OrderConditionDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path")})
    public Object orderRefundAuditList(@RequestBody OrderConditionDTO orderConditionDTO,
                                       @PathVariable(value = "pageNum") Integer pageNum,
                                       @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<OrderSalesInfoDTO> orderList = orderFeign.orderRefundAuditList(orderConditionDTO, pageNum, pageSize);
        return ResponseEntity.ok(orderList);
    }
}
