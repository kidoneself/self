package com.yimao.cloud.order.processor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderInstallTypeEnum;
import com.yimao.cloud.base.enums.WorkOrderOperationType;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.AfterSalesOrderMapper;
import com.yimao.cloud.order.mapper.InvestmentIncomeRecordMapper;
import com.yimao.cloud.order.mapper.MaintenanceWorkOrderMapper;
import com.yimao.cloud.order.mapper.OrderDeliveryMapper;
import com.yimao.cloud.order.mapper.OrderInvoiceMapper;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.mapper.OrderPayCheckMapper;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.OrderWithdrawMapper;
import com.yimao.cloud.order.mapper.ReimburseManageMapper;
import com.yimao.cloud.order.mapper.ServiceIncomeRecordMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.DeliveryConditionDTO;
import com.yimao.cloud.pojo.dto.order.DeliveryInfoExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderExportDTO;
import com.yimao.cloud.pojo.dto.order.MaintenanceWorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderExportDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceExportDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.RentalGoodsExportDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawExportDTO;
import com.yimao.cloud.pojo.dto.order.WithdrawQueryDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderExportDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderInvoiceExportDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.refundManageExportDTO;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.export.order.InvestmentIncomeExportDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.export.order.OrderCheckExport;
import com.yimao.cloud.pojo.export.order.OrderRenewExport;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.order.RenewOrderQuery;
import com.yimao.cloud.pojo.query.order.WithdrawQuery;
import com.yimao.cloud.pojo.vo.order.OrderRenewVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述：导出
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
@Component
@Slf4j
public class ExportProcessor {

    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final int pageSize = 500;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private DomainProperties domainProperties;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderMainMapper orderMainMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private OrderWithdrawMapper orderWithdrawMapper;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductIncomeRecordService productIncomeRecordService;
    @Resource
    private ServiceIncomeRecordMapper serviceIncomeRecordMapper;
    @Resource
    private ReimburseManageMapper reimburseManageMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private InvestmentIncomeRecordMapper investmentIncomeRecordMapper;
    @Resource
    private AfterSalesOrderMapper afterSalesOrderMapper;
    @Resource
    private OrderRenewMapper orderRenewMapper;
    @Resource
    private MaintenanceWorkOrderMapper maintenanceWorkOrderMapper;
    @Resource
    private OrderInvoiceMapper orderInvoiceMapper;
    @Resource
    private OrderPayCheckMapper orderPayCheckMapper;
    @Resource
    private OrderDeliveryMapper orderDeliveryMapper;

