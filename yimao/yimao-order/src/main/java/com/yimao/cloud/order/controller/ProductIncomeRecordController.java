package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.mapper.ProductIncomeRecordPartMapper;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.ServiceIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeGrandTotalVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ProductIncomeRecordService productIncomeRecordService;
    @Resource
    private ServiceIncomeRecordService serviceIncomeRecordService;
    @Resource
    private ProductIncomeRecordPartMapper productIncomeRecordPartMapper;

    /**
     * 描述：分配产品销售收益
     *
     * @param orderId 子订单号
     */
    @PostMapping(value = "/allot/sell/income")
    @ApiOperation(value = "分配产品销售收益")
    @ApiImplicitParam(name = "orderId", value = "子订单号", required = true, dataType = "Long", paramType = "query")
    public void allotSellIncome(@RequestParam Long orderId) {
        productIncomeRecordService.allotSellIncome(orderId);
    }

    /**
     * 描述：设置收益结算状态
     *
     * @param id
     */
    @PatchMapping(value = "/order/income/settlement/{id}/status")
    @ApiOperation(value = "设置收益结算状态", notes = "暂停结算/恢复结算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "状态：1-可结算 4-暂停结算", required = true, dataType = "Long", paramType = "query")
    })
    public Object setSettlementIncomeStatus(@PathVariable(value = "id") Integer id, @RequestParam(value = "status") Integer status) {
        productIncomeRecordService.setSettlementIncomeStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询产品收益明细列表
     *
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询产品收益明细列表", notes = "查询产品收益明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCompanyId", value = "产品公司ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productCategoryId", value = "产品类目id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益，2-续费收益，3-服务收益", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userType", value = "用户类型:1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账户", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorName", value = "经销商姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorProvince", value = "省", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorCity", value = "市", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorRegion", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "settlementTime", value = "结算时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "settlementStatus", value = "结算状态", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "engineerSettlementTime", value = "安装工结算时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "订单完成结束时间", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object pageQueryProductIncome(@RequestParam(value = "productCompanyId", required = false) Integer productCompanyId,
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
                                         @PathVariable("pageNum") Integer pageNum,
                                         @PathVariable("pageSize") Integer pageSize) {

        ProductIncomeQueryDTO incomeQueryDTO = new ProductIncomeQueryDTO();
        incomeQueryDTO.setProductCompanyId(productCompanyId);
        incomeQueryDTO.setOrderId(orderId);
        incomeQueryDTO.setProductCategoryId(productCategoryId);
        incomeQueryDTO.setUserId(userId);
        incomeQueryDTO.setIncomeType(incomeType);
        incomeQueryDTO.setUserType(userType);
        incomeQueryDTO.setDistributorAccount(distributorAccount);
        incomeQueryDTO.setDistributorName(distributorName);
        incomeQueryDTO.setDistributorProvince(distributorProvince);
        incomeQueryDTO.setDistributorCity(distributorCity);
        incomeQueryDTO.setDistributorRegion(distributorRegion);
        incomeQueryDTO.setSettlementTime(settlementTime);
        incomeQueryDTO.setSettlementStatus(settlementStatus);
        incomeQueryDTO.setEngineerSettlementTime(engineerSettlementTime);
        incomeQueryDTO.setStartTime(startTime);
        incomeQueryDTO.setEndTime(endTime);
        incomeQueryDTO.setProductMode(productMode);
        incomeQueryDTO.setPayStartTime(payStartTime);
        incomeQueryDTO.setPayEndTime(payEndTime);

        PageVO<ProductIncomeVO> page;
        //如果是服务收益
        if (incomeQueryDTO.getIncomeType() != null && incomeQueryDTO.getIncomeType() == 3) {
            page = serviceIncomeRecordService.pageQueryServiceIncome(incomeQueryDTO, pageNum, pageSize);
        } else {
            //产品收益，续费收益
            page = productIncomeRecordService.pageQueryProductIncome(incomeQueryDTO, pageNum, pageSize);
        }

        return ResponseEntity.ok(page);
    }

    /**
     * @param orderId
     * @return
     * @description 根据订单id查询收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome")
    @ApiOperation(value = "根据订单id查询收益记录", notes = "根据订单id查询收益记录")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "query")
    public Object getProductIncomeRecordPartList(@RequestParam("orderId") Integer orderId) {
        List<ProductIncomeRecordPartDTO> list = productIncomeRecordService.getProductIncomeRecordPartList(orderId);

        return ResponseEntity.ok(list);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/{id}")
    @ApiOperation(value = "根据id查询收益记录", notes = "根据id查询收益记录")
    @ApiImplicitParam(name = "id", value = "收益主表id", required = true, dataType = "Long", paramType = "path")
    public Object getProductIncomeById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO dto = productIncomeRecordService.getProductIncomeById(id);

        return ResponseEntity.ok(dto);
    }

    /**
     * @param orderId
     * @return
     * @description 变更订单状态为已完成可结算
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/toComplete")
    @ApiOperation(value = "变更订单状态为已完成可结算", notes = "变更订单状态为已完成可结算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderCompleteTime", required = true, value = "订单完成时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query")
    })
    public Object changeProductIncomeRecordToComplete(@RequestParam("orderId") Long orderId, @RequestParam("orderCompleteTime") Date orderCompleteTime) {
        productIncomeRecordService.changeProductIncomeRecordToComplete(orderId, orderCompleteTime);

        return ResponseEntity.noContent();
    }


    /**
     * @param orderId
     * @return
     * @description 退单收益变更
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/refundOrder")
    @ApiOperation(value = "退单收益变更", notes = "退单收益变更")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "query")
    public Object refundOrder(@RequestParam("orderId") Long orderId) {
        productIncomeRecordService.refundOrder(orderId);

        return ResponseEntity.noContent();
    }

    /**
     * @param orderId
     * @return
     * @description 退货收益变更
     * @author Liu Yi
     */
    @PutMapping(value = "/order/productIncome/refundGoods")
    @ApiOperation(value = "退货收益变更", notes = "退货收益变更")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "num", value = "退货数量", required = true, dataType = "Long", paramType = "query")
    })
    public Object refundGoods(@RequestParam("orderId") Long orderId, @RequestParam("num") Integer num) {
        productIncomeRecordService.refundGoods(orderId, num);
        return ResponseEntity.noContent();
    }

    /**
     * 产品收益明细导出
     *
     * @author hhf
     * @date 2019/5/9
     */
