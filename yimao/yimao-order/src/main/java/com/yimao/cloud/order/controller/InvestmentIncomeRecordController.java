package com.yimao.cloud.order.controller;

import com.yimao.cloud.order.service.InvestmentIncomeRecordService;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.InvestmentIncomeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 招商收益
 *
 * @author Liu Yi
 * @date 2019/1/23.
 */
@RestController
@Slf4j
@Api(tags = "InvestmentIncomeRecordController")
public class InvestmentIncomeRecordController {

    @Resource
    private InvestmentIncomeRecordService investmentIncomeRecordService;

    /**
     * @return
     * @description 招商收益分配
     * @author Liu Yi
     */
    @PostMapping(value = "/order/investemntIncome/investmentAllot")
    @ApiOperation(value = "收益分配", notes = "收益分配")
    public Object serviceAllot(@RequestBody DistributorOrderDTO dto) {
        investmentIncomeRecordService.investmentIncomeAllot(dto);

        return ResponseEntity.noContent().build();
    }

    /**
     * @param orderId
     * @param userId
     * @param distributorAccount
     * @param distributorName
     * @param province
     * @param city
     * @param region
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询招商收益明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/investmentIncome/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询招商收益明细列表", notes = "查询招商收益明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型 0-注册、1-升级、2-续费", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "经销商id(用户e家号)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorLevel", value = "经销商类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账户", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorName", value = "经销商名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "region", value = "区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "refereeName", value = "经销商名称(推荐人)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "refereeAccount", value = "经销商账户(推荐人)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "settlementMonth", value = "结算月份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public Object pageQueryInvestmentIncome(@RequestParam(value = "orderId", required = false) Long orderId,
                                            @RequestParam(value = "orderType", required = false) Integer orderType,
                                            @RequestParam(value = "userId", required = false) Integer userId,
                                            @RequestParam(value = "distributorId", required = false) Integer distributorId,
                                            @RequestParam(value = "distributorLevel", required = false) Integer distributorLevel,
                                            @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                            @RequestParam(value = "distributorName", required = false) String distributorName,
                                            @RequestParam(value = "province", required = false) String province,
                                            @RequestParam(value = "city", required = false) String city,
                                            @RequestParam(value = "region", required = false) String region,
                                            @RequestParam(value = "refereeName", required = false) String refereeName,
                                            @RequestParam(value = "refereeAccount", required = false) String refereeAccount,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            @RequestParam(value = "settlementMonth", required = false) String settlementMonth,
                                            @PathVariable("pageNum") Integer pageNum,
                                            @PathVariable("pageSize") Integer pageSize) {
        InvestmentIncomeQueryDTO incomeQueryDTO = new InvestmentIncomeQueryDTO();
        incomeQueryDTO.setOrderId(orderId);
        incomeQueryDTO.setDistributorOrderType(orderType);
        incomeQueryDTO.setUserId(userId);
        incomeQueryDTO.setDistributorId(distributorId);
        incomeQueryDTO.setDistributorLevel(distributorLevel);
        incomeQueryDTO.setDistributorAccount(distributorAccount);
        incomeQueryDTO.setDistributorName(distributorName);
        incomeQueryDTO.setProvince(province);
        incomeQueryDTO.setCity(city);
        incomeQueryDTO.setRegion(region);
        incomeQueryDTO.setRefereeName(refereeName);
        incomeQueryDTO.setRefereeAccount(refereeAccount);
        incomeQueryDTO.setStartTime(startTime);
        incomeQueryDTO.setEndTime(endTime);
        incomeQueryDTO.setSettlementMonth(settlementMonth);

        PageVO<InvestmentIncomeVO> page = investmentIncomeRecordService.page(incomeQueryDTO, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询招商收益
     * @author Liu Yi
     */
    @GetMapping(value = "/order/investmentIncome/{id}")
    @ApiOperation(value = "根据id查询招商收益", notes = "根据id查询招商收益")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path")
    public Object getInvestmentIncomeRecordById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO incomeRecordDTO = investmentIncomeRecordService.getInvestmentIncomeById(id);
        return ResponseEntity.ok(incomeRecordDTO);
    }
}
