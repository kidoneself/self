package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.ServiceIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ServiceIncomeRecordPartDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务收益
 *
 * @author Liu Yi
 * @date 2019/1/23.
 */
@RestController
@Slf4j
@Api(tags = "ServiceIncomeRecordController")
public class ServiceIncomeRecordController {

    @Resource
    private ServiceIncomeRecordService serviceIncomeRecordService;

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
    @ApiOperation(value = "查询产品服务收益明细列表", notes = "查询产品服务收益明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCompanyId", value = "产品公司ID", dataType = "Long", paramType = "query"),
            //@ApiImplicitParam(name = "mainOrderId", value = "主订单号", dataType = "Long",paramType = "query"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ticketNo", value = "体检卡号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productCategoryId", value = "产品类目id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "incomeType", value = "收益类型：1-产品收益，2-续费收益，3-服务收益", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userType", value = "用户类型1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账户", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorName", value = "产品类目id3级", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "settlementTime", value = "结算时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "订单完成结束时间", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object pageQueryProductIncome(@RequestParam(value = "productCompanyId", required = false) Integer productCompanyId,
                                         //@RequestParam(value = "mainOrderId",required = false) Integer mainOrderId,
                                         @RequestParam(value = "orderId", required = false) String orderId,
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
                                         @PathVariable("pageNum") Integer pageNum,
                                         @PathVariable("pageSize") Integer pageSize,
                                         @RequestParam(value = "ticketNo", required = false) String ticketNo) {

        ProductIncomeQueryDTO incomeQueryDTO = new ProductIncomeQueryDTO();
        incomeQueryDTO.setProductCompanyId(productCompanyId);
        incomeQueryDTO.setTicketNo(ticketNo);
        //incomeQueryDTO.setMainOrderId(mainOrderId);
        incomeQueryDTO.setOrderId(orderId);
        incomeQueryDTO.setProductCategoryId(productCategoryId);
        incomeQueryDTO.setUserId(userId);
        incomeQueryDTO.setIncomeType(incomeType);
        incomeQueryDTO.setUserType(userType);
        incomeQueryDTO.setDistributorAccount(distributorAccount);
        incomeQueryDTO.setDistributorName(distributorName);
        incomeQueryDTO.setDistributorProvince(province);
        incomeQueryDTO.setDistributorCity(city);
        incomeQueryDTO.setDistributorRegion(region);
        incomeQueryDTO.setSettlementTime(settlementTime);
        incomeQueryDTO.setStartTime(startTime);
        incomeQueryDTO.setEndTime(endTime);
        incomeQueryDTO.setProductMode(productMode);
        PageVO<ProductIncomeVO> page = serviceIncomeRecordService.pageQueryServiceIncome(incomeQueryDTO, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/serviceIncome/{id}")
    @ApiOperation(value = "根据id查询服务收益记录", notes = "根据id查询服务收益记录")
    @ApiImplicitParam(name = "id", value = "收益主表id", required = true, dataType = "Long", paramType = "path")
    public Object getServiceIncomeById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO dto = serviceIncomeRecordService.getServiceIncomeById(id);

        return ResponseEntity.ok(dto);
    }

    /**
     * @param orderId
     * @return
     * @description 根据订单id查询服务收益记录
     * @author Liu Yi
     */
    @GetMapping(value = "/order/serviceIncome")
    @ApiOperation(value = "根据订单id查询收益记录", notes = "根据订单id查询收益记录")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "Long", paramType = "query")
    public Object geServiceIncomeRecordPartList(@RequestParam("orderId") Long orderId) {
        List<ServiceIncomeRecordPartDTO> list = serviceIncomeRecordService.getServiceIncomeRecordPartList(orderId);

        return ResponseEntity.ok(list);
    }

    /**
     * 产品服务收益导出
     *
     * @author hhf
     * @date 2019/5/14
     */
//    @PostMapping(value = "/order/service/income/export")
//    @ApiOperation(value = "产品服务收益导出", notes = "产品服务收益导出")
//    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "ProductIncomeQueryDTO", paramType = "body")
//    public List<IncomeExportDTO> serviceIncomeExport(@RequestBody ProductIncomeQueryDTO query) {
//        return serviceIncomeRecordService.serviceIncomeExport(query);
//    }

    /**
     * 描述：分配产品销售收益
     *
     */
    @PostMapping(value = "/allot/service/income")
    @ApiOperation(value = "分配产品销售收益")
    public void allotServiceIncome(@RequestParam String ticketNo, @RequestParam Integer stationId, @RequestParam String deviceId) {
        serviceIncomeRecordService.serviceAllot(ticketNo,stationId,deviceId);
    }
}
