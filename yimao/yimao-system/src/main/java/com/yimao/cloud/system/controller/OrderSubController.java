package com.yimao.cloud.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.OrderFrom;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.order.ModifyOrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ModifyOrderVO;
import com.yimao.cloud.pojo.vo.order.OrderBillVO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import com.yimao.cloud.system.feign.OrderFeign;
import com.yimao.cloud.system.feign.ProductFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "OrderSubController")
public class OrderSubController {

    @Resource
    private OrderFeign orderFeign;
    @Resource
    private ProductFeign productFeign;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @param query    订单查询条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     * @return java.lang.Object
     * @description 查询订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    @PostMapping(value = "/order/sub/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单列表根据订单查询条件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "OrderConditionDTO", paramType = "body")
    })
    public PageVO<OrderSubDTO> orderSubList(@RequestBody OrderConditionDTO query, @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return orderFeign.orderSubList(query, pageNum, pageSize);
    }

    /**
     * 订单列表导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/order/export")
    @ApiOperation(value = "订单导出")
    @ApiImplicitParam(name = "query", value = "订单导出", dataType = "OrderConditionDTO", paramType = "body")
    public Object exportOrderExcel(@RequestBody(required = false) OrderConditionDTO query) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/export";
        ExportRecordDTO record = exportRecordService.save(url, "订单");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_ORDER, map);
        }
        return CommResult.ok(record.getId());

        // //1-每次导出条数
        // Integer pageSize = 5000;
        // //2-待导出数量
        // Integer exportCount = orderFeign.orderExportListCount(query);
        // if (exportCount < 1) {
        //     throw new NotFoundException("没有数据可以导出");
        // }
        // //3-导出次数
        // Integer count = exportCount % pageSize > 0 ? exportCount / pageSize + 1 : exportCount / pageSize;
        // List<OrderExportDTO> orderList = new ArrayList<>();
        // //4-按次数将数据写入文件
        // for (int i = 1; i <= count; i++) {
        //     orderList.addAll(orderFeign.orderExportList(query, i, pageSize));
        // }
        //
        // String[] titles;
        // String[] beanPropertys;
        // //excel表头和文件名格式为 yyyy年MM月dd天 **对账
        // String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        //
        // titles = new String[]{"主订单号", "子订单号", "云平台工单号", "订单来源", "订单状态", "下单时间", "支付方式", "支付状态",
        //         "支付时间", "流水号", "订单完成时间", "下单用户身份", "下单用户ID", "收货人", "联系方式", "收货地址", "活动方式", "产品名称",
        //         "产品公司", "产品类型(一级类目)", "产品范围(二级类目)", "产品型号(三级类目)", "数量", "订单总金额", "经销商身份", "经销商姓名",
        //         "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名", "会员用户ID", "会员用户是否有收益", "备注"};
        //
        // beanPropertys = new String[]{"mainOrderId", "id", "refer", "terminal", "status", "createTime", "payType", "payStatus",
        //         "payTime", "tradeNo", "completeTime", "userType", "userId", "addresseeName", "addresseePhone", "addressee", "activityType",
        //         "productName", "productCompanyName", "productFirstCategoryName", "productSecondCategoryName", "productCategoryName",
        //         "count", "orderAmountFee", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince",
        //         "distributorCity", "distributorRegion", "hasSubDistributor", "subDistributorAccount", "subDistributorName", "vipUserId", "vipUserHasIncomeTxt", "remark"};
        //
        // header = header + " 订单";
        //
        // boolean boo = ExcelUtil.exportSXSSF(orderList, header, titles.length - 1, titles, beanPropertys, response);
        // if (!boo) {
        //     throw new YimaoException("导出失败");
        // }

    }

    /**
     * 后台修改子订单页面初始化参数
     *
     * @param orderId 子订单号
     */
    @GetMapping(value = "/order/sub/modify")
    @ApiOperation(value = "后台修改子订单页面初始化参数")
    @ApiImplicitParam(name = "orderId", value = "子订单号", required = true, dataType = "Long", paramType = "query")
    public Object getModifyOrderInfo(@RequestParam Long orderId) {
        OrderSubDTO subOrder = orderFeign.findOrderBasicInfoById(orderId);
        if (subOrder == null) {
            throw new BadRequestException("获取订单信息失败。");
        }
        List<ProductDTO> productList = productFeign.listProductForModifyOrder(orderId, subOrder.getProductId(), subOrder.getCostId());
        if (CollectionUtil.isEmpty(productList)) {
            return null;
        }
        Integer productId = subOrder.getProductId();
        ModifyOrderVO vo = new ModifyOrderVO();

        for (ProductDTO dto : productList) {
            if (Objects.equals(productId, dto.getId())) {
                vo.setProductId(subOrder.getProductId());
                vo.setProductName(subOrder.getProductName());
                vo.setProductCategoryName(dto.getFrontFirstCategoryName());
            }
        }

        vo.setProductList(productList);

        vo.setAddresseeName(subOrder.getAddresseeName());
        vo.setAddresseePhone(subOrder.getAddresseePhone());
        vo.setAddresseeSex(subOrder.getAddresseeSex());
        vo.setAddresseeProvince(subOrder.getAddresseeProvince());
        vo.setAddresseeCity(subOrder.getAddresseeCity());
        vo.setAddresseeRegion(subOrder.getAddresseeRegion());
        vo.setAddresseeStreet(subOrder.getAddresseeStreet());
        return vo;
    }

    /**
     * 后台修改子订单
     *
     * @param modifyOrder 后台修改子订单操作入参信息
     */
    @PatchMapping(value = "/order/sub/modify")
    @ApiOperation(value = "后台修改子订单")
    @ApiImplicitParam(name = "modifyOrder", value = "后台修改子订单操作入参信息", required = true, dataType = "ModifyOrderDTO", paramType = "body")
    public void updateSubOrder(@RequestBody ModifyOrderDTO modifyOrder) {
        if (modifyOrder.getOrderId() == null) {
            throw new BadRequestException("订单ID不能为空。");
        }
        if (modifyOrder.getProductId() == null) {
            throw new BadRequestException("产品ID不能为空。");
        }
        OrderSubDTO subOrder = new OrderSubDTO();
        subOrder.setId(modifyOrder.getOrderId());
        subOrder.setProductId(modifyOrder.getProductId());  //必传
        subOrder.setCostId(modifyOrder.getCostId());        //修改水机传
        subOrder.setCostName(modifyOrder.getCostName());    //修改水机传
        subOrder.setProductModel(modifyOrder.getCategoryName());
        subOrder.setAddresseeName(modifyOrder.getAddresseeName());
        subOrder.setAddresseePhone(modifyOrder.getAddresseePhone());
        subOrder.setAddresseeSex(modifyOrder.getAddresseeSex());
        subOrder.setAddresseeProvince(modifyOrder.getAddresseeProvince());
        subOrder.setAddresseeCity(modifyOrder.getAddresseeCity());
        subOrder.setAddresseeRegion(modifyOrder.getAddresseeRegion());
        subOrder.setAddresseeStreet(modifyOrder.getAddresseeStreet());
        orderFeign.updateSubOrder(subOrder);
    }


    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号查询订单详情
     * @author zhilin.he
     * @date 2019/1/12 13:46
     */
    @GetMapping(value = "/order/sub/{id}")
    @ApiOperation(value = "根据订单号查询订单详情")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public OrderSubDTO findOrderInfoById(@PathVariable Long id) {
        return orderFeign.findOrderDetailById(id);
    }

    /**
     * @param id 订单号
     * @return java.lang.Object
     * @description 根据订单号删除订单（逻辑删除）
     * @author zhilin.he
     * @date 2019/1/24 15:21
     */
    @DeleteMapping(value = {"/order/sub/{id}"})
    @ApiOperation(value = "根据订单号删除订单")
    @ApiImplicitParam(name = "id", value = "订单ID", dataType = "Long", required = true, paramType = "path")
    public Object deleteOrder(@PathVariable("id") Long id) {
        orderFeign.deleteOrder(id, OrderFrom.SYSTEM.value);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param cancelReason 取消订单原因
     * @param cancelDetail 取消订单详情
     * @param ids          订单id集合
     * @return java.lang.Object
     * @description 批量更新订单状态（批量取消订单）
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @PutMapping(value = {"/order/sub/batch/status"})
    @ApiOperation(value = "批量取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cancelReason", value = "取消订单原因", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cancelDetail", value = "取消订单详情", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "子订单id集合", allowMultiple = true, dataType = "Long", required = true, paramType = "query")})
    public JSONObject updateOrderStatusBatch(@RequestParam(value = "cancelReason") String cancelReason,
                                             @RequestParam(value = "cancelDetail", required = false) String cancelDetail,
                                             @RequestParam(value = "ids") List<Long> ids) {
        return orderFeign.updateOrderStatusBatch(cancelReason, cancelDetail, ids);
    }


    /**
     * @param ids 订单id集合
     * @return java.lang.Object
     * @description 批量更新是否删除订单(逻辑删除)
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    @PutMapping(value = {"/order/sub/deleted"})
    @ApiOperation(value = "批量删除订单")
    @ApiImplicitParam(name = "ids", value = "订单id集合", allowMultiple = true, dataType = "Long", required = true, paramType = "query")
    public Object updateOrderDeleted(@RequestParam(value = "ids") List<Long> ids) {
        orderFeign.updateOrderDeleted(ids);
        return ResponseEntity.noContent().build();
    }


    /**
     * 查询订单对账列表
     *
     * @param query    查询条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     */
    @PostMapping(value = "/order/sub/bill/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询订单对账列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", defaultValue = "1", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", dataType = "Long", defaultValue = "10", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "OrderBillQuery", paramType = "body"),
    })
    public PageVO<OrderBillVO> orderBillList(@RequestBody OrderBillQuery query,
                                             @PathVariable Integer pageNum,
                                             @PathVariable Integer pageSize) {
        return orderFeign.queryOrderBillList(query, pageNum, pageSize);
    }


    /**
     * 查询订单对账导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/order/sub/bill/export")
    @ApiOperation(value = "订单对账导出")
    @ApiImplicitParam(name = "query", value = "查询条件", dataType = "OrderBillQuery", paramType = "body")
    public Object exportReconciliationExcel(@RequestBody OrderBillQuery query, HttpServletResponse response) {
        Integer mode = query.getMode();
        if (mode == null) {
            mode = 0;
        }
        if (mode != 0 && ProductModeEnum.find(mode) == null) {
            throw new BadRequestException("商品模式选择错误。");
        }

        Integer companyId = query.getProductCompanyId();
        if (companyId == null) {
            companyId = 0;
        }
        String companyName = "";
        if (companyId != 0) {
            ProductCompanyVO company = productFeign.productCompanyGetById(query.getProductCompanyId());
            if (company != null) {
                companyName = company.getName();
            }
        }
        String title = "";
        if (mode == 0) {
            //汇总
            title = "汇总对账";
        } else if (mode == ProductModeEnum.REALTHING.value || mode == ProductModeEnum.LEASE.value || mode == ProductModeEnum.VIRTUAL.value) {
            //实物(健康食品和生物科技) //租赁 //虚拟
            title = companyName;
        }
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/order/sub/bill/export";
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

        /*List<OrderBillExport> orderList = orderFeign.exportOrderBill(query);
        if (CollectionUtil.isEmpty(orderList)) {
            throw new NotFoundException("没有数据可以导出");
        }
	
        String[] titles = null;
        String[] beanPropertys = null;
        //excel表头和文件名格式为 yyyy年MM月dd天 **对账
        String header = DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        if (mode == 0) {
            titles = new String[]{"主订单号", "子订单号", "云平台工单号", "订单来源", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "销售主体省市区", "销售主体公司名称", "收款说明"};

            beanPropertys = new String[]{"mainOrderId", "id", "refer", "terminal", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName", "count",
                    "fee", "tradeNo", "payTime", "payType", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

            header = header + " 汇总对账";
        } else if (mode == ProductModeEnum.REALTHING.value) {
            //实物(健康食品和生物科技)
            titles = new String[]{"主订单号", "子订单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名", "推荐人姓名",
                    "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "销售主体省市区", "销售主体公司名称", "收款说明"};

            beanPropertys = new String[]{"mainOrderId", "id", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName", "recommendName", "recommendAccount", "recommendProvince", "recommendCity", "recommendRegion", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

            header = header + companyName;
        } else if (mode == ProductModeEnum.LEASE.value) {
            //租赁
            titles = new String[]{"主订单号", "子订单号", "工单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "计费方式", "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名"};

            beanPropertys = new String[]{"mainOrderId", "id", "refer", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "costName", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName"};

            header = header + companyName;
        } else if (mode == ProductModeEnum.VIRTUAL.value) {
            //虚拟
            titles = new String[]{"主订单号", "子订单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名",
                    "销售主体省市区", "销售主体公司名称", "收款说明"};

            beanPropertys = new String[]{"mainOrderId", "id", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

            header = header + companyName;
        }

        boolean boo = ExcelUtil.exportSXSSF(orderList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/

    }


}
