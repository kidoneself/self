package com.yimao.cloud.system.controller.income;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ServiceIncomeRecordPartDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
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
            @ApiImplicitParam(name = "productCategoryId", value = "产品类目id级", dataType = "Long", paramType = "query"),
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
            @ApiImplicitParam(name = "productMode", value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<ProductIncomeVO>> pageQueryProductIncome(@RequestParam(value = "productCompanyId", required = false) Long productCompanyId,
                                                                          //@RequestParam(value = "mainOrderId",required = false) Long mainOrderId,
                                                                          @RequestParam(value = "orderId", required = false) Long orderId,
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


        PageVO<ProductIncomeVO> page = orderFeign.pageQueryServiceIncome(productCompanyId, orderId, productCategoryId, userId, incomeType,
                userType, distributorAccount, distributorName, province, city, region, settlementTime, startTime, endTime, productMode, pageNum, pageSize, ticketNo);

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
    public ResponseEntity<IncomeRecordResultDTO> getServiceIncomeById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO dto = orderFeign.getServiceIncomeById(id);
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
        List<ServiceIncomeRecordPartDTO> list = orderFeign.getServiceIncomeRecordPartList(orderId);
        return ResponseEntity.ok(list);
    }


    /**
     * 产品服务收益导出
     *
     * @author hhf
     * @date 2019/5/14
     */
    @PostMapping(value = "/order/service/income/export")
    @ApiOperation(value = "产品服务收益导出", notes = "产品服务收益导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "ProductIncomeQueryDTO", paramType = "body")
    public Object serviceIncomeExport(@RequestBody ProductIncomeQueryDTO query, HttpServletResponse response) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/service/income/export";
        ExportRecordDTO record = exportRecordService.save(url, "产品服务收益导出");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());


//        long l1 = System.currentTimeMillis();
//        List<IncomeExportDTO> list = orderFeign.serviceIncomeExport(query);
//        long l2 = System.currentTimeMillis();
//        log.info("serviceIncomeExport---远程调用order服务执行耗时：" + (l2 - l1) + "毫秒。");
//
//        String[] titles = {"主订单号", "订单号", "下单时间", "体检卡号", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
//                "订单金额", "可分配金额", "流水号", "支付时间", "支付方式", "使用时间", "结算月份", "体检人姓名", "体检人手机号", "体检服务站省", "体检服务站市",
//                "体检服务站区", "体检服务站公司名称", "服务站承包人（服务收益）"};
//        String[] beanProperties = {"mainOrderId", "orderId", "createTime", "ticketNo", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount",
//                "orderFee", "settlementFee", "tradeNo", "payTime", "payType", "examDate", "settlementMonth",
//                "medicalName", "medicalPhone", "stationProvince", "stationCity", "stationRegion", "stationCompanyName", "stationContractorService"};
//        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "产品服务收益导出";
//        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
//        long l3 = System.currentTimeMillis();
//        log.info("serviceIncomeExport---封装Excel文件执行耗时：" + (l3 - l2) + "毫秒。");
    }


    /**
     * 描述：分配服务收益
     */
    @PostMapping(value = "/allot/service/income")
    @ApiOperation(value = "分配服务收益")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ticketNo", value = "子订单号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "stationId", value = "子订单号", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "deviceId", value = "子订单号", required = true, dataType = "String", paramType = "query")
    })
    public void allotSellIncome(@RequestParam String ticketNo, @RequestParam Integer stationId, @RequestParam String deviceId) {
        orderFeign.allotServiceIncome(ticketNo, stationId, deviceId);
    }
}