    /**
     * 导出
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_ACTION_ORDER)
    @RabbitHandler
    public void processor(final Map<String, Object> map) {
        executor.submit(() -> {
        ExportRecordDTO record = (ExportRecordDTO) map.get("exportRecordDTO");
        String url = record.getUrl();
        Integer recordId = record.getId();
        Integer adminId = record.getAdminId();
        SXSSFWorkbook workbook = null;
        try {
            //导出中
            record.setStatus(ExportRecordStatus.IN_EXPORT.value);
            record.setStatusName(ExportRecordStatus.IN_EXPORT.name);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            //设置下载进度
            redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
            String[] titles = null;
            String[] beanProperties = null;
            //url区分是哪项导出
            switch (url) {
                //支付审核纪录列表
                case "/order/main/paycheckRecordExport":
                    titles = new String[]{"主订单号/续费单号/工单号", "订单类型", "财务审核状态", "审核不通过原因", "审核人", "财务审核时间"};
                    beanProperties = new String[]{"orderId", "orderType", "payCheckStatusName", "reason", "creator", "createTime"};
                    OrderPayCheckDTO orderPayCheckDTO = (OrderPayCheckDTO) map.get("query");
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, this.getPaycheckRecordExportData(orderPayCheckDTO, adminId, recordId));
                    break;
                //续费支付待审核列表
                case "/order/renew/paycheckExport":
                    titles = new String[]{"续费单号", "产品型号", "实付款", "支付状态", "支付方式", "提交凭证时间"};
                    beanProperties = new String[]{"id", "productCategoryName", "amountFee", "statusName", "payTypeName", "payCredentialSubmitTime"};
                    RenewOrderQuery renewOrderQuery = (RenewOrderQuery) map.get("query");
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, this.getRenewPaycheckExportData(renewOrderQuery, adminId, recordId));
                    break;
                //订单列表导出
                case "/order/export":
                    titles = new String[]{"主订单号", "子订单号", "云平台工单号", "订单来源", "订单状态", "下单时间", "支付方式", "支付状态",
                            "支付时间", "流水号", "订单完成时间", "下单用户身份", "下单用户ID", "收货人", "联系方式", "收货地址", "活动方式", "产品名称",
                            "产品公司", "产品类型(一级类目)", "产品范围(二级类目)", "产品型号(三级类目)", "数量", "订单总金额", "经销商身份", "经销商姓名",
                            "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名", "会员用户ID", "会员用户是否有收益", "备注"};
                    beanProperties = new String[]{"mainOrderId", "id", "refer", "terminal", "status", "createTime", "payType", "payStatus",
                            "payTime", "tradeNo", "completeTime", "userType", "userId", "addresseeName", "addresseePhone", "addressee", "activityType",
                            "productName", "productCompanyName", "productFirstCategoryName", "productSecondCategoryName", "productCategoryName",
                            "count", "orderAmountFee", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince",
                            "distributorCity", "distributorRegion", "hasSubDistributor", "subDistributorAccount", "subDistributorName", "vipUserId", "vipUserHasIncomeTxt", "remark"};
                    OrderConditionDTO query = (OrderConditionDTO) map.get("query");
                    workbook = this.subOrderExportData(query, adminId, recordId, titles, beanProperties);
                    this.uploadExportData(record, workbook);
                    break;
                //仅退款实物商品-1：全部 2：待审核 3：审核记录导出
                case "/refund/matter/goods/export":
                    AfterSalesConditionDTO afterSalesConditionDTO = (AfterSalesConditionDTO) map.get("query");
                    Integer type = afterSalesConditionDTO.getType();
                    if (type == 1) {
                        titles = new String[]{"售后单号", "子订单号", "主订单号", "订单来源", "产品类目", "用户ID",
                                "收货人", "经销商账号", "经销商姓名", "售后申请端", "申请数量", "申请时间", "申请原因", "售后状态"};
                        beanProperties = new String[]{"salesId", "orderId", "mainOrderId",
                                "salesTerminal", "productCategoryName", "userId", "addresseeName",
                                "distributorAccount", "distributorName", "salesTerminal", "num", "createTime", "cancelReason", "status"};
                    } else if (type == 2) {
                        titles = new String[]{"售后单号", "子订单号", "主订单号", "订单来源", "产品类目", "用户ID",
                                "收货人", "经销商账号", "经销商姓名", "售后申请端", "申请数量", "申请时间", "申请原因", "备注"};
                        beanProperties = new String[]{"salesId", "orderId", "mainOrderId",
                                "salesTerminal", "productCategoryName", "userId", "addresseeName",
                                "distributorAccount", "distributorName", "salesTerminal", "num", "createTime", "cancelReason", "remark"};
                    } else {
                        titles = new String[]{"售后单号", "子订单号", "主订单号", "售后申请时间", "申请原因", "审核人", "审核处理时间", "审核状态", "审核不通过原因", "备注"};
                        beanProperties = new String[]{"salesId", "orderId", "mainOrderId",
                                "createTime", "cancelReason", "buyer", "handleTime", "operationStatus", "auditReason", "remark"};
                    }
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, this.getRentalGoodsExportData(afterSalesConditionDTO, adminId, recordId));
                    break;
                case "/order/renew/export":
                    titles = new String[]{"续费单号", "工单号", "设备SN码", "设备型号", "续费时间", "支付方式", "支付时间", "流水号", "上一次计费方式", "计费模板名称",
                            "计费类型", "支付金额", "续费次数", "续费端", "设备所在省", "设备所在市", "设备所在区", "客户姓名", "客户联系方式", "经销商姓名", "经销商联系方式",
                            "经销商账号", "经销商身份证号码", "经销商归属地", "经销商服务站", "经销商推荐人", "推荐人归属地", "推荐人服务站", "安装工姓名", "安装工联系方式", "安装工服务站"};
                    beanProperties = new String[]{"id", "workOrderId", "snCode", "deviceModel", "createTime", "payTypeName", "payTime", "tradeNo",
                            "lastCostName", "costName", "costTypeName", "amountFee", "times", "terminalName", "province", "city", "region", "waterUserName",
                            "waterUserPhone", "distributorName", "distributorPhone", "distributorAccount", "distributorIdCard", "distributorArea",
                            "distributorStationName", "distributorRecommendName", "distributorRecommendArea", "distributorRecommendStationName", "engineerName",
                            "engineerPhone", "engineerStationName"};
                    RenewOrderQuery renewOrderQuery2 = (RenewOrderQuery) map.get("query");
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, this.getRenewOrderExportData(renewOrderQuery2, adminId, recordId));
                    break;
                case "/order/renew/finance/export":
                    titles = new String[]{"续费单号", "工单号", "设备SN码", "设备型号", "续费时间", "支付方式", "支付时间", "流水号", "上一次计费方式", "计费模板名称",
                            "计费类型", "支付金额", "续费次数", "续费端", "设备所在省", "设备所在市", "设备所在区", "客户姓名", "客户联系方式", "经销商姓名", "经销商联系方式",
                            "经销商账号", "经销商身份证号码", "经销商归属地", "经销商服务站", "经销商推荐人", "推荐人归属地", "推荐人服务站", "安装工姓名", "安装工联系方式", "安装工服务站"};
                    beanProperties = new String[]{"id", "workOrderId", "snCode", "deviceModel", "createTime", "payTypeName", "payTime", "tradeNo",
                            "lastCostName", "costName", "costTypeName", "amountFee", "times", "terminalName", "province", "city", "region", "waterUserName",
                            "waterUserPhone", "distributorName", "distributorPhone", "distributorAccount", "distributorIdCard", "distributorArea",
                            "distributorStationName", "distributorRecommendName", "distributorRecommendArea", "distributorRecommendStationName", "engineerName",
                            "engineerPhone", "engineerStationName"};
                    RenewOrderQuery renewOrderQuery3 = (RenewOrderQuery) map.get("query");
                    renewOrderQuery3.setStatus(2);//对账导出只支付完成的单子
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, this.getRenewOrderExportData(renewOrderQuery3, adminId, recordId));
                    break;
                case "/order/main/paycheckList/export":
                    OrderMainDTO paycheckQuery = (OrderMainDTO) map.get("query");
                    List<OrderCheckExport> paycheckList = this.orderMainPayCheckExport(paycheckQuery, adminId, recordId);
                    titles = new String[]{"主订单号", "产品公司", "数量", "实付款", "支付状态", "支付方式", "提交凭证时间", "支付类型"};
                    beanProperties = new String[]{"id", "productCompanyName", "count", "orderAmountFee", "payStatus", "payType", "payCredentialSubmitTime", "payTerminal"};
                    //导出成功
                    this.exportSuccessful(record, titles, beanProperties, paycheckList);
                    break;

                    case "/order/main/deliveryPaycheckList/export":
                        WorkOrderQueryDTO deliveryPaycheckQuery = (WorkOrderQueryDTO) map.get("query");
                        List<OrderCheckExport> deliveryPaycheckList = this.orderDeliveryPayCheckListExport(deliveryPaycheckQuery, adminId, recordId);
                        titles = new String[]{"工单号", "子订单号", "数量", "实付款", "状态", "支付方式", "下单时间", "支付类型"};
                        beanProperties = new String[]{"id", "subOrderId", "count", "fee", "status", "payType", "createTime", "payTerminal"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, deliveryPaycheckList);
                        break;

                    case "/withdraw/export":
                        WithdrawQuery withdrawQuery = (WithdrawQuery) map.get("query");
                        List<WithdrawExportDTO> withdrawList = this.withdrawListExport(withdrawQuery, adminId, recordId);
                        titles = new String[]{"主订单单号", "子订单号/续费单号", "产品类目", "产品数量", "订单金额", "支付方式", "订单完成时间", "可提现金额", "收益类型", "用户下单时身份", "用户姓名", "用户手机号"};
                        beanProperties = new String[]{"mainOrderId", "orderId", "productCategoryName", "productNum", "orderFee", "payType", "orderCompleteTime", "withdrawFee", "incomeType", "userType", "userName", "phone"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, withdrawList);
                        break;

                    case "/withdraw/audit/export":
                        WithdrawQuery withdrawAuditQuery = (WithdrawQuery) map.get("query");
                        List<WithdrawExportDTO> withdrawAuditList = this.withdrawAuditListExport(withdrawAuditQuery, adminId, recordId);
                        titles = new String[]{"主提现单号", "子提现单号", "主订单号", "子订单号/续费单号", "工单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "产品类型", "产品范围", "产品型号", "产品公司", "订单金额", "支付时间",
                                "订单完成时间", "收益类型", "用户身份", "用户ID", "用户姓名", "用户手机号", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号",
                                "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区"};
                        beanProperties = new String[]{"mainPartnerTradeNo", "id", "mainOrderId", "orderId", "workOrderId", "withdrawFee", "applyTime", "status", "withdrawType", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "orderFee", "payTime",
                                "orderCompleteTime", "incomeType", "userType", "userId", "userName", "phone", "distributorType", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount",
                                "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, withdrawAuditList);
                        break;

                    case "/withdraw/record/detail/export":
                        WithdrawQueryDTO withdrawDetailQuery = (WithdrawQueryDTO) map.get("query");
                        List<WithdrawExportDTO> withdrawDetailList = this.withdrawRecordDetailListExport(withdrawDetailQuery, adminId, recordId);
                        titles = new String[]{"主提现单号", "子提现单号", "主订单号", "子订单号/续费单号", "工单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "流水号", "提现到账时间",
                                "产品类型", "产品范围", "产品型号", "收益类型", "产品公司", "用户身份", "用户ID", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区",
                                "是否有子账号", "子账号", "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "结算主体省市区", "结算主体公司名称", "付款说明", "审核时间"};
                        beanProperties = new String[]{"mainPartnerTradeNo", "id", "mainOrderId", "orderId", "workOrderId", "withdrawFee", "applyTime", "status", "withdrawType", "paymentNo", "paymentTime",
                                "firstProductCategory", "secondProductCategory", "productCategory", "incomeType", "productCompanyName", "userType", "userId", "distributorType", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                                "hasSubAccount", "subAccount", "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion", "subjectArea", "subjectCompany", "payInstructions", "auditTime"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, withdrawDetailList);
                        break;

                    case "/refund/record/export":
                        OrderSubDTO refundRecordQuery = (OrderSubDTO) map.get("query");
                        List<refundManageExportDTO> refundRecordList = this.exportRefund(refundRecordQuery, adminId, recordId);
                        titles = new String[]{
                                "售后单号", "子订单号", "主订单号", "订单来源",
                                "产品类型", "产品型号", "型号范围", "产品公司",
                                "支付方式", "用户ID", "收货人", "经销商账户",
                                "经销商姓名", "售后申请时间", "申请原因", "申请数量",
                                "应退金额", "手续费", "可退金额", "售后状态", "流水号", "退款审核时间", "审核人"};
                        beanProperties = new String[]{
                                "afterSalesOrderId", "id", "mainOrderId", "terminal",
                                "productOneCategoryName", "productCategoryName", "productTwoCategoryName", "productCompanyName",
                                "payType", "userId", "addresseeName", "distributorAccount",
                                "distributorName", "cancelTime", "cancelReason", "count",
                                "fee", "formalitiesFee", "shouldReturn", "subStatus", "refundTradeNo", "financeTime", "financer"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, refundRecordList);
                        break;
                    case "/order/invoice/export":
                        titles = new String[]{"工单号", "提货状态", "省", "市", "区", "水机数量", "下单时间",
                                "支付方式", "状态", "经销商姓名", "经销商联系方式", "经销商归属地", "经销商服务站", "经销商推荐人", "推荐人归属地", "推荐人服务站", "客服姓名", "客服联系方式", "服务站", "客服接单时间", "交易单号"
                                , "计费方式", "计费金额", "开户费", "商品类型", "SN码", "设备添加时间", "支付时间", "用户姓名", "用户联系方式", "派送方式", "支付端", "完成时间", "提货时间",
                                "物流编码", "客户邮箱", "是否开票", "发票类型", "发票抬头", "税号", "开户行", "开户号", "开票金额", "公司名称", "地址", "电话", "开票时间", "经销商类型"};
                        beanProperties = new String[]{"workOrderId", "isTake", "province", "city", "region", "count", "orderTimeStr",
                                "payTypeStr", "statusStr", "realName", "distributorPhone", "distributorAddress", "distributorStation", "recommendName", "recommendAddress", "recommendStation", "serviceEngineerName", "serviceEngineerPhone", "stationName", "acceptTimeStr", "tradeNo"
                                , "costName", "modelPrice", "openAccountFee", "deviceModel", "snCode", "deviceActiveTimeStr", "payTimeStr", "userName", "userPhone", "dispatchTypeStr", "payTerminalStr", "completeTimeStr", "pickTimeStr",
                                "logisticsCode", "billEmail", "isBilling", "invoiceTypeStr", "invoiceHeadStr", "dutyNo", "bankName", "bankAccount", "billFee", "companyName", "billAddress", "billPhone", "billTimeStr", "distributorTypeName"};
                        OrderInvoiceQueryDTO orderInvoiceQuery = (OrderInvoiceQueryDTO) map.get("query");
                        workbook = orderInvoiceExport(orderInvoiceQuery, adminId, recordId, titles, beanProperties);
                        //导出成功
                        this.uploadExportData(record, workbook);
                        break;

                    case ExportUrlConstant.EXPORT_MAINTENANCEWORKORDER_URL:
                        MaintenanceWorkOrderQueryDTO maintenanceQuery = (MaintenanceWorkOrderQueryDTO) map.get("query");
                        List<MaintenanceWorkOrderExportDTO> maintenanceExportList = maintenanceWorkOrderExportData(maintenanceQuery, adminId, recordId);
                        titles = new String[]{"维护工单号", "滤芯类型", "地址", "客户名称", "客户电话", "批次码", "产品范围", "设备型号", "设备SN码", "设备ICCID", "完成状态", "完成时间", "创建时间", "审核方式", "来源"};
                        beanProperties = new String[]{"id", "materielDetailName", "address", "consumerName", "consumerPhone", "deviceBatchCode", "kindName", "deviceModelName", "deviceSncode", "deviceSimcard", "workOrderCompleteStatus", "workOrderCompleteTime", "createTime", "auditType", "source"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, maintenanceExportList);
                        break;
                    case ExportUrlConstant.EXPORT_INVESTMENTINCOME_URL:
                        InvestmentIncomeQueryDTO incomeQuery = (InvestmentIncomeQueryDTO) map.get("query");
                        List<InvestmentIncomeExportDTO> investmentIncomeExportList = investmentIncomeExportData(incomeQuery, adminId, recordId);
                        titles = new String[]{"订单号", "订单类型", "经销商类型", "实付款", "应收款", "多收款", "付款主体",
                                "支付方式", "支付时间", "流水号", "订单完成时间", "结算月份", "升级后经销商类型", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "推荐人经销商姓名", "推荐人经销商账户", "推荐人归属区域"
                                , "推荐人服务站公司", "推荐人收益", "智慧助理收益", "区县级公司（推荐人）收益", "翼猫总部收益"};
                        beanProperties = new String[]{"orderId", "distributorOrderTypeStr", "distributorTypeStr", "realPayment", "receivableMoney", "moreMoney", "paySubject",
                                "payTypeStr", "payTime", "trade", "orderCompletionTimeStr", "settlementMonth", "destDistributorTypeStr", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                                "refereeName", "refereeAccount", "refereeDistrict"
                                , "refereeStationCompany", "refereeIncome", "wisdomAssistantIncome", "refereeStationCompanyIncome", "yiMaoHQIncome"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, investmentIncomeExportList);
                        break;
                    //提现记录导出
                    case "/withdraw/record/export":
                        WithdrawQueryDTO wdQuery = (WithdrawQueryDTO) map.get("query");
                        List<WithdrawExportDTO> wdList = this.withdrawExportData(wdQuery, adminId, recordId);
                        titles = new String[]{"主提现单号 ", "子提现单号", "提现金额", "申请提现时间", "审核状态", "提现方式", "流水号", "收益类型", "产品公司", "提现到账时间", "用户身份", "用户ID",
                                "审核时间", "审核人"};
                        beanProperties = new String[]{"mainPartnerTradeNo", "id", "withdrawFee", "applyTime", "status", "withdrawType", "paymentNo", "incomeType",
                                "productCompanyName", "paymentTime", "userType", "userId", "auditTime", "updater"};
                        this.exportSuccessful(record, titles, beanProperties, wdList);
                        break;

                    //订单对账
                    case "/order/sub/bill/export":
                        OrderBillQuery billquery = (OrderBillQuery) map.get("query");
                        List<OrderBillExport> billList = this.billExportData(billquery, adminId, recordId);
                        titles = getBilltitles(billquery.getMode());
                        beanProperties = getBillbeanPropertys(billquery.getMode());
                        this.exportSuccessful(record, titles, beanProperties, billList);
                        break;

                    case "/workorder/export"://工单列表导出
                        beanProperties = new String[]{"id", "subOrderId", "type", "isTake", "status", "chargeBackStatusText", "createTime",
                                "payTerminal", "payType", "payTime", "tradeNo", "pickTime", "completeTime", "createUserId",
                                "createUserName", "userTypeName", "createUserPhone", "receiveName", "receivePhone",
                                "receiveProvice", "receiveCity", "receiveRegion", "receiveAdress", "firstCategoryName", "twoCategoryName",
                                "threeCategoryName", "count", "costName", "costTypeName", "modelPrice", "openAccountFee", "dispatchType",
                                "engineerName", "engineerPhone", "province", "city", "region", "stationName", "acceptTime",
                                "snCode", "deviceActiveTime", "logisticsCode", "distributorRealName", "distributorPhone", "distributorIdCard",
                                "distributorAccount", "distributorTypeName", "distributorFirstUpgradetime", "subDistributorRealName", "subDistributorName",
                                "distributorAddress", "distributorRegion", "distributorRefereeName", "distributorRefereeAddress", "distributorRefereeRegion", "completePay",
                                "completeFirstPayMoney", "payCompleteMoney", "payCompleteTime", "payCompleteTradeNo", "payCompletePayType", "engineerName",
                                "engineerPhone", "settlementTime"};
                        titles = new String[]{"工单号", "子订单号", "工单来源", "提货状态", "工单状态", "退单状态", "下单时间",
                                "支付类型", "支付方式", "支付时间", "流水号", "提货时间", "完成时间", "下单用户id",
                                "下单用户姓名", "下单用户身份", "用户联系方式", "收货人姓名", "收货人联系方式",
                                "收货人省", "收货人市", "收货人区", "收货地址", "产品类型(一级类目)", "产品范围(二级类目)",
                                "产品型号(三级类目)", "水机数量", "计费模版名称", "计费类型", "计费金额", "开户费", "派单方式",
                                "安装工程姓名", "安装工程师联系方式", "安装工省", "安装工市", "安装工区", "安装工服务站", "客服接单时间",
                                "设备SN码", "设备添加时间", "物流编号", "经销商姓名", "经销商联系方式", "经销商身份证号",
                                "经销商账户", "经销商类型", "体验版经销商第一次升级时间", "子账号姓名", "子账号登录名",
                                "经销商归属地", "经销商服务站", "经销商推荐人", "推荐人归属地", "推荐人服务站", "完款状态",
                                "首款金额", "完款金额", "完款时间", "完款交易单号", "完款支付方式", "服务工程师姓名",
                                "服务工程师联系方式", "结算日期"};
                        WorkOrderQueryDTO workOrderQuery = (WorkOrderQueryDTO) map.get("query");
                        workbook = this.workOrderExportData(workOrderQuery, adminId, recordId, titles, beanProperties);
                        this.uploadExportData(record, workbook);
                        break;
                    case "/workorder/invoice/export"://工单发票信息导出
                        WorkOrderQueryDTO invoiceQuery = (WorkOrderQueryDTO) map.get("query");
                        List<WorkOrderInvoiceExportDTO> invoiceExportData = this.invoiceExportData(invoiceQuery, adminId, recordId);
                        titles = new String[]{"工单号", "省", "市", "区", "用户姓名", "用户联系方式", "客服姓名", "客服联系方式", "商品类型", "计费方式", "发票类型", "发票抬头", "公司名称", "税号", "开户行", "开户号", "地址", "电话", "下单时间", "支付时间", "开票时间", "开票金额", "完成时间"};
                        beanProperties = new String[]{"workOrderId", "province", "city", "region", "userRealName", "userPhone", "engineerName", "engineerPhone", "deviceModel", "costName", "invoiceType", "invoiceHead", "companyName", "dutyNo", "bankName", "bankAccount", "companyAddress", "companyPhone", "createTime", "payTime", "applyTime", "amountFee", "confirmTime"};
                        this.exportSuccessful(record, titles, beanProperties, invoiceExportData);
                        break;
                    case "/order/service/income/export"://产品服务收益导出
                        ProductIncomeQueryDTO serviceIncomeQuery = (ProductIncomeQueryDTO) map.get("query");
                        List<IncomeExportDTO> serviceIncomeExportData = this.serviceIncomeExportData(serviceIncomeQuery, adminId, recordId);
                        titles = new String[]{"主订单号", "订单号", "下单时间", "体检卡号", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                                "订单金额", "可分配金额", "流水号", "支付时间", "支付方式", "使用时间", "结算月份", "体检人姓名", "体检人手机号", "体检服务站省", "体检服务站市",
                                "体检服务站区", "体检服务站公司名称", "服务站承包人（服务收益）"};
                        beanProperties = new String[]{"mainOrderId", "orderId", "createTime", "ticketNo", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount",
                                "orderFee", "settlementFee", "tradeNo", "payTime", "payType", "examDate", "settlementMonth",
                                "medicalName", "medicalPhone", "stationProvince", "stationCity", "stationRegion", "stationCompanyName", "stationContractorService"};
                        this.exportSuccessful(record, titles, beanProperties, serviceIncomeExportData);
                        break;

                    case "/reimburse/manage/export"://退款管理导出
                        OrderSubDTO refundQuery = (OrderSubDTO) map.get("query");
                        List<refundManageExportDTO> refundExportData = this.refundExportData(refundQuery, adminId, recordId);
                        beanProperties = new String[]{"afterSalesOrderId", "id", "mainOrderId", "terminal", "productOneCategoryName", "productCategoryName", "productTwoCategoryName", "productCompanyName",
                                "payType", "userId", "addresseeName", "distributorAccount", "distributorName", "cancelTime", "cancelReason", "count",
                                "fee", "formalitiesFee", "shouldReturn", "subStatus"};
                        titles = new String[]{"售后单号", "子订单号", "主订单号", "订单来源", "产品类型", "产品型号", "型号范围", "产品公司", "支付方式", "用户ID", "收货人", "经销商账户",
                                "经销商姓名", "售后申请时间", "申请原因", "申请数量", "应退金额", "手续费", "可退金额", "售后状态"};
                        this.exportSuccessful(record, titles, beanProperties, refundExportData);
                        break;

                    case "/order/product/income/export": //产品收益明细导出
                        ProductIncomeQueryDTO productIncomeQuery = (ProductIncomeQueryDTO) map.get("query");
                        List<IncomeExportDTO> incomeExportData = this.incomeExportData(productIncomeQuery, adminId, recordId);
                        Integer productCompanyId = productIncomeQuery.getProductCompanyId();
                        Integer incomeType = productIncomeQuery.getIncomeType();
                        if (productCompanyId == 10000) {//产品公司为翼猫科技发展（上海）有限公司
                            if (incomeType == 1) {
                                //收益类型-产品收益  商品类型-租赁商品
                                titles = new String[]{"主订单号", "订单号", "工单号", "订单来源", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "计费方式", "产品数量",
                                        "订单金额", "可分配金额", "流水号", "支付时间", "支付方式", "订单完成时间", "结算月份", "结算状态", "安装工结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区",
                                        "是否有子账号", "子账号ID", "会员用户ID", "会员用户是否享受收益", "安装工姓名", "安装工省", "安装工所在服务站公司名称", "经销商收益", "会员用户收益", "安装服务人员收益", "产品公司收益"};
                                beanProperties = new String[]{"mainOrderId", "orderId", "workOrderId", "orderSource", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "costName", "productCount",
                                        "orderFee", "settlementFee", "tradeNo", "payTime", "payType", "orderCompleteTime", "settlementMonth", "settlementStatus", "engineerSettlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                                        "hasSubAccount", "subAccountId", "saleId", "userSaleFlag", "engineerName", "engineerProvince", "engineerStationName", "distributorIncome", "saleIncome", "regionInstallerIncome", "productCompanyIncome"};
                            } else {
                                titles = new String[]{"续费单号", "工单号", "设备SN码", "设备型号", "续费时间", "支付方式", "支付时间", "流水号",
                                        "计费模板名称", "计费类型", "支付金额",
                                        "续费端", "客户姓名", "客户联系方式	", "结算月份", "结算状态", "安装工结算月份", "经销商身份", "经销商姓名",
                                        "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "享受收益的会员用户ID",
                                        "享受收益的会员用户手机号", "安装工姓名", "安装工省", "安装工市", "安装工区",
                                        "安装工所在服务站公司名称", "经销商收益", "会员用户收益", "安装服务人员收益", "区县级公司（安装工）收益", "市级公司收益",
                                        "省级公司收益", "产品公司收益"};
                                beanProperties = new String[]{"renewOrderId", "workOrderId", "snCode", "productCategory", "renewDate", "payType", "payTime", "tradeNo",
                                        "costName", "secondProductCategory", "orderFee",
                                        "renewTerminal", "waterUserName", "waterUserPhone", "settlementMonth", "settlementStatus", "engineerSettlementMonth", "distributorType", "distributorName",
                                        "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccountId", "saleId",
                                        "saleMobile", "engineerName", "engineerProvince", "engineerCity", "engineerRegion",
                                        "stationCompanyName", "distributorIncome", "saleIncome", "regionInstallerIncome", "engineerStationCompanyIncome", "cityCompanyIncome",
                                        "provincialCompanyIncome", "productCompanyIncome"};
                            }
                        } else if (productCompanyId == 20000 || productCompanyId == 30000) {//产品公司为上海养未来健康食品有限公司或者上海翼猫生物科技有限公司
                            //收益类型-产品收益  商品类型-实物商品
                            titles = new String[]{"主订单号", "订单号", "工单号", "订单来源", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量", "订单金额", "流水号", "支付时间", "支付方式",
                                    "订单完成时间", "结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "会员用户ID", "会员用户是否享受收益",
                                    "推荐人姓名", "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "推荐人所在服务站公司名称", "经销商收益", "会员用户收益", "推荐人收益", "区县级发起人收益", "区县级站长收益",
                                    "区县级公司（经销商）收益", "市级发起人收益", "市级合伙人收益", "产品公司收益"};
                            beanProperties = new String[]{"mainOrderId", "orderId", "workOrderId", "orderSource", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount", "orderFee", "tradeNo", "payTime", "payType",
                                    "orderCompleteTime", "settlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount", "saleId", "userSaleFlag",
                                    "refereeName", "refereeAccount", "refereeProvince", "refereeCity", "refereeRegion", "refereeStationName", "distributorIncome", "saleIncome", "refereeIncome", "regionSponsorIncome", "stationMasterIncome",
                                    "regionDistributorIncome", "citySponsorIncome", "cityPartnerIncome", "productCompanyIncome"};
                        } else { //产品公司为上海翼猫智能科技有限公司
                            //收益类型-产品收益  商品类型-虚拟商品
                            titles = new String[]{"主订单号", "订单号", "下单时间", "用户身份", "用户ID", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量", "订单金额", "可分配金额", "流水号", "支付时间", "支付方式",
                                    "订单完成时间", "结算月份", "经销商身份", "经销商ID", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "会员用户ID", "会员用户是否享受收益",
                                    "经销商服务站公司名称", "经销商所在服务站是否承包", "承包人姓名", "承包人手机号", "承包人身份证号", "经销商所在服务站是否享受收益", "经销商收益", "会员用户收益", "服务站承包人（产品收益）", "产品公司收益"};
                            beanProperties = new String[]{"mainOrderId", "orderId", "createTime", "userType", "userId", "firstProductCategory", "secondProductCategory", "productCategory", "productCompanyName", "productCount", "orderFee", "settlementFee", "tradeNo", "payTime", "payType",
                                    "orderCompleteTime", "settlementMonth", "distributorType", "distributorId", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion", "hasSubAccount", "subAccount", "saleId", "userSaleFlag",
                                    "distributorStationName", "distributorStationContract", "contractName", "contractPhone", "contractIdCard", "distributorStationIncome", "distributorIncome", "saleIncome", "stationContractorCompany", "productCompanyIncome"};
                        }

                        this.exportSuccessful(record, titles, beanProperties, incomeExportData);
                        break;

                    case "/order/delivery/export":
                        DeliveryConditionDTO deliveryConditionDTO = (DeliveryConditionDTO) map.get("query");
                        Integer exportType = deliveryConditionDTO.getExportType();
                        if (exportType == 1) {
                            titles = new String[]{"发货日期", "子订单号", "服务站公司名称", "快递公司", "快递单号", "收货人地址", "收货人",
                                    "收货人联系方式", "发货件数", "数量/盒", "重量", "寄付方式", "快递费", "备注"};

                            beanProperties = new String[]{"deliveryTime", "orderId", "stationCompanyName", "logisticsCompany",
                                    "logisticsNo", "addressee", "addresseeName", "addresseePhone", "num", "boxNum", "weigh", "payType",
                                    "logisticsFee", "remark"};

                        } else if (exportType == 2) {
                            titles = new String[]{"子订单号", "产品型号", "产品数量", "收货人省", "收货人市", "收货人区", "收货人街道",
                                    "收货人", "联系方式", "服务站公司名称", "备注"};

                            beanProperties = new String[]{"orderId", "productCategoryName", "count", "addresseeProvince",
                                    "addresseeCity", "addresseeRegion", "addresseeStreet", "addresseeName", "addresseePhone", "stationCompanyName", "remark"};

                        } else if (exportType == 3) {
                            titles = new String[]{"快递单号", "子订单号", "商品名称", "数量", "件数", "重量", "付款方式", "到付款",
                                    "寄件公司", "寄件人", "寄件地址", "邮寄人联系方式", "收货公司", "收货地址", "收货人", "收货人联系方式", "收货固话", "备注"};

                            beanProperties = new String[]{"logisticsNo", "orderId", "productName", "count", "num", "weigh", "payType",
                                    "logisticsFee", "sendCompany", "sender", "senderAddress", "senderPhone", "receiveCompany", "address",
                                    "addressName", "addressPhone", "phone", "remark"};
                        }
                        this.exportSuccessful(record, titles, beanProperties, this.deliveryExport(deliveryConditionDTO, adminId, recordId));
                        break;
                    //仅退款租赁商品全部-导出
                    case "/refund/rental/goods/export":
                        AfterSalesConditionDTO conditionDTO = (AfterSalesConditionDTO) map.get("query");
                        Integer rentalType = conditionDTO.getType();
                        if (rentalType == 1) {
                            titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "订单来源", "产品类目名称", "用户id",
                                    "收件人", "经销商账号", "经销商姓名", "安装工姓名", "售后申请端", "申请数量", "申请时间", "申请原因",
                                    "售后状态", "审核方", "备注"};
                            beanProperties = new String[]{"salesId", "refer", "orderId", "mainOrderId",
                                    "terminal", "productCategoryName", "userId", "addresseeName", "distributorAccount", "distributorName",
                                    "engineerName", "salesTerminal", "num", "createTime", "cancelReason", "status", "auditee", "remark"};

                        } else if (rentalType == 2) {
                            titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "订单来源", "产品类目名称", "用户id",
                                    "收件人", "经销商账号", "经销商姓名", "安装工姓名", "售后申请端", "申请数量", "申请时间", "申请原因", "备注"};
                            beanProperties = new String[]{"salesId", "refer", "orderId", "mainOrderId",
                                    "terminal", "productCategoryName", "userId", "addresseeName", "distributorAccount", "distributorName",
                                    "engineerName", "salesTerminal", "num", "createTime", "cancelReason", "remark"};
                        } else {
                            titles = new String[]{"售后单号", "工单号", "子订单号", "主订单号", "售后申请时间", "申请原因", "审核人",
                                    "审核处理时间", "审核状态", "审核不通过原因", "备注"};
                            beanProperties = new String[]{"salesId", "refer", "orderId", "mainOrderId",
                                    "createTime", "cancelReason", "buyer", "handleTime", "operationStatus", "auditReason",
                                    "remark"};
                        }
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, this.getLeaseGoodsExportData(conditionDTO, adminId, recordId));
                        break;
                    //发货列表-发货记录相关导出
                    case "/order/delivery/record/export":
                        DeliveryConditionDTO recordQuery = (DeliveryConditionDTO) map.get("query");
                        Integer recordType = recordQuery.getExportType();
                        if (recordType == 1) {
                            //发货台账登记表
                            titles = new String[]{"发货日期", "子订单号", "服务站公司名称", "快递公司", "快递单号", "收货人地址", "收货人",
                                    "收货人联系方式", "发货件数", "数量/盒", "重量", "寄付方式", "快递费", "备注"};

                            beanProperties = new String[]{"deliveryTime", "orderId", "stationCompanyName", "logisticsCompany",
                                    "logisticsNo", "addressee", "addresseeName", "addresseePhone", "count", "boxNum", "weigh", "payType",
                                    "logisticsFee", "remark"};
                        } else if (recordType == 2) {
                            titles = new String[]{"子订单号", "产品型号", "产品数量", "收货人省", "收货人市", "收货人区", "收货人街道", "收货人", "联系方式", "服务站公司名称", "备注"};
                            beanProperties = new String[]{"orderId", "productCategoryName", "count", "addresseeProvince", "addresseeCity", "addresseeRegion", "addresseeStreet", "addresseeName", "addresseePhone", "stationCompanyName", "remark"};
                        }
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, this.getDeliveryRecordExportData(recordQuery, adminId, recordId));
                        break;
                }
            } catch (Exception e) {
                log.error("======导出模板失败(recordId=" + recordId + "),异常信息===========" + e.getMessage());
                //导出失败
                record.setStatus(ExportRecordStatus.FAILURE.value);
                record.setStatusName(ExportRecordStatus.FAILURE.name);
                if (e instanceof YimaoException) {
                    record.setReason(e.getMessage());
                } else {
                    record.setReason("导出失败");
                }
                try {
                    rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
                } catch (Exception e1) {
                    log.error("===============导出失败,发送mq消息失败(recordId=" + recordId + "),异常信息=" + e1.getMessage());
                }
            }
        });
    }


    private List<OrderCheckExport> getPaycheckRecordExportData(OrderPayCheckDTO query, Integer adminId, Integer recordId) {
        try {
            List<OrderCheckExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<OrderPayCheckDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderPayCheckMapper.selectPayCheckRecordListExport(query);
                if (page.getResult() != null && page.getResult().size() > 0) {
                    List<OrderCheckExport> exportList = JSONObject.parseArray(JSONObject.toJSONString(page.getResult(), SerializerFeature.WriteDateUseDateFormat), OrderCheckExport.class);

                    list.addAll(exportList);
                }

                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条线下订单审核记录数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<OrderRenewVO> getRenewPaycheckExportData(RenewOrderQuery query, Integer adminId, Integer recordId) {
        try {
            List<OrderRenewVO> list = new ArrayList<>();
            int pageNum = 1;
            Page<OrderRenewVO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderRenewMapper.orderRenewPayCheckListExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条续费支付审核数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 开票管理导出
     *
     * @param beanProperties
     * @param titles
     */
    private SXSSFWorkbook orderInvoiceExport(OrderInvoiceQueryDTO query, Integer adminId, Integer recordId, String[] titles, String[] beanProperties) {
        try {
            int pageNum = 1;
            Page<OrderInvoiceExportDTO> page = null;
            String completeTime = "";
            String title = "发票导出";
            SXSSFWorkbook workbook = null;
            boolean isFistBatch;
            int pages = 0;
            int size = 5000;
            boolean flag = true;
            List<String> ids = new ArrayList<>();
            while (flag) {
                if (pageNum == 1) {
                    PageHelper.startPage(pageNum, size);
                    page = orderInvoiceMapper.orderInvoiceExport(query);
                    isFistBatch = true;
                    pages = page.getPages();
                } else {
                    query.setCompleteTime(completeTime);
                    query.setPageSize(size);
                    page = orderInvoiceMapper.orderInvoiceExport(query);
                    isFistBatch = false;
                }

                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    flag = false;
                    break;
                }
                workbook = ExcelUtil.generateWorkBook(workbook, distinctInvoiceData(page.getResult(), ids), title, titles.length - 1, titles, beanProperties, isFistBatch);
                completeTime = page.getResult().get(page.getResult().size() - 1).getCompleteTimeStr();
                if (pageNum < pages) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / pages * 100));
                }
                pageNum++;
            }
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List getRenewOrderExportData(RenewOrderQuery query, Integer adminId, Integer recordId) {
        try {
            List<OrderRenewExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<OrderRenewExport> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderRenewMapper.orderRenewExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条续费订单数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<RentalGoodsExportDTO> getRentalGoodsExportData(AfterSalesConditionDTO query, Integer adminId, Integer recordId) {
        List<RentalGoodsExportDTO> list = new ArrayList<>();
        Integer type = query.getType();
        int pageNum = 1;
        Page<RentalGoodsExportDTO> page = null;
        while (pageNum == 1 || page.getPages() >= pageNum) {
            PageHelper.startPage(pageNum, pageSize);
            if (type == 1) {
                page = afterSalesOrderMapper.exportAllMatterGoods(query);
            } else if (type == 2) {
                page = afterSalesOrderMapper.exportAuditMatterGoods(query);
            } else {
                page = afterSalesOrderMapper.exportMatterGoodsRecord(query);
            }
            list.addAll(page.getResult());
            if (pageNum < page.getPages()) {
                //设置下载进度
                redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
            }
            pageNum++;
        }

        return list;
    }

    /**
     * 仅退款-租赁商品导出
     */
    private List<RentalGoodsExportDTO> getLeaseGoodsExportData(AfterSalesConditionDTO query, Integer adminId, Integer recordId) {
        List<RentalGoodsExportDTO> list = new ArrayList<>();
        Integer type = query.getType();
        int pageNum = 1;
        Page<RentalGoodsExportDTO> page = null;
        while (pageNum == 1 || page.getPages() >= pageNum) {
            PageHelper.startPage(pageNum, pageSize);
            if (type == 1) {
                page = afterSalesOrderMapper.exportRentalGoods(query);
            } else if (type == 2) {
                page = afterSalesOrderMapper.exportAuditedRentalGoods(query);
            } else {
                page = afterSalesOrderMapper.exportAuditedRecord(query);
            }
            list.addAll(page.getResult());
            if (pageNum < page.getPages()) {
                //设置下载进度
                redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
            }
            pageNum++;
        }

        return list;
    }

    /**
     * 退款管理导出
     */
    private List<refundManageExportDTO> refundExportData(OrderSubDTO query, Integer adminId, Integer recordId) {
        try {
            List<refundManageExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<refundManageExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                if (query.getQueryType() == 1) {
                    query.setTypePay(1);
                } else {
                    query.setTypePay(2);
                }
                PageHelper.startPage(pageNum, pageSize);
                page = reimburseManageMapper.exportOnline(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 产品服务收益导出
     */
    private List<IncomeExportDTO> serviceIncomeExportData(ProductIncomeQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<IncomeExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<IncomeExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = serviceIncomeRecordMapper.serviceIncomeExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 产品收益明细导出
     */
    private List<IncomeExportDTO> incomeExportData(ProductIncomeQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<IncomeExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            int pages = 500;
            Page<IncomeExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                page = productIncomeRecordService.productIncomeExport(query, pageNum, pages);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private SXSSFWorkbook subOrderExportData(OrderConditionDTO query, Integer adminId, Integer recordId, String[] titles, String[] beanProperties) {
        try {
            int pageNum = 1;
            int pages = 0;
            int pageSize = 5000;
            Page<OrderExportDTO> page;
            String createTime = null;
            String title = "订单列表导出";
            SXSSFWorkbook workbook = null;
            boolean isFistBatch;
            boolean flag = true;
            List<Long> subOrderIds = new ArrayList<>();
            //根据经销商e家号，找到经销商id [查询用的是经销商e家号]
            if (query.getDistributorId() != null) {
                List<Integer> ids = userFeign.getDistributorByUserId(query.getDistributorId());
                if (CollectionUtil.isNotEmpty(ids)) {
                    query.setDistributorId(ids.get(0));
                }
            }
            while (flag) {
                if (pageNum == 1) {
                    isFistBatch = true;
                    query.setPageSize(null);
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderSubMapper.orderExportList(query);
                    pages = page.getPages();
                } else {
                    isFistBatch = false;
                    query.setCreateTime(createTime);
                    query.setPageSize(pageSize);
                    page = orderSubMapper.orderExportList(query);
                }
                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    flag = false;
                    break;
                }
                workbook = ExcelUtil.generateWorkBook(workbook, distinctSubOrderId(page.getResult(), subOrderIds), title, titles.length - 1, titles, beanProperties, isFistBatch);
                createTime = page.getResult().get(page.getResult().size() - 1).getCreateTime();
                if (pageNum < pages) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / pages * 100));
                }
                pageNum++;
            }
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List distinctSubOrderId(List<OrderExportDTO> result, List<Long> subOrderIds) {
        if (subOrderIds.isEmpty()) {
            for (OrderExportDTO order : result) {
                subOrderIds.add(order.getId());
            }
            return result;
        }

        List<OrderExportDTO> datas = new ArrayList<>();
        for (OrderExportDTO order : result) {
            if (!subOrderIds.contains(order.getId())) {
                datas.add(order);
            }
        }
        subOrderIds.clear();
        for (OrderExportDTO order : result) {
            subOrderIds.add(order.getId());
        }
        return datas;
    }


    private List<OrderCheckExport> orderMainPayCheckExport(OrderMainDTO query, Integer adminId, Integer recordId) {
        try {
            List<OrderCheckExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<OrderMainDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderMainMapper.selectPayCheckListExport(query);
                if (page.getResult() != null && page.getResult().size() > 0) {
                    List<OrderCheckExport> exportList = JSONObject.parseArray(JSONObject.toJSONString(page.getResult(), SerializerFeature.WriteDateUseDateFormat), OrderCheckExport.class);

                    list.addAll(exportList);
                }

                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private List<OrderCheckExport> orderDeliveryPayCheckListExport(WorkOrderQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<OrderCheckExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<WorkOrderDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = workOrderMapper.selectDeliveryPayCheckListExport(query);

                if (page.getResult() != null && page.getResult().size() > 0) {
                    List<OrderCheckExport> exportList = JSONObject.parseArray(JSONObject.toJSONString(page.getResult(), SerializerFeature.WriteDateUseDateFormat), OrderCheckExport.class);

                    list.addAll(exportList);
                }

                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private List<WithdrawExportDTO> withdrawListExport(WithdrawQuery query, Integer adminId, Integer recordId) {
        try {
            List<WithdrawExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WithdrawExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderWithdrawMapper.withdrawListExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<WithdrawExportDTO> withdrawAuditListExport(WithdrawQuery query, Integer adminId, Integer recordId) {
        try {
            List<WithdrawExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WithdrawExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderWithdrawMapper.withdrawAuditListExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    private List<WithdrawExportDTO> withdrawRecordDetailListExport(WithdrawQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<WithdrawExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WithdrawExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderWithdrawMapper.withdrawRecordDetailListExport(query);
                withdrawRecordDetailListExport(page);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private void withdrawRecordDetailListExport(Page<WithdrawExportDTO> page) {

        for (WithdrawExportDTO dto : page.getResult()) {

            String subAccount = dto.getSubAccount();
            if (subAccount != null) {
                // 有子账号
                dto.setHasSubAccount("是");
            } else {
                dto.setHasSubAccount("否");
            }

            Integer productType = dto.getProductType();
            String subjectCompany = dto.getSubjectCompany();
            String productCategory = dto.getProductCategory();
            if (productType != null) {
                ProductModeEnum productModeEnum = ProductModeEnum.find(productType);
                switch (productModeEnum) {
                    case REALTHING:
                        if (subjectCompany != null) {
                            dto.setPayInstructions("此款项是代" + subjectCompany + "付款");
                        }
                    case VIRTUAL:
                        if (productCategory != null) {
                            if ("Y".equals(productCategory) && subjectCompany != null) {
                                dto.setPayInstructions("此款项是代" + subjectCompany + "付款");
                            }
                            if ("M".equals(productCategory)) {
                                dto.setPayInstructions("此款项是代提供健康评估服务的服务站付款");
                            }
                        }
                        break;
                    case LEASE:
                        // 服务收益不能提现
                        break;
                    default:
                        break;
                }
            }

        }

    }

    private List<refundManageExportDTO> exportRefund(OrderSubDTO dto, Integer adminId, Integer recordId) {
        try {
            List<refundManageExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<refundManageExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = reimburseManageMapper.exportRefund(dto);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 工单列表
     */
    private SXSSFWorkbook workOrderExportData(WorkOrderQueryDTO query, Integer adminId, Integer recordId, String[] titles, String[] beanProperties) {
        try {
            long start = System.currentTimeMillis();
            query = this.workOrderQuery(query);
            int pageNum = 1;
            int pages = 0;
            int pageSize = 5000;
            Page<WorkOrderExportDTO> page = null;
            String queryTime = "";
            String title = "工单导出";
            SXSSFWorkbook workbook = null;
            boolean isFistBatch;
            boolean flag = true;
            List<String> ids = new ArrayList<>();
            while (flag) {
                //第一页查询总记录数 计算总页数来设置下载进度,后面直接查数据。
                if (pageNum == 1) {
                    isFistBatch = true;
                    page = workOrderService.exportWorkOrderList(query, pageNum, pageSize);
                    pages = page.getPages();
                } else {
                    isFistBatch = false;
                    query.setPageSize(pageSize);
                    query.setQueryTime(queryTime);
                    page = workOrderMapper.getWorkOrderListExport(query);
                }
                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    flag = false;
                    break;
                }
                workbook = ExcelUtil.generateWorkBook(workbook, distinctWorkOrderId(page.getResult(), ids), title, titles.length - 1, titles, beanProperties, isFistBatch);
                queryTime = page.getResult().get(page.getResult().size() - 1).getCreateTime();
                if (pageNum < pages) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / pages * 100));
                }
                pageNum++;

            }
            long end = System.currentTimeMillis();
            log.info("工单导出花费" + (end - start) / 1000 + "秒");
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 开票信息导出
     */
    private List<WorkOrderInvoiceExportDTO> invoiceExportData(WorkOrderQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<WorkOrderInvoiceExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WorkOrderInvoiceExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                page = workOrderService.exportWorkOrderInvoiceList(query, pageNum, pageSize);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<WithdrawExportDTO> withdrawExportData(WithdrawQueryDTO withdrawQuery, Integer adminId, Integer recordId) {
        try {
            List<WithdrawExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<WithdrawExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderWithdrawMapper.withdrawRecordListExportPage(withdrawQuery);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String[] getBilltitles(Integer mode) {
        String[] billtitles = null;
        if (mode == 0) {
            billtitles = new String[]{"主订单号", "子订单号", "云平台工单号", "订单来源", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "销售主体省市区", "销售主体公司名称", "收款说明"};

        } else if (mode == ProductModeEnum.REALTHING.value) {
            //实物(健康食品和生物科技)
            billtitles = new String[]{"主订单号", "子订单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名", "推荐人姓名",
                    "推荐人账户", "推荐人省", "推荐人市", "推荐人区", "销售主体省市区", "销售主体公司名称", "收款说明"};

        } else if (mode == ProductModeEnum.LEASE.value) {
            //租赁
            billtitles = new String[]{"主订单号", "子订单号", "工单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "计费方式", "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名"};

        } else if (mode == ProductModeEnum.VIRTUAL.value) {
            //虚拟
            billtitles = new String[]{"主订单号", "子订单号", "订单来源", "下单时间", "订单状态", "用户身份", "用户id", "产品类型", "产品范围", "产品型号", "产品公司", "产品数量",
                    "订单金额", "流水号", "支付时间", "支付方式", "经销商身份", "经销商姓名", "经销商账户", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号", "子账号姓名",
                    "销售主体省市区", "销售主体公司名称", "收款说明"};

        }
        return billtitles;
    }

    private String[] getBillbeanPropertys(Integer mode) {
        String[] billbeanPropertys = null;
        if (mode == 0) {
            billbeanPropertys = new String[]{"mainOrderId", "id", "refer", "terminal", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName", "count",
                    "fee", "tradeNo", "payTime", "payType", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

        } else if (mode == ProductModeEnum.REALTHING.value) {
            //实物(健康食品和生物科技)
            billbeanPropertys = new String[]{"mainOrderId", "id", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName", "recommendName", "recommendAccount", "recommendProvince", "recommendCity", "recommendRegion", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

        } else if (mode == ProductModeEnum.LEASE.value) {
            //租赁
            billbeanPropertys = new String[]{"mainOrderId", "id", "refer", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "costName", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName"};

        } else if (mode == ProductModeEnum.VIRTUAL.value) {
            //虚拟
            billbeanPropertys = new String[]{"mainOrderId", "id", "terminal", "createTime", "status", "userTypeName", "userId", "productFirstCategoryName", "productSecondCategoryName", "productThirdCategoryName", "productCompanyName",
                    "count", "fee", "tradeNo", "payTime", "payType", "distributorTypeName", "distributorName", "distributorAccount", "distributorProvince", "distributorCity", "distributorRegion",
                    "hasSubDistributor", "subDistributorAccount", "subDistributorName", "salesSubjectPCR", "salesSubjectCompanyName", "feeDescription"};

        }
        return billbeanPropertys;
    }

    private List<OrderBillExport> billExportData(OrderBillQuery billquery, Integer adminId, Integer recordId) {
        try {
            log.info("订单对账导出操作查询条件={}", JSONObject.toJSONString(billquery));
            List<OrderBillExport> list = new ArrayList<>();
            int pageNum = 1;
            Page<OrderBillExport> page = null;

            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = orderSubMapper.exportOrderBillPage(billquery);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            return list;
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 维护工单
     *
     * @param query
     * @param adminId
     * @param recordId
     * @return
     */
    private List<MaintenanceWorkOrderExportDTO> maintenanceWorkOrderExportData(MaintenanceWorkOrderQueryDTO query, Integer adminId, Integer recordId) {
        //设置下载进度
        redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            List<MaintenanceWorkOrderExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<MaintenanceWorkOrderExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = maintenanceWorkOrderMapper.maintenanceWorkOrderExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, df.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @return java.util.List<com.yimao.cloud.pojo.export.order.InvestmentIncomeExportDTO>
     * @description 招商收益
     * @author Liu Yi
     * @date 2019/11/27 10:57
     */
    private List<InvestmentIncomeExportDTO> investmentIncomeExportData(InvestmentIncomeQueryDTO query, Integer adminId, Integer recordId) {
        try {
            List<InvestmentIncomeExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<InvestmentIncomeExportDTO> page = null;

            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = investmentIncomeRecordMapper.exportInvestmentIncome(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发货列表中相关导出
     */
    private List<Object> deliveryExport(DeliveryConditionDTO query, Integer adminId, Integer recordId) {
        try {
            List<Object> list = new ArrayList<>();
            int pageNum = 1;
            Page<Object> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {

                if (query.getExportType() == 1) {
                    //发货台账登记列表
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderDeliveryMapper.deliveryLedgerExport(query);
                } else if (query.getExportType() == 2) {
                    //订单发货信息列表
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderDeliveryMapper.deliveryInfoExport(query);
                } else {
                    //快递信息表
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderDeliveryMapper.queryDeliveryList(query);
                }
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<DeliveryInfoExportDTO> getDeliveryRecordExportData(DeliveryConditionDTO recordQuery, Integer adminId, Integer recordId) {
        try {
            List<DeliveryInfoExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<DeliveryInfoExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                if (recordQuery.getExportType() == 1) {
                    //发货台账登记表
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderDeliveryMapper.deliveryRecordLedgerExport(recordQuery);
                } else if (recordQuery.getExportType() == 2) {
                    //订单发货信息表
                    PageHelper.startPage(pageNum, pageSize);
                    page = orderDeliveryMapper.deliveryRecordInfoExport(recordQuery);
                }
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("本次导出共查询" + list.size() + "条数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 更新导出记录
     */
    private void exportSuccessful(ExportRecordDTO record, String[] titles, String[] beanProperties, List list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new YimaoException("没有需要导出的数据");
        }
        String path = ExcelUtil.exportToFtp(list, record.getTitle(), titles.length - 1, titles, beanProperties);
        if (StringUtil.isEmpty(path)) {
            throw new YimaoException("上传导出数据到FTP服务器发生异常");
        }
        String downloadLink = domainProperties.getImage() + path;
        //导出成功
        record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
        record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
        record.setDownloadLink(downloadLink);
        rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
    }

    /**
     * 工单查询条件转换
     */
    public WorkOrderQueryDTO workOrderQuery(WorkOrderQueryDTO workOrderQueryDTO) {
        if (workOrderQueryDTO == null) {
            return null;
        }
        if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.WORK_ORDER.value) {
            //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
            if (workOrderQueryDTO.getStatus() == null || workOrderQueryDTO.getStatus() == WorkOrderStateEnum.STATUS.state) {
                workOrderQueryDTO.setStatus(null);
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.PAY.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setPay(true);
                workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.NO.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.AUDITING.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                workOrderQueryDTO.setPayStatus(PayStatus.WAITING_AUDIT.value);//原值：1
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.NOT_PASS.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state);
                workOrderQueryDTO.setPayStatus(PayStatus.FAIL.value);//原值：2
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.WAITING_PAY.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setPay(false);
                workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.NO.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.CUSTOMER_REJECT.state) {
                workOrderQueryDTO.setStatus(null);
                workOrderQueryDTO.setNotRefuse(StatusEnum.YES.value());
            } else if (workOrderQueryDTO.getStatus() == WorkOrderStateEnum.CUSTOMER_DISPATCH.state) {
                workOrderQueryDTO.setStatus(WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state);
                workOrderQueryDTO.setIsNotAllot(true);
            } else {
                workOrderQueryDTO.setStatus(workOrderQueryDTO.getStatus());
            }
        } else if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.CANCEL.value) {
            workOrderQueryDTO.setIsBackWorkOrder(StatusEnum.YES.value());
            workOrderQueryDTO.setStatus(null);
        } else if (workOrderQueryDTO.getOperationType() == WorkOrderOperationType.DELETE.value) {
            workOrderQueryDTO.setStatus(null);
        } else if (WorkOrderOperationType.find(workOrderQueryDTO.getOperationType()) == null) {
            throw new BadRequestException("没有此类型的操作！");
        }
        if (workOrderQueryDTO.getType() != null && -1 != workOrderQueryDTO.getType()) {
            if (workOrderQueryDTO.getType() == 0) {
                workOrderQueryDTO.setType(WorkOrderInstallTypeEnum.JXS_APP_ORDER.getType());
            } else {
                workOrderQueryDTO.setScanCodeType(workOrderQueryDTO.getType());
            }
        } else {
            workOrderQueryDTO.setType(null);
        }
        workOrderQueryDTO.setDelStatus("N");//业务系统查询和导出未删除的工单列表
        return workOrderQueryDTO;
    }

    /***
     * 上传导出文件并更新导出状态
     * @param record
     * @param workbook
     */
    private void uploadExportData(ExportRecordDTO record, SXSSFWorkbook workbook) {
        try {
            if (workbook == null) {
                throw new YimaoException("导出的数据不能为空");
            }
            String path = SFTPUtil.upload(workbook, record.getTitle());
            if (StringUtil.isEmpty(path)) {
                throw new YimaoException("上传导出数据到FTP服务器发生异常");
            }
            String downloadLink = domainProperties.getImage() + path;
            // 导出成功
            record.setStatus(ExportRecordStatus.SUCCESSFUL.value);
            record.setStatusName(ExportRecordStatus.SUCCESSFUL.name);
            record.setDownloadLink(downloadLink);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
        } catch (Exception e) {
            throw new YimaoException("导出的数据不能为空");
        }
    }

    /****
     * 工单导出数据去重
     * @param result
     * @param ids
     * @return
     */
    private List distinctWorkOrderId(List<WorkOrderExportDTO> result, List<String> ids) {
        if (ids.isEmpty()) {
            for (WorkOrderExportDTO workorder : result) {
                ids.add(workorder.getId());
            }
            return result;
        }

        List<WorkOrderExportDTO> datas = new ArrayList<>();
        for (WorkOrderExportDTO workorder : result) {
            if (!ids.contains(workorder.getId())) {
                datas.add(workorder);
            }
        }
        ids.clear();
        for (WorkOrderExportDTO workorder : result) {
            ids.add(workorder.getId());
        }
        return datas;
    }

    private List distinctInvoiceData(List<OrderInvoiceExportDTO> result, List<String> ids) {
        if (ids.isEmpty()) {
            for (OrderInvoiceExportDTO workorder : result) {
                ids.add(workorder.getWorkOrderId());
            }
            return result;
        }

        List<OrderInvoiceExportDTO> datas = new ArrayList<>();
        for (OrderInvoiceExportDTO workorder : result) {
            if (!ids.contains(workorder.getWorkOrderId())) {
                datas.add(workorder);
            }
        }
        ids.clear();
        for (OrderInvoiceExportDTO workorder : result) {
            ids.add(workorder.getWorkOrderId());
        }
        return datas;
    }

    /**
     * 重复数据过滤(保留)
     *
     * @param page
     * @return
     */
    private Page<OrderExportDTO> orderExportDataExclude(Page<OrderExportDTO> page) {
        //如果最后一条的时间和最后第二条的不一样。 就用上一条的时候去执行查询
        String firTime = page.getResult().get(page.getResult().size() - 1).getCreateTime();
        String secTime = page.getResult().get(page.getResult().size() - 2).getCreateTime();
        if (!firTime.equals(secTime)) {
            page.remove(page.getResult().size() - 1);
            return page;
        }
        page.remove(page.getResult().size() - 1);
        return orderExportDataExclude(page);
    }
}