//    @PostMapping(value = "/order/product/income/export")
//    @ApiOperation(value = "产品收益明细导出", notes = "产品收益明细导出")
//    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "ProductIncomeQueryDTO", paramType = "body")
//    public List<IncomeExportDTO> productIncomeExport(@RequestBody ProductIncomeQueryDTO query,
//                                                     @RequestParam(value = "pageNum") Integer pageNum,
//                                                     @RequestParam(value = "pageSize") Integer pageSize) {
//        return productIncomeRecordService.productIncomeExport(query,pageNum,pageSize);
//    }

    /**
     * 待导出收益明细总数
     */
//    @PostMapping(value = "/order/product/income/export/count")
//    public Integer productIncomeExportCount(@RequestBody ProductIncomeQueryDTO query) {
//        return productIncomeRecordService.productIncomeExportCount(query);
//    }

    /**
     * @description 获取经销商收益统计
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/grandTotal")
    @ApiOperation(value = "获取经销商收益统计", notes = "获取经销商收益统计")
    @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    public Object productIncomeGrandTotalforApp(@RequestParam("incomeType") Integer incomeType) {
        ProductIncomeGrandTotalVO vo = productIncomeRecordService.productIncomeGrandTotalforApp(incomeType);
        return ResponseEntity.ok(vo);
    }

    /**
     * @param dateTime
     * @param type
     * @description app经销商收益概况
     * @author Liu Yi
     */
    @GetMapping(value = "/order/productIncome/distributorApp/reportOverview")
    @ApiOperation(value = "app经销商收益概况", notes = "app经销商收益概况")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateTime", value = "开始时间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型：1-按日 2-按月", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益 2-续费收益", required = true, dataType = "Long", paramType = "query")
    })
    public Object productIncomeReportOverviewforApp(@RequestParam("dateTime") Date dateTime,
                                                    @RequestParam("type") Integer type,
                                                    @RequestParam("incomeType") Integer incomeType) {
        Map<String, Object> map = productIncomeRecordService.productIncomeReportOverviewforApp(dateTime, type, incomeType);
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
                                                        @RequestParam("incomeType") Integer incomeType,
                                                        @PathVariable("pageNum") Integer pageNum,
                                                        @PathVariable("pageSize") Integer pageSize) {
        Map<String, Object> map = productIncomeRecordService.productIncomeReportOverviewListforApp(dateTime, type, incomeType, pageNum, pageSize);
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
                                          @RequestParam(value = "queryType", required = false) Integer queryType,
                                          @RequestParam(value = "status", required = false) Integer status,
                                          @RequestParam(value = "timeType", required = false) Integer timeType,
                                          @RequestParam(value = "incomeType", required = false) Integer incomeType,
                                          @RequestParam(value = "beginDate", required = false) Date beginDate,
                                          @RequestParam(value = "endDate", required = false) Date endDate,
                                          @PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize) {
        return ResponseEntity.ok(productIncomeRecordService.productIncomeListForApp(terminal, categoryId, distributorId, queryType, status, timeType, incomeType, beginDate, endDate, pageNum, pageSize));
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
    public Object productIncomeDetailForApp(@PathVariable("id") Integer id, @RequestParam("incomeType") Integer incomeType) {
        Map<String, Object> map = productIncomeRecordService.productIncomeDetailForApp(id, incomeType);
        return ResponseEntity.ok(map);
    }


    @RequestMapping(value = "/order/product/income/{id}", method = RequestMethod.GET)
    public List<ProductIncomeRecordDTO> getProductIncomeRecord(@PathVariable(value = "id") Integer id) {
        return productIncomeRecordService.getProductIncomeRecord(id);
    }

    @RequestMapping(value = "/product/sale/income/{id}", method = RequestMethod.GET)
    public List<SalesProductDTO> getSaleProductListById(@PathVariable(value = "id") Integer id) {
        return productIncomeRecordService.getSaleProductListById(id);
    }

    /**
     * 查询用户是否下过订单
     */
    @GetMapping(value = "/order/productIncome/existsByUserId")
    public Object existsWithUserId(@RequestParam Integer userId) {
        return productIncomeRecordPartMapper.existsIncomeWithUserId(userId);
    }

}
