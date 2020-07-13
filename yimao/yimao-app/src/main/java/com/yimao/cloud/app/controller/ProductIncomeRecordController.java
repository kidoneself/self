package com.yimao.cloud.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.pojo.dto.order.IncomeStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.IncomeStatsResultDTO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeGrandTotalVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品下单和续费收益
 *
 * @author Liu Yi
 * @date 2019/1/23.
 */
@RestController
@Api(tags = "ProductIncomeRecordController")
public class ProductIncomeRecordController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * @return
     * @description 获取经销商收益统计
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/grandTotal")
    @ApiOperation(value = "获取经销商收益统计", notes = "获取经销商收益统计")
    @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    public Object productIncomeGrandTotalforApp(@RequestParam(value = "incomeType", defaultValue = "1") Integer incomeType) {
        ProductIncomeGrandTotalVO vo = orderFeign.productIncomeGrandTotalforApp(incomeType);

        return ResponseEntity.ok(vo);
    }

    /**
     * @param dateTime
     * @param type
     * @description app经销商收益概况(按日和按月)
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/reportOverview")
    @ApiOperation(value = "app经销商收益概况(按日和按月)", notes = "app经销商收益概况(按日和按月)")

    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型：1-按日 2-按月", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    })
    public Object productIncomeReportOverviewforApp(@RequestParam("dateTime") Date dateTime,
                                                    @RequestParam("type") Integer type,
                                                    @RequestParam(value = "incomeType", defaultValue = "1") Integer incomeType) {
        Map<String, Object> map = orderFeign.productIncomeReportOverviewforApp(dateTime, type, incomeType);
        return ResponseEntity.ok(map);
    }

    /**
     * @description app经销商收益金额明细列表(按日和按月)
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/reportOverviewList/{pageNum}/{pageSize}")
    @ApiOperation(value = "app经销商收益金额明细列表(按日和按月)", notes = "app经销商收益金额明细列表(按日和按月)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型：1-按日 2-按月", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object productIncomeReportOverviewListforApp(@RequestParam("dateTime") Date dateTime,
                                                        @RequestParam("type") Integer type,
                                                        @RequestParam(value = "incomeType", defaultValue = "1") Integer incomeType,
                                                        @PathVariable("pageNum") Integer pageNum,
                                                        @PathVariable("pageSize") Integer pageSize) {
        Map<String, Object> map = orderFeign.productIncomeReportOverviewListforApp(dateTime, type, incomeType, pageNum, pageSize);
        return ResponseEntity.ok(map);
    }

    /**
     * @description app经销商收益金额明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "app经销商收益金额明细列表", notes = "app经销商收益金额明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminal", value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；5-翼猫总部业务系统；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "一级分类id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "queryType", value = "查询类型 0-全部 1-主账户 2-子账户", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态:1-未完成 2-已完成 3-退单", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "timeType", value = "下单时间类型：1、昨日；2、7日内；3、30天内；4、3个月内；5、今年；6、上一年", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginDate", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object productIncomeListForApp(@RequestParam(value = "terminal", required = false) Integer terminal,
                                          @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "distributorId", required = false) Integer distributorId,
                                          @RequestParam(value = "queryType", defaultValue = "0") Integer queryType,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "timeType", required = false, defaultValue = "0") Integer timeType,
                                          @RequestParam(value = "incomeType", required = false, defaultValue = "1") Integer incomeType,
                                          @RequestParam(value = "beginDate", required = false) Date beginDate,
                                          @RequestParam(value = "endDate", required = false) Date endDate,
                                          @PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize) {

        Map<String, Object> mapList = orderFeign.productIncomeListForApp(terminal, categoryId, distributorId, queryType, status, timeType, incomeType, beginDate, endDate, pageNum, pageSize);
        return ResponseEntity.ok(mapList);
    }

    /**
     * @description app经销商收益收益详情
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/{id}/detail")
    @ApiOperation(value = "app经销商收益收益详情", notes = "app经销商收益收益详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query"),
    })
    public Object productIncomeDetailForApp(@PathVariable("id") Integer id, @RequestParam(value = "incomeType", defaultValue = "1") Integer incomeType) {
        Map<String, Object> map = orderFeign.productIncomeDetailForApp(id, incomeType);
        return ResponseEntity.ok(map);
    }

    /****
     * 获取经销商收益统计信息(产品收益和续费收益)
     * @param id
     * @return
     */
    @PostMapping(value = "/order/productIncome/statsInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "app经销商收益统计信息", notes = "app经销商收益统计信息")
    @ApiImplicitParam(name = "dto", value = "app经销商收益统计请求参数", required = true, dataType = "IncomeStatsQueryDTO", paramType = "body")
    public Object productIncomeStats(@RequestBody IncomeStatsQueryDTO dto) {
        List<IncomeStatsResultDTO> result = orderFeign.productIncomeStats(dto);
        return ResponseEntity.ok(JSONObject.toJSONString(result));
    }
}
