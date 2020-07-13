package com.yimao.cloud.order.controller;

import com.alibaba.fastjson.JSON;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.po.OrderRenew;
import com.yimao.cloud.order.service.OrderRenewService;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.OrderRenewDTO;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.station.RenewStatisticsDTO;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.station.StationWaterDeviceQuery;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderRenewController")
public class OrderRenewController {

    @Resource
    private OrderRenewService orderRenewService;
    @Resource
    private OrderRenewMapper orderRenewMapper;
    @Resource
    private UserCache userCache;

    /**
     * 创建续费工单
     *
     * @param dto 续费工单
     */
    @PostMapping(value = "/order/renew", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody OrderRenewDTO dto) {
        OrderRenew renew = new OrderRenew(dto);
        orderRenewService.save(renew);
    }

    /**
     * 订单管理-续费列表-列表
     */
    @PostMapping(value = "/order/renew/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getOrderRenewList(@RequestBody RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderRenewService.getOrderRenewList(query, pageNum, pageSize);
    }

    /**
     * 订单管理-续费列表-详情
     */
    @GetMapping(value = "/order/renew/{id}/detail")
    public OrderRenewVO getOrderRenewInfo(@PathVariable String id) {
        return orderRenewService.getOrderRenewDetail(id);
    }

    /**
     * 订单管理-续费列表-重新提交支付凭证
     */
    @PatchMapping(value = "/order/renew/{id}/resubmit")
    public void resubmit(@PathVariable String id, @RequestParam Integer payType, @RequestParam String payCredential) {
        orderRenewService.resubmit(id, payType, payCredential);
    }

    /**
     * 财务管理-续费订单对账-列表
     */
    @PostMapping(value = "/order/renew/finance/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object orderRenewFinanceList(@RequestBody RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderRenewService.orderRenewFinanceList(query, pageNum, pageSize);
    }

    /**
     * 财务管理-续费订单对账-查看
     */
    @GetMapping(value = "/order/renew/finance/{id}/detail")
    public OrderRenewVO getOrderRenewFinanceDetail(@PathVariable String id) {
        return orderRenewService.getOrderRenewFinanceDetail(id);
    }

    /**
     * 财务管理-支付审核-续费支付待审核-列表
     */
    @GetMapping(value = "/order/renew/paycheck/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object orderRenewPayCheck(@RequestBody RenewOrderQuery query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        log.info("参数=" + JSON.toJSONString(query));
        return orderRenewService.orderRenewPayCheckList(query, pageNum, pageSize);
    }

    /**
     * 财务管理-支付审核-续费支付待审核-审核
     */
    @PatchMapping(value = "/order/renew/{id}/paycheck/single")
    public void orderRenewPayCheckSingle(@PathVariable String id, @RequestParam Boolean pass, @RequestParam(required = false) String reason) {
        String adminName = userCache.getCurrentAdminRealName();
        orderRenewService.orderRenewPayCheckSingle(id, pass, reason, adminName);
    }

    /**
     * 查询续费单信息（基本信息）
     */
    @GetMapping(value = "/order/renew/{id}")
    public Object getRenewOrder(@PathVariable String id) {
        OrderRenew renew = orderRenewService.getById(id);
        if (renew == null) {
            return null;
        }
        OrderRenewDTO dto = new OrderRenewDTO();
        renew.convert(dto);
        return dto;
    }

    /**
     * 翼猫APP-我的-水机续费-续费记录
     */
    @GetMapping(value = "/order/renew/sn")
    public Object renewRecord(@RequestParam String snCode, @RequestParam Integer distributorId) {
        return orderRenewMapper.selectRenewOrderRecordBySnCodeForApp(snCode, distributorId);
    }


    @GetMapping(value = "/order/renew/countfordistributor")
    public Map<String, Object> getRenewOrderListById(@RequestParam Integer distributorId) {
        Map<String, Object> map = new HashMap<>();
        List<RenewDTO> renewDTOList = orderRenewMapper.getRenewOrderListById(distributorId);
        Integer count = orderRenewMapper.getRenewOrderCount(distributorId);
        map.put("renewDTOList", renewDTOList);
        map.put("count", count);
        return map;
    }

    /***
     * 查询续费次数
     * @param deviceId
     * @return
     */
    @GetMapping(value = "/order/renew/currentrenewtimes")
    @ApiOperation(value = "查询续费次数", notes = "查询续费次数")
    @ApiImplicitParam(name = "deviceId", value = "查询续费次数", dataType = "Long", required = true, paramType = "query")
    public Integer getCurrentRenewTimes(@RequestParam Integer deviceId) {
        return orderRenewMapper.countRenewTimes(deviceId);
    }

    /**
     * 水机设备列表-更换设备-同步修改续费单上的SN码
     */
    @PatchMapping(value = "/order/renew/changeSn")
    public void renewRecord(@RequestParam Integer deviceId, @RequestParam String oldSn, @RequestParam String newSn) {
        orderRenewMapper.updateRenewOrderSn(deviceId, oldSn, newSn);
    }
    
    /**
     * 站务系统-统计-续费统计-查询每天续费数（图表）
     * @param waterDeviceQuery
     * @return
     */
    @PostMapping(value = "/order/renew/station/isRenewPicData")
	List<RenewStatisticsDTO> getIsRenewPicData(@RequestBody StationWaterDeviceQuery waterDeviceQuery){
    	return orderRenewMapper.getIsRenewPicData(waterDeviceQuery);
    }
    
    /****
     * 经销商app统计公司的续费额
     * @param query
     * @return
     */
    @PostMapping(value = "/order/renewSale/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getRenewSaleStats(@RequestBody SalesStatsQueryDTO query) {
		return orderRenewService.getRenewSaleStats(query);
    	
    }

    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.order.SalesStatsDTO
     * @description 代理商续费报表首页
     * @author Liu Yi
     * @date 2020/4/29 10:28
     */
    @PostMapping(value = "/order/renew/home/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    AgentSalesOverviewDTO getOrderSalesHomeReport(@RequestBody SalesStatsQueryDTO query) {
        return orderRenewService.getOrderSalesHomeReport(query);
    }


    /**
     * @param
     * @return com.yimao.cloud.pojo.dto.order.SalesStatsDTO
     * @description 代理商续费汇总报表
     * @author Liu Yi
     * @date 2020/4/29 10:28
     */
    @PostMapping(value = "/order/renew/trend/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<SalesStatsDTO> getOrderSalesTotalReport(@RequestBody SalesStatsQueryDTO query) {
        return orderRenewService.getWaterDeviceRenewSale(query);
    }

    /**
     * 净水设备续费订单分类名称
     * @return
     */
    @GetMapping(value = "/order/renew/type/name")
    public List<String> getRenewOrderTypeNames(){
        return orderRenewService.getRenewOrderTypeNames();
    }
}
