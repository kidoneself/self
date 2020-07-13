package com.yimao.cloud.system.controller.income;

import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.InvestmentIncomeVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

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
     * @description 查询产品服务收益明细列表
     * @author Liu Yi
     */
    @GetMapping(value = "/order/investmentIncome/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询产品服务收益明细列表", notes = "查询产品服务收益明细列表")
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
    public ResponseEntity<PageVO<InvestmentIncomeVO>> pageQueryInvestmentIncome(@RequestParam(value = "orderId", required = false) Long orderId,
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

        PageVO<InvestmentIncomeVO> page = orderFeign.pageQueryInvestmentIncome(orderId, orderType, userId,distributorId, distributorLevel, distributorAccount, distributorName, province, city, region, refereeName, refereeAccount, startTime, endTime,settlementMonth, pageNum, pageSize);

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
    public ResponseEntity<IncomeRecordResultDTO> getInvestmentIncomeRecordById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO incomeRecordDTO = orderFeign.getInvestmentIncomeRecordById(id);
        return ResponseEntity.ok(incomeRecordDTO);
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
     * @return
     * @description 导出经销商订单收益明细列表
     * @author Liu Yi
     */
    @PostMapping(value = "/order/investmentIncome/export")
    @ApiOperation(value = "导出经销商订单收益明细列表", notes = "导出经销商订单收益明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型 0-注册、1-升级、2-续费", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "经销商id(用户e家号)", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorId", value = "经销商id", dataType = "Long", paramType = "query"),
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
            @ApiImplicitParam(name = "settlementMonth", value = "结算月份", dataType = "String", paramType = "query")
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
                                          @RequestParam(value = "settlementMonth", required = false) String settlementMonth) {
      /*  List<InvestmentIncomeExportDTO> list = orderFeign.investmentIncomeExport(orderId, orderType, userId,distributorId, distributorLevel, distributorAccount, distributorName, province, city, region, refereeName, refereeAccount, startTime, endTime,settlementMonth);
        if (CollectionUtil.isEmpty(list)) {
            throw new NotFoundException("没有数据可以导出");
        }
        //导出
        String header = "招商收益明细";
        String[] beanProperties =  new String[]{"orderId", "distributorOrderTypeStr", "distributorTypeStr", "realPayment", "receivableMoney", "moreMoney", "paySubject",
                "payTypeStr", "payTime", "trade", "orderCompletionTimeStr", "settlementMonth", "destDistributorTypeStr", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                "refereeName", "refereeAccount", "refereeDistrict"
                , "refereeStationCompany", "refereeIncome", "wisdomAssistantIncome", "refereeStationCompanyIncome", "yiMaoHQIncome"};
        String[] titles = new String[]{"订单号", "订单类型", "经销商类型", "实付款", "应收款", "多收款", "付款主体",
                "支付方式", "支付时间", "流水号", "订单完成时间", "结算月份", "升级后经销商类型", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "推荐人经销商姓名", "推荐人经销商账户", "推荐人归属区域"
                , "推荐人服务站公司", "推荐人收益", "智慧助理收益", "区县级公司（推荐人）收益", "翼猫总部收益"};
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_INVESTMENTINCOME_URL, "招商收益明细");
        InvestmentIncomeQueryDTO query = new InvestmentIncomeQueryDTO();
        query.setOrderId(orderId);
        query.setDistributorOrderType(orderType);
        query.setUserId(userId);
        query.setDistributorId(distributorId);
        query.setDistributorLevel(distributorLevel);
        query.setDistributorAccount(distributorAccount);
        query.setDistributorName(distributorName);
        query.setProvince(province);
        query.setCity(city);
        query.setRegion(region);
        query.setRefereeName(refereeName);
        query.setRefereeAccount(refereeAccount);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setSettlementMonth(settlementMonth);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());
    }

}
