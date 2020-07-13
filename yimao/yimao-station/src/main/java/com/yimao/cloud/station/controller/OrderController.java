package com.yimao.cloud.station.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.PermissionTypeEnum;
import com.yimao.cloud.base.enums.StationQueryEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.station.OrderQuery;
import com.yimao.cloud.pojo.query.station.RefundOrderQuery;
import com.yimao.cloud.pojo.query.station.StationOrderGeneralSituationQuery;
import com.yimao.cloud.pojo.query.station.WorkOrderQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import com.yimao.cloud.station.aop.annotation.StationQuery;
import com.yimao.cloud.station.feign.OrderFeign;
import com.yimao.cloud.station.feign.ProductFeign;
import com.yimao.cloud.station.feign.UserFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 订单模块
 *
 * @author yaoweijun
 */
@RestController
@Api(tags = "OrderController")
@Slf4j
public class OrderController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserCache userCache;
    @Resource
    private UserFeign userFeign;

    /**
     * 订单-概况
     *
     * @return
     */
    @GetMapping("/order/station/generalSituation")
    @ApiOperation(value = "概况")
    public Object generalSituation() {
        //管理员权限类型
        Integer type = userCache.getStationUserInfo().getType();
        //获取售前服务区域
        Set<Integer> preServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.PRE_SALE.value);
        //获取售后服务区域
        Set<Integer> afterServiceAreas = userCache.getStationUserAreas(1, PermissionTypeEnum.AFTER_SALE.value);
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(afterServiceAreas);
        List<Integer> distributorIds = userFeign.getDistributorIdsByAreaIds(preServiceAreas);
        StationOrderGeneralSituationQuery query = new StationOrderGeneralSituationQuery();
        query.setAreas(preServiceAreas);
        query.setEngineerIds(engineerIds);
        query.setDistributorIds(distributorIds);
        query.setType(type);
        return orderFeign.getOrderGeneralSituation(query);
    }


    /**
     * 订单-订单列表-订单列表
     *
     * @param query
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/order/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "OrderQuery", paramType = "body")
    })
    public PageVO<OrderSubDTO> getOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                            @PathVariable(value = "pageSize") Integer pageSize,
                                            @RequestBody OrderQuery query) {
        log.info("查询={}", JSON.toJSONString(query));
        OrderConditionDTO orderConditionDTO = new OrderConditionDTO();
        BeanUtils.copyProperties(query, orderConditionDTO);
        PageVO<OrderSubDTO> page = orderFeign.orderSubList(orderConditionDTO, pageNum, pageSize);

        return page;
    }

    /**
     * 订单-订单列表-订单列表-详情
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/order/station/sub/{id}")
    @ApiOperation(value = "根据订单号查询订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public OrderSubDTO findOrderInfoById(@PathVariable Long id) {
        return orderFeign.findOrderDetailById(id);
    }

    /**
     * 订单-工单列表-工单列表
     *
     * @param query
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping("/workorder/station/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "WorkOrderQuery", paramType = "body")
    })
    public PageVO<WorkOrderResultDTO> getWorkOrderList(@PathVariable(value = "pageNum") Integer pageNum,
                                                       @PathVariable(value = "pageSize") Integer pageSize,
                                                       @RequestBody WorkOrderQuery query) {

        log.info("查询={}", JSON.toJSONString(query));
        //查询服务站下的安装工id集合
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());

        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }

        query.setEngineerIds(engineerIds);
        PageVO<WorkOrderResultDTO> page = orderFeign.getStationWorkOrderList(pageNum, pageSize, query);

        return page;
    }

    /**
     * 订单-工单列表-工单列表-详情
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/workorder/station/{workOrderId}")
    @ApiOperation(value = "根据工单id获取工单信息", notes = "根据工单id获取工单信息")
    @ApiImplicitParam(name = "workOrderId", value = "工单ID", dataType = "String", required = true, paramType = "path")
    public WorkOrderDTO getWorkOrderById(@PathVariable(value = "workOrderId") String workOrderId) {
        return orderFeign.getWorkOrderById(workOrderId);
    }

    /**
     * 订单-退款订单-租赁商品/实物商品列表
     *
     * @param query
     * @return
     */
    @StationQuery(StationQueryEnum.ListQuery)
    @PostMapping("/order/station/refund/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询退款订单(租赁商品/实物商品)列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询实体类", dataType = "RefundOrderQuery", required = true, paramType = "body")
    })
    public PageVO<AfterSalesOrderDTO> getRefundOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                         @PathVariable(value = "pageSize") Integer pageSize,
                                                         @RequestBody RefundOrderQuery query) {
        log.info("查询query={}", JSON.toJSONString(query));

        AfterSalesConditionDTO dto = new AfterSalesConditionDTO();

        BeanUtils.copyProperties(query, dto);

        log.info("查询AfterSalesConditionDTO={}", JSON.toJSONString(dto));

        PageVO<AfterSalesOrderDTO> page = orderFeign.rentalGoodsList(pageNum, pageSize, dto);
        return page;
    }

    /**
     * 订单-退款订单-租赁商品/实物商品列表-详情
     *
     * @param id
     * @return
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/order/station/refund/rental/{id}")
    @ApiOperation(value = "退款订单-租赁商品/实物商品 详情", notes = "租赁商品/实物商品 详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "售后单号", dataType = "Long", required = true, paramType = "path"),
    })
    public AfterSalesOrderDTO getSalesDetailById(@PathVariable(value = "id") Long id) {
        return orderFeign.getSalesDetailById(id);
    }


    /**
     * 订单-安装工续费订单-续费订单列表
     *
     * @param query
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.AFTER_SALE)
    @PostMapping("/order/station/renew/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询安装工续费订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "RenewOrderQuery", paramType = "body")
    })
    public PageVO<OrderRenewVO> getEngineerRenewOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize,
                                                          @RequestBody RenewOrderQuery query) {
        log.info("查询={}", JSON.toJSONString(query));
        //查询区域下的安装工id集合
        List<Integer> engineerIds = userFeign.getEngineerIdsByAreaIds(query.getAreas());

        if (CollectionUtil.isEmpty(engineerIds)) {
            return null;
        }
        query.setEngineerIds(engineerIds);
        return orderFeign.getOrderRenewList(query, pageNum, pageSize);
    }

    /**
     * 订单-经销商续费订单-续费订单列表
     *
     * @param query
     * @return
     */
    @StationQuery(value = StationQueryEnum.ListQuery, serviceType = PermissionTypeEnum.PRE_SALE)
    @PostMapping("/order/station/distributorRenew/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "根据条件查询经销商续费订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "RenewOrderQuery", paramType = "body")
    })
    public PageVO<OrderRenewVO> getDistributorRenewOrderInfo(@PathVariable(value = "pageNum") Integer pageNum,
                                                             @PathVariable(value = "pageSize") Integer pageSize,
                                                             @RequestBody RenewOrderQuery query) {
        log.info("查询={}", JSON.toJSONString(query));
        //查询区域下的经销商id集合
        List<Integer> distributorIds = userFeign.getDistributorIdsByAreaIds(query.getAreas());

        if (CollectionUtil.isEmpty(distributorIds)) {
            return null;
        }
        query.setDistributorIds(distributorIds);
        return orderFeign.getOrderRenewList(query, pageNum, pageSize);
    }

    /**
     * 订单-续费订单-续费订单列表-详情
     */
    @StationQuery(StationQueryEnum.InfoQuery)
    @GetMapping(value = "/order/station/renew/{id}/detail")
    @ApiOperation(value = "订单管理-续费列表-详情")
    @ApiImplicitParam(name = "id", value = "续费id", dataType = "String", paramType = "path", required = true)
    public Object getOrderRenewDetail(@PathVariable String id) {

        return orderFeign.getOrderRenewDetail(id);
    }

    /**
     * 订单-订单列表-产品类目筛选列表
     */
    @GetMapping("/product/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "产品类目筛选列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "前台类目还是后台类目：1-后台类目；2-前台类目；", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "产品类目等级：1-一级；2-二级；3-三级；", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductCategoryDTO> page(@RequestParam(required = false) Integer type,
                                           @RequestParam(required = false) Integer level,
                                           @PathVariable Integer pageNum, @PathVariable Integer pageSize) {

        return productFeign.pageProductCategory(null, type, null, null, level, null, null, null, pageNum, pageSize);
    }


}
