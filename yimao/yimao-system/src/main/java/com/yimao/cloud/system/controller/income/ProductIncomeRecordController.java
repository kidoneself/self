package com.yimao.cloud.system.controller.income;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordPartDTO;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 产品下单和续费收益
 *
 * @author Liu Yi
 * @date 2019/1/23.
 */
@RestController
@Slf4j
@Api(tags = "ProductIncomeRecordController")
public class ProductIncomeRecordController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

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
            @ApiImplicitParam(name = "settlementStatus", value = "结算状态 0-不可结算（订单未完成）；1-可结算（订单已完成）；2-已结算；3-已失效(退单) 4-暂停结算", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "engineerSettlementTime", value = "安装工结算月份", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "订单完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "订单完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付完成开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付完成结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "productMode", value = "商品类型（大类）:1实物商品，2电子卡券，3租赁商品", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<ProductIncomeVO>> pageQueryProductIncome(@RequestParam(value = "productCompanyId", required = false) Long productCompanyId,
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

        PageVO<ProductIncomeVO> page = orderFeign.pageQueryProductIncome(productCompanyId, orderId, productCategoryId, userId, incomeType, userType, distributorAccount,
                distributorName, distributorProvince, distributorCity, distributorRegion, settlementTime, settlementStatus, engineerSettlementTime, startTime, endTime, payStartTime, payEndTime, productMode, pageNum, pageSize);

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
        List<ProductIncomeRecordPartDTO> list = orderFeign.getProductIncomeRecordPartList(orderId);

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
    public ResponseEntity<IncomeRecordResultDTO> getProductIncomeById(@PathVariable("id") Integer id) {
        IncomeRecordResultDTO dto = orderFeign.getProductIncomeById(id);

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
        orderFeign.changeProductIncomeRecordToComplete(orderId, orderCompleteTime);

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
        orderFeign.refundOrder(orderId);

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
        orderFeign.refundGoods(orderId, num);

        return ResponseEntity.noContent();
    }

    /**
     * 产品收益明细导出
     *
     * @author hhf
     * @date 2019/5/9
     */
    @PostMapping(value = "/order/product/income/export")
    @ApiOperation(value = "产品收益明细导出", notes = "产品收益明细导出")
    @ApiImplicitParam(name = "query", value = "查询信息", required = true, dataType = "ProductIncomeQueryDTO", paramType = "body")
    public Object productIncomeExport(@RequestBody ProductIncomeQueryDTO query) {
        Integer productCompanyId = query.getProductCompanyId();
        Integer incomeType = query.getIncomeType();
        String title = "";
        if (Objects.isNull(productCompanyId)) {
            throw new YimaoException("请指明产品公司");
        }
        if (incomeType == null) {
            throw new YimaoException("请指明收益类型");
        }

        if (productCompanyId == 10000) {
            //产品公司为翼猫科技发展（上海）有限公司
            if (incomeType == 1) {
                title = "产品收益明细导出";
            } else if (incomeType == 2) {
                title = "续费收益明细导出";
            } else {
                throw new YimaoException("没有对应的导出模板");
            }
        } else if (productCompanyId == 20000 || productCompanyId == 30000) {
            if (incomeType == 1) {
                title = "产品收益明细导出";
            } else {
                throw new YimaoException("没有对应的导出模板");
            }
        } else if (productCompanyId == 50000) {
            //产品公司为上海翼猫智能科技有限公司
            if (incomeType == 1) {
                title = "产品收益明细导出";
            } else {
                throw new YimaoException("没有对应的导出模板");
            }
        }

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/product/income/export";
        ExportRecordDTO record = exportRecordService.save(url, title);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());


//        //1-每次导出条数
//        Integer pageSize = 5000;
//        //2-待导出数量
//        Integer exportCount = orderFeign.productIncomeExportCount(query);
//        if (exportCount < 1) {
//            throw new NotFoundException("没有数据可以导出");
//        }
//        //3-导出次数
//        Integer count = exportCount % pageSize > 0 ? exportCount / pageSize + 1 : exportCount / pageSize;
//        List<IncomeExportDTO> incomeExportList = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//            incomeExportList.addAll(orderFeign.productIncomeExport(query, i, pageSize));
//        }
//
//        Integer productCompanyId = query.getProductCompanyId();
//        Integer incomeType = query.getIncomeType();
//        if (Objects.isNull(productCompanyId)) {
//            throw new YimaoException("请指明产品公司");
//        }
//        if (incomeType == null) {
//            throw new YimaoException("请指明收益类型");
//        }
//        boolean boo = true;
//        if (productCompanyId == 10000) {
//            //产品公司为翼猫科技发展（上海）有限公司
//            if (incomeType == 1) {
//                //收益类型-产品收益  商品类型-租赁商品
//                String[] titles = {"主订单号", "订单号", "工单号", "订单来源", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "计费方式", "产品数量",
//                        "订单金额", "可分配金额", "流水号", "支付时间", "支付方式", "订单完成时间", "结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区",
//                        "是否有子账号", "子账号ID", "会员用户ID", "会员用户是否享受收益", "安装工姓名", "安装工省", "安装工所在服务站公司名称", "经销商收益", "会员用户收益", "安装服务人员收益", "产品公司收益"};
//                String[] beanProperties = {"mainOrderId", "orderId", "workOrderId", "orderSource", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "costName", "productCount",
//                        "orderFee", "settlementFee", "tradeNo", "payTime", "payType", "orderCompleteTime", "settlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
//                        "hasSubAccount", "subAccountId", "saleId", "userSaleFlag", "engineerName", "engineerProvince", "engineerStationName", "distributorIncome", "saleIncome", "regionInstallerIncome", "productCompanyIncome"};
//                String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "产品收益明细导出";
//                boo = ExcelUtil.exportSXSSF(incomeExportList, header, titles.length - 1, titles, beanProperties, response);
//            } else if (incomeType == 2) {
//                String[] titles = {"续费单号", "工单号", "设备SN码", "设备型号", "续费时间", "支付方式", "支付时间", "流水号",
//                        "计费模板名称", "计费类型", "支付金额",
//                        "续费端", "客户姓名", "客户联系方式	", "结算月份", "经销商身份", "经销商姓名",
//                        "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "享受收益的会员用户ID",
//                        "享受收益的会员用户手机号", "安装工姓名", "安装工省", "安装工市", "安装工区",
//                        "安装工所在服务站公司名称", "经销商收益", "会员用户收益", "安装服务人员收益", "区县级公司（安装工）收益", "市级公司收益",
//                        "省级公司收益", "产品公司收益"};
//                String[] beanProperties = {"renewOrderId", "workOrderId", "snCode", "productCategory", "renewDate", "payType", "payTime", "tradeNo",
//                        "costName", "secondProductCategory", "orderFee",
//                        "renewTerminal", "waterUserName", "waterUserPhone", "settlementMonth", "distributorType", "distributorName",
//                        "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccountId", "saleId",
//                        "saleMobile", "engineerName", "engineerProvince", "engineerCity", "engineerRegion",
//                        "stationCompanyName", "distributorIncome", "saleIncome", "regionInstallerIncome", "engineerStationCompanyIncome", "cityCompanyIncome",
//                        "provincialCompanyIncome", "productCompanyIncome"};
//                String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "续费收益明细导出";
//                boo = ExcelUtil.exportSXSSF(incomeExportList, header, titles.length - 1, titles, beanProperties, response);
//            } else {
//                throw new YimaoException("没有对应的导出模板");
//            }
//        } else if (productCompanyId == 20000 || productCompanyId == 30000) {
//            //产品公司为上海养未来健康食品有限公司或者上海翼猫生物科技有限公司
//            if (incomeType == 1) {
//                //收益类型-产品收益  商品类型-实物商品
//                String[] titles = {"主订单号", "订单号", "工单号", "订单来源", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量", "订单金额", "流水号", "支付时间", "支付方式",
//                        "订单完成时间", "结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "会员用户ID", "会员用户是否享受收益",
//                        "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "推荐人所在服务站公司名称", "经销商收益", "会员用户收益", "推荐人收益", "区县级发起人收益", "区县级站长收益",
//                        "区县级公司（经销商）收益", "市级发起人收益", "市级合伙人收益", "产品公司收益"};
//                String[] beanProperties = {"mainOrderId", "orderId", "workOrderId", "orderSource", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount", "orderFee", "tradeNo", "payTime", "payType",
//                        "orderCompleteTime", "settlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount", "saleId", "userSaleFlag",
//                        "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion", "refereeStationName", "distributorIncome", "saleIncome", "refereeIncome", "regionSponsorIncome", "stationMasterIncome",
//                        "regionDistributorIncome", "citySponsorIncome", "cityPartnerIncome", "productCompanyIncome"};
//                String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "产品收益明细导出";
//                boo = ExcelUtil.exportSXSSF(incomeExportList, header, titles.length - 1, titles, beanProperties, response);
//            } else {
//                throw new YimaoException("没有对应的导出模板");
//            }
//        } else if (productCompanyId == 50000) {
//            //产品公司为上海翼猫智能科技有限公司
//            if (incomeType == 1) {
//                //收益类型-产品收益  商品类型-虚拟商品
//                String[] titles = {"主订单号", "订单号", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量", "订单金额", "可分配金额", "流水号", "支付时间", "支付方式",
//                        "订单完成时间", "结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "会员用户ID", "会员用户是否享受收益",
//                        "经销商服务站公司名称", "经销商所在服务站是否承包", "承包人姓名", "承包人手机号", "承包人身份证号", "经销商所在服务站是否享受收益", "经销商收益", "会员用户收益", "服务站承包人（产品收益）", "产品公司收益"};
//                String[] beanProperties = {"mainOrderId", "orderId", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount", "orderFee", "settlementFee", "tradeNo", "payTime", "payType",
//                        "orderCompleteTime", "settlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount", "saleId", "userSaleFlag",
//                        "distributorStationName", "distributorStationContract", "contractName", "contractPhone", "contractIdCard", "distributorStationIncome", "distributorIncome", "saleIncome", "stationContractorCompany", "productCompanyIncome"};
//                String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01) + "产品收益明细导出";
//                boo = ExcelUtil.exportSXSSF(incomeExportList, header, titles.length - 1, titles, beanProperties, response);
//            } else {
//                throw new YimaoException("没有对应的导出模板");
//            }
//        } else {
//            throw new YimaoException("没有对应的导出模板");
//        }
//        if (!boo) {
//            throw new YimaoException("导出失败");
//        }
    }

    /**
     * 描述：分配产品销售收益
     *
     * @param orderId 子订单号
     */
    @PostMapping(value = "/allot/sell/income")
    @ApiOperation(value = "分配产品销售收益")
    @ApiImplicitParam(name = "orderId", value = "子订单号", required = true, dataType = "Long", paramType = "query")
    public void allotSellIncome(@RequestParam Long orderId) {
        orderFeign.allotSellIncome(orderId);
    }

    /**
     * 描述：设置收益结算状态
     *
     * @param id
     */
    @PatchMapping(value = "/order/income/settlement/{id}/status")
    @ApiOperation(value = "设置收益结算状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "status", value = "状态：1-可结算 4-暂停结算", required = true, dataType = "Long", paramType = "query")
    })
    public Object setSettlementIncomeStatus(@PathVariable(value = "id") Integer id, @RequestParam(value = "status") Integer status) {
        orderFeign.setSettlementIncomeStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
