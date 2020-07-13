package com.yimao.cloud.user.processor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.DistributorType;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.properties.DomainProperties;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.base.utils.SFTPUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.user.*;
import com.yimao.cloud.pojo.export.user.WaterDeviceUserExport;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.query.user.EngineerQuery;
import com.yimao.cloud.user.mapper.*;
import com.yimao.cloud.user.po.UserChangeRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private EngineerMapper engineerMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private DomainProperties domainProperties;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisCache redisCache;

    @Resource
    private WaterDeviceUserMapper waterDeviceUserMapper;

//    @Resource
//    private DistributorOrderAuditRecordService distributorOrderAuditRecordService;
//    @Resource
//    private SystemFeign systemFeign;

    @Resource
    private DistributorMapper distributorMapper;

    @Resource
    private FinancialAuditMapper financialAuditMapper;

    @Resource
    private UserCompanyApplyMapper userCompanyApplyMapper;

    @Resource
    private DistributorOrderAuditRecordMapper distributorOrderAuditRecordMapper;

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private UserChangeMapper userChangeMapper;

    /**
     * 导出
     */
    @RabbitListener(queues = RabbitConstant.EXPORT_ACTION_USER)
    @RabbitHandler
    public void processor(final Map<String, Object> map) {
        executor.submit(() -> {
            ExportRecordDTO record = (ExportRecordDTO) map.get("exportRecordDTO");
            String url = record.getUrl();
            Integer recordId = record.getId();
            Integer adminId = record.getAdminId();
            SXSSFWorkbook workbook = null;
            //导出中
            record.setStatus(ExportRecordStatus.IN_EXPORT.value);
            record.setStatusName(ExportRecordStatus.IN_EXPORT.name);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);

            //设置下载进度
            redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, 0);
            try {
                String[] titles;
                String[] beanProperties;
                //url区分是哪项导出
                switch (url) {
                    case "/financial/audit/export":
                        titles = new String[]{"订单号", "地区", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型",
                                "性别", "身份证号", "手机号", "推荐人姓名", "服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "财务审核状态"};
                        beanProperties = new String[]{"orderId", "address", "orderTypeStr", "distributorAccount", "realName", "distributorType", "destDistributorType",
                                "sexStr", "idCard", "phone", "recommendName", "stationCompanyName", "payTypeStr", "payStateStr", "payTimeStr", "payMoney", "orderStateStr", "financialStateStr"};
                        FinancialAuditQuery query = (FinancialAuditQuery) map.get("query");
                        List<FinancialAuditExportDTO> list = this.exportFinancialAudit(query, adminId, recordId);
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, list);
                        break;
                    case "/user/companyAudit/export":
                        titles = new String[]{"订单号", "地区", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型",
                                "性别", "身份证号", "手机号", "推荐人姓名", "服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "企业类型", "企业行业"
                                , "公司名称", "联系电话", "公司邮箱", "公司地址", "统一信用代码", "税务信息", "法人代表", "开户账号", "开户银行", "营业执照照片", "企业审核状态"};
                        beanProperties = new String[]{"orderId", "address", "orderTypeStr", "distributorAccount", "realName", "roleName", "destRoleName",
                                "sexStr", "idCard", "phone", "recommendName", "companyName", "payTypeStr", "payStateStr", "payTimeStr", "payMoney", "orderStateStr", "companyTypeStr", "industry"
                                , "companyName", "companyPhone", "companyEmail", "companyAddress", "creditCode", "taxInformation", "corporateRepresentative", "bankAccount", "bank"
                                , "businessLicense", "enterpriseStateStr"};
                        CompanyAuditQuery companyAuditQuery = (CompanyAuditQuery) map.get("query");
                        List<CompanyAuditExportDTO> companyAuditExportDTOList = exportUserCompanyApplyAudit(companyAuditQuery, adminId, recordId);
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, companyAuditExportDTOList);
                        break;
                    //导出安装工
                    case "/engineers/export":
                        titles = new String[]{"安装工账号", "安装工姓名", "身份证号", "性别", "联系方式", "服务区域（省）", "服务区域（市）",
                                "服务区域（区）", "服务站门店", "服务站公司", "创建时间", "账号状态", "绑定Iccid号"};
                        beanProperties = new String[]{"userName", "realName", "idCard", "sex", "phone", "province", "city",
                                "region", "stationName", "stationCompanyName", "createTime", "forbidden", "iccid"};
                        EngineerQuery engineerQuery = (EngineerQuery) map.get("query");
                        List<EngineerExportDTO> engineerExportList = this.getEngineerExportData(engineerQuery, adminId, recordId);
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, engineerExportList);
                        break;
                    //用户列表导出
                    case "/user/export":
                        titles = new String[]{
                                "用户ID", "昵称", "真实姓名", "性别", "年龄", "手机号", "用户身份", "客户类型", "来源端", "来源方式", "创建时间", "升级普通用户时间",
                                "变为分销用户的时间", "省", "市", "区", "详细地址", "公司名称", "公司行业", "日均服务人数", "场景标签", "健康大使ID", "健康大使身份",
                                "健康大使手机号", "经销商ID", "经销商账号", "经销商姓名", "经销商类型", "经销商省", "经销商市", "经销商区", "是否有子账号", "子账号ID",
                                "子账号用户名", "子账号姓名"};
                        beanProperties = new String[]{
                                "userId", "nickName", "realName", "sex", "age", "mobile", "userType", "customerType", "originTerminal", "origin", "createTime", "generalUserTime",
                                "beSalesTime", "province", "city", "region", "address", "companyName", "companyIndustry", "serviceNum", "sceneTag", "ambassadorId", "ambassadorUserType",
                                "ambassadorPhone", "distributorId", "distributorAccount", "distributorName", "distributorType", "distProvince", "distCity", "distRegion", "hasSubAccount",
                                "subAccountId", "subAccountUserName", "subAccountRealName"};
                        UserContidionDTO userContidionDTO = (UserContidionDTO) map.get("query");
                        List<UserExportDTO> userExportList = this.getUserExportData(userContidionDTO, adminId, recordId);
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, userExportList);
                        break;
                    case ExportUrlConstant.EXPORT_DISTRIBUTOR_AUDIT_RECORD_URL:
                        DistributorOrderAuditRecordQuery distributorOrderAuditRecordQuery = (DistributorOrderAuditRecordQuery) map.get("query");
                        List<DistributorOrderAuditRecordExportDTO> recordExportList = distributorOrderAuditRecordExport(distributorOrderAuditRecordQuery, adminId, recordId);
                        beanProperties = new String[]{"orderId", "orderType", "name", "distributorAccount", "roleName", "destRoleName", "price",
                                "payType", "payTime", "auditType", "status", "cause", "auditor", "auditTime"};
                        titles = new String[]{"订单号", "订单类型", "姓名", "经销商账号", "经销商类型", "升级经销商类型", "价格",
                                "支付方式", "支付时间", "审核类型", "审核状态", "审核不通过原因", "审核人", "审核时间"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, recordExportList);
                        break;
                    case "/distributor/order/export":
                        DistributorOrderQueryDTO distributorOrderquery = (DistributorOrderQueryDTO) map.get("query");
                        List<DistributorOrderExportDTO> distributorOrderlist = this.getDistributorOrderlist(distributorOrderquery, adminId, recordId);
                        //setStationInfo(distributorOrderlist);
                        titles = new String[]{"订单号", "经销商区域", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型", "性别", "身份证号",
                                "手机号", "推荐人姓名", "推荐人身份证号", "推荐人区域", "经销商所属服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "用户合同签署状态", "服务站合同签署状态", "翼猫合同签署状态",
                                "财务审核状态", "财务审核人", "财务审核时间", "是否创建合同", "创建时间", "财务审核次数", "流水号", "订单来源", "企业审核状态", "企业审核人", "企业审核时间", "企业名称", "完成时间"};
                        beanProperties = new String[]{"id", "area", "orderType", "distributorAccount", "name", "roleLevel", "destRoleLevel",
                                "sex", "idCard", "phone", "recommendName", "recommendIdCard", "recommendArea", "stationCompanyName", "payType", "payState", "payTime", "price", "orderState",
                                "userSignState", "stationSignState", "ymSignState", "financialState", "financialName", "financialTime", "isCreateProtocol", "createTime",
                                "financialCount", "tradeNo", "orderSouce", "enterpriseState", "enterpriseUser", "enterpriseTime", "enterpriseName", "completionTime"};
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, distributorOrderlist);
                        break;

                    //经销商导出
                    case "/distributor/export":
                        DistributorQueryDTO distributorquery = (DistributorQueryDTO) map.get("query");
                        List<DistributorExportDTO> distributorlist = this.getDistributorlist(distributorquery, adminId, recordId);
                        titles = getTitleByIsSubAccount(distributorquery.getSubAccount());
                        beanProperties = getDistributorbeanPropertys(distributorquery.getSubAccount());
                        //导出成功
                        this.exportSuccessful(record, titles, beanProperties, distributorlist);
                        break;
                    //客户列表导出
                    case "/customers/deviceUser/export":
                        titles = new String[]{
                                "联系人姓名", "性别", "手机号", "身份证号", "来源端", "创建时间", "客户类型", "公司行业", "场景标签",
                                "日均服务人数", "公司名称", "省", "市", "区", "地址", "经销商ID", "经销商账号", "经销商姓名",
                                "经销商类型", "经销商手机号", "身份证号", "经销商所属省", "经销商所属市", "经销商所属区", "经销商地址"
                        };
                        beanProperties = new String[]{
                                "realName", "sex", "phone", "idCard", "originTerminal", "createTime", "type",
                                "companyIndustry", "sceneTag", "serviceNum", "companyName", "province", "city", "region",
                                "address", "distributorUserId", "distributorAccount", "distributorName", "roleName", "distributorPhone",
                                "distributorIdCard", "distributorProvince", "distributorCity", "distributorRegion", "distributorAddress"
                        };
                        //导出成功
                        CustomerContidionDTO customerQuery = (CustomerContidionDTO) map.get("query");
                        workbook = this.customersExportData(customerQuery, adminId, recordId, titles, beanProperties);
                        this.uploadExportData(record, workbook);
                        break;
                }
            } catch (Exception e) {
                //导出失败
                record.setStatus(ExportRecordStatus.FAILURE.value);
                record.setStatusName(ExportRecordStatus.FAILURE.name);
                if (e instanceof YimaoException) {
                    record.setReason(e.getMessage());
                } else {
                    record.setReason("导出失败");
                }
                rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_RECORD, record);
            }
        });
    }

    private List<DistributorOrderAuditRecordExportDTO> distributorOrderAuditRecordExport(DistributorOrderAuditRecordQuery query, Integer adminId, Integer recordId) {
        try {
            List<DistributorOrderAuditRecordExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<DistributorOrderAuditRecordExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = distributorOrderAuditRecordMapper.distributorOrderAuditRecordExport(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出经销商订单审核记录数据" + list.size() + "条");
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
     * 企业审核导出
     */
    private List<CompanyAuditExportDTO> exportUserCompanyApplyAudit(CompanyAuditQuery query, Integer adminId, Integer recordId) {
        try {
            List<CompanyAuditExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<CompanyAuditExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = userCompanyApplyMapper.listCompanyAudit(query.getOrderId(), query.getOrderType(), query.getCompanyName(), query.getAccount(), query.getRoleLevel());
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条经销商订单企业审核数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 财务审核导出
     */
    private List<FinancialAuditExportDTO> exportFinancialAudit(FinancialAuditQuery query, Integer adminId, Integer recordId) {
        try {
            List<FinancialAuditExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<FinancialAuditExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = financialAuditMapper.listFinancialAudit(query.getDistributorOrderId(), query.getOrderType(), query.getName(), query.getDistributorAccount(),
                        query.getRoleId(), query.getDestRoleId(), query.getPayType(), query.getPayStartTime(), query.getPayEndTime());
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条经销商订单财务审核数据");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 分页查询安装工导出数据
     */
    private List<EngineerExportDTO> getEngineerExportData(EngineerQuery query, Integer adminId, Integer recordId) {
        try {
            List<EngineerExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<EngineerExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = engineerMapper.exportEngineer(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出安装工数据" + list.size() + "条");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 分页查询用户导出数据
     */
    private List<UserExportDTO> getUserExportData(UserContidionDTO query, Integer adminId, Integer recordId) {
        try {

            PageHelper.startPage(1, pageSize);
            Page<UserExportDTO> tempPage1 = userMapper.userExportList(query);
            Page<UserExportDTO> tempPage2 = userMapper.userDistributorExportList(query);
            int pages1 = tempPage1.getPages();
            int pages2 = tempPage2.getPages();
            int pages = pages1 + pages2;

            List<UserExportDTO> list = new ArrayList<>();
            //查询普通用户、分享用户、会员用户列表
            int pageNum = 1;
            Page<UserExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = userMapper.userExportList(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / pages * 100));
                }
                pageNum++;
            }
            //查询绑定了经销商的用户列表
            pageNum = 1;
            page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = userMapper.userDistributorExportList(query);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) (pageNum + pages1) / pages * 100));
                }
                pageNum++;
            }
            log.info("共导出用户数据" + list.size() + "条");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String[] getTitleByIsSubAccount(Boolean subAccount) {
        String[] titles = null;
        if (!subAccount) {
            titles = new String[]{"经销商ID", "经销商账号", "经销商姓名", "性别", "身份证号", "联系方式", "角色类型", "代理商级别", "经销商类型", "经销商省", "经销商市", "经销商区", "企业公司名称", "子账号个数", "是否为站长",
                    "是否为发起人", "是否主账号", "来源端", "来源方式", "总配额", "总置换金额", "水机剩余配额", "剩余置换金额", "支付总金额", "智慧助理", "智慧助理所在区域", "推荐人姓名",
                    "经销商账号（推荐人）", "推荐人身份证号", "推荐人所在区域", "推荐人智慧助理", "推荐人智慧助理所在地", "注册经销商时间", "免费体验首次升级时间", "升级支付时间(微创)",
                    "升级支付时间(个人)", "升级支付时间(企业)", "升级支付金额(微创)", "升级支付金额(个人)", "升级支付金额(企业)", "续费次数", "是否溢价", "省代时间", "市代时间", "区代时间", "是否是省发起人", "是否是市发起人",
                    "是否是区发起人", "是否禁用", "是否禁止下单", "创建时间"};
        } else {
            titles = new String[]{"经销商ID", "经销商账号", "经销商姓名", "性别", "身份证号", "联系方式", "经销商主账号", "主账号姓名", "经销商省", "经销商市", "经销商区",
                    "企业公司名称", "来源端", "来源方式", "是否禁用", "是否禁止下单", "创建时间"};
        }
        return titles;
    }

    private String[] getDistributorbeanPropertys(Boolean subAccount) {
        String[] beanProperties = null;
        if (!subAccount) {
            beanProperties = new String[]{"userId", "userName", "realName", "sex", "idCard", "phone", "type", "agentLevel", "roleName", "province", "city", "region", "companyName", "count", "stationMaster",
                    "sponsor", "mainAccount", "terminal", "sourceType", "quota", "replacementAmount", "remainingQuota", "remainingReplacementAmount", "amount", "userAssistant", "userAssistantArea", "recommendName",
                    "recommendAccount", "recommendIdCard", "recommendArea", "recommendAssistant", "recommendAssistantArea", "completeTime", "firstUpdateTime", "payTimeforMin",
                    "payTimeforPerson", "payTimeforEnterprise", "payAmountforMin", "payAmountforPerson", "payAmountforEnterprise", "renewalCount", "premium", "provinceTime", "cityTime", "regionTime", "isProvinceSponsor",
                    "isCitySponsor", "isRegionSponsor", "forbidden", "forbiddenOrder", "createTime"};
        } else {
            beanProperties = new String[]{"userId", "userName", "realName", "sex", "idCard", "phone", "mainUserName", "mainName", "province", "city", "region",
                    "companyName", "terminal", "sourceType", "forbidden", "forbiddenOrder", "createTime"};
        }
        return beanProperties;
    }


    private SXSSFWorkbook customersExportData(CustomerContidionDTO customerQuery, Integer adminId, Integer recordId, String[] titles, String[] beanProperties) {
        try {
            int pageNum = 1;
            int pages = 0;
            int pageSize = 5000;
            Page<WaterDeviceUserExport> page;
            String createTime = null;
            String title = "客户列表导出";
            SXSSFWorkbook workbook = null;
            boolean isFistBatch;
            boolean flag = true;
            List<Long> ids = new ArrayList<>();
            while (flag) {
                if (pageNum == 1) {
                    isFistBatch = true;
                    customerQuery.setPageSize(null); //避免前端传值
                    PageHelper.startPage(pageNum, pageSize);
                    page = waterDeviceUserMapper.customersList(customerQuery);
                    pages = page.getPages();
                } else {
                    isFistBatch = false;
                    customerQuery.setPageSize(pageSize);
                    customerQuery.setCreateTime(createTime);
                    page = waterDeviceUserMapper.customersList(customerQuery);
                }
                if (page == null || page.getResult().isEmpty() || pageNum > pages + 1) {
                    flag = false;
                    break;
                }

                workbook = ExcelUtil.generateWorkBook(workbook, distinctDeviceUserData(page.getResult(), ids), title, titles.length - 1, titles, beanProperties, isFistBatch);
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

    private List<DistributorOrderExportDTO> getDistributorOrderlist(DistributorOrderQueryDTO distributorOrderquery,
                                                                    Integer adminId, Integer recordId) {
        try {
            List<DistributorOrderExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<DistributorOrderExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = distributorOrderMapper.listOrderExport(distributorOrderquery);
                list.addAll(page.getResult());
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条经销商订单");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<DistributorExportDTO> getDistributorlist(DistributorQueryDTO distributorquery, Integer adminId,
                                                          Integer recordId) {
        try {
            convertQuery(distributorquery);
            List<DistributorExportDTO> list = new ArrayList<>();
            int pageNum = 1;
            Page<DistributorExportDTO> page = null;
            while (pageNum == 1 || page.getPages() >= pageNum) {
                PageHelper.startPage(pageNum, pageSize);
                page = distributorMapper.distributorExport(distributorquery);
                List<DistributorExportDTO> data = page.getResult();
                list.addAll(convertDistributorData(data));
                if (pageNum < page.getPages()) {
                    //设置下载进度
                    redisCache.set(Constant.EXPORT_PROGRESS + adminId + "_" + recordId, decimalFormat.format((double) pageNum / page.getPages() * 100));
                }
                pageNum++;
            }
            log.info("共导出" + list.size() + "条经销商");
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private void convertQuery(DistributorQueryDTO query) {
        Boolean isAgent = query.getIsAgent();
        Boolean isDistributor = query.getIsDistributor();
        List<Integer> ids = new ArrayList<>();
        Boolean flag = null;
        if (isAgent != null) {
            if (isDistributor != null) {//经销商代理商条件都不为空
                if (isAgent && isDistributor) {//代理商并且经销商
                    // 1,2,3
                    flag = true;
                    ids.add(DistributorType.BOTH.value);
                }
                if (!isAgent && isDistributor) {//经销商非代理商
                    // 2
                    flag = true;
                    ids.add(DistributorType.DEALER.value);

                }
                if (isAgent && !isDistributor) {//代理商非经销商
                    // 1
                    flag = true;
                    ids.add(DistributorType.PROXY.value);

                }
                if (!isAgent && !isDistributor) {//都不是
                    flag = false;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);
                }
            } else {//仅判断代理商

                if (isAgent) {
                    // 是代理商 in （1,3）
                    flag = true;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.BOTH.value);
                } else {
                    // 不是代理商 not in （1,3）
                    flag = false;
                    ids.add(DistributorType.PROXY.value);
                    ids.add(DistributorType.BOTH.value);
                }
            }
        } else {
            if (isDistributor != null) {
                // 经销商判断
                if (isDistributor) {
                    // 是经销商  in (2,3)
                    flag = true;
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);

                } else {
                    // 不是经销商 not in (2,3)
                    flag = false;
                    ids.add(DistributorType.DEALER.value);
                    ids.add(DistributorType.BOTH.value);
                }
            }
        }
        query.setFlag(flag);
        query.setTypes(ids);

    }

    private List<DistributorExportDTO> convertDistributorData(List<DistributorExportDTO> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //查询所有升级纪录
        Example example = new Example(UserChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 6);
        List<UserChangeRecord> userChangeList = userChangeMapper.selectByExample(example);
        if (Objects.isNull(userChangeList) || userChangeList.size() < 1) {
            return list;
        }
        Map<Integer, List<UserChangeRecord>> userChangeMap = new HashMap<Integer, List<UserChangeRecord>>();
        for (UserChangeRecord userChangeRecord : userChangeList) {
            if (userChangeRecord.getOrigDistributorId() != null) {
                if (userChangeMap.containsKey(userChangeRecord.getOrigDistributorId())) {
                    userChangeMap.get(userChangeRecord.getOrigDistributorId()).add(userChangeRecord);
                } else {
                    List<UserChangeRecord> changeRecordList = new ArrayList<UserChangeRecord>();
                    changeRecordList.add(userChangeRecord);
                    userChangeMap.put(userChangeRecord.getOrigDistributorId(), changeRecordList);
                }
            }

        }
        for (DistributorExportDTO export : list) {
            //获取经销商id
            Integer id = export.getId();

            if (userChangeMap.containsKey(id)) {
                //获取升级纪录
                List<UserChangeRecord> userChangeRecordList = userChangeMap.get(id);
                for (UserChangeRecord userChangeRecord : userChangeRecordList) {
                    if (Objects.nonNull(userChangeRecord.getDestDistributorType())) {
                        if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_50.value) {//体验版经销商
                            export.setFirstUpdateTime(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));

                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_350.value) {//微创
                            export.setPayTimeforMin(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforMin(userChangeRecord.getAmount());
                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_650.value) {//个人版经销商
                            export.setPayTimeforPerson(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforPerson(userChangeRecord.getAmount());
                        } else if (userChangeRecord.getDestDistributorType() == DistributorRoleLevel.D_950.value) {//企业版经销商
                            export.setPayTimeforEnterprise(userChangeRecord.getTime() == null ? null : sdf.format(userChangeRecord.getTime()));
                            export.setPayAmountforEnterprise(userChangeRecord.getAmount());
                        }
                    }

                }

            }
        }
        return list;
    }

    private void uploadExportData(ExportRecordDTO record, SXSSFWorkbook workbook) {
        try {
        	if(workbook==null){
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

    /**
     * 去重
     *
     * @param result
     * @return
     */
    private List distinctDeviceUserData(List<WaterDeviceUserExport> result, List<Long> ids) {
        if (ids.isEmpty()) {
            for (WaterDeviceUserExport user : result) {
                ids.add(user.getId());
            }
            return result;
        }

        List<WaterDeviceUserExport> datas = new ArrayList<>();
        for (WaterDeviceUserExport user : result) {
            if (!ids.contains(user.getId())) {
                datas.add(user);
            }
        }
        ids.clear();
        for (WaterDeviceUserExport user : result) {
            ids.add(user.getId());
        }
        return datas;
    }
}
