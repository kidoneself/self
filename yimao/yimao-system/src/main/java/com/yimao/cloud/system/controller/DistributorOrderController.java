package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.user.CompanyAuditQuery;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.FinancialAuditDTO;
import com.yimao.cloud.pojo.dto.user.FinancialAuditQuery;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.UserFeign;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liu Yi
 * @description 经销商订单
 * @date 9:46 2019/8/20
 **/
@RestController
@Slf4j
@Api(tags = "DistributorOrderController")
public class DistributorOrderController {
    @Resource
    private UserFeign userFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 分页查询经销商订单
     *
     * @param pageNum
     * @param pageSize
     * @param query
     */
    @GetMapping(value = "/distributor/order/{pageSize}/{pageNum}")
    @ApiOperation(value = "分页查询经销商订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity page(@PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               DistributorOrderQueryDTO query) {
        PageVO<DistributorOrderDTO> page = userFeign.distributorOrderPage(pageNum, pageSize, query);
        return ResponseEntity.ok(page);
    }

    /**
     * 经销商订单详情
     *
     * @param orderId
     */
    @GetMapping(value = "/distributor/order/{orderId}")
    @ApiOperation(value = "经销商订单详情")
    @ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity findDistributorById(@PathVariable(value = "orderId") Long orderId) {
        DistributorOrderAllInfoDTO distributorById = userFeign.findDistributorOrderAllInfoById(orderId);

        return ResponseEntity.ok(distributorById);
    }

    /**
     * 待办事项统计（企业信息审核，支付审核）
     *
     * @author hhf
     * @date 2019/3/23
     */
    @GetMapping(value = "/distributor/overview")
    @ApiOperation(value = "待办事项统计（企业信息审核，支付审核）")
    public ResponseEntity distributorOrderOverview() {
        Map<String, Long> map = userFeign.distributorOrderOverview();
        return ResponseEntity.ok(map);
    }

    /**
     * @param
     * @return java.lang.Object
     * @description 导出经销商订单
     * @author Liu Yi
     * @date 2019/8/26 9:57
     */
    @PostMapping(value = "/distributor/order/export")
    @ApiOperation(value = "导出经销商订单")
    @ApiImplicitParam(name = "orderQuery", value = "订单查询实体", dataType = "DistributorOrderQueryDTO", paramType = "body")
    public Object listExport(@RequestBody DistributorOrderQueryDTO orderQuery) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/distributor/order/export";
        ExportRecordDTO record = exportRecordService.save(url, "经销商订单列表");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", orderQuery);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    	/*Object obj = userFeign.listExport(orderQuery);
    	List<DistributorOrderExportConvertDTO> list=JSONObject.parseArray(JSONObject.toJSONString(obj), DistributorOrderExportConvertDTO.class);

        if (CollectionUtil.isEmpty(list)) {
            throw new NotFoundException("未找到数据");
        }
        String header = "经销商订单列表";

        String[] beanProperties = new String[]{"id", "area", "orderType", "distributorAccount", "name", "roleLevel", "destRoleLevel",
        		"sex", "idCard", "phone", "recommendName", "stationCompanyName", "payType", "payState", "payTime", "price", "orderState",
        		"userSignState", "stationSignState", "ymSignState", "financialState", "financialName", "financialTime","isCreateProtocol", "createTime",
        		"financialCount", "tradeNo", "orderSouce", "enterpriseState", "enterpriseUser", "enterpriseTime", "enterpriseName", "completionTime"};
        String[] titles = new String[]{"订单号", "地区", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型", "性别", "身份证号",
                "手机号", "推荐人姓名", "服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "用户合同签署状态", "服务站合同签署状态", "翼猫合同签署状态",
                "财务审核状态", "财务审核人", "财务审核时间","是否创建合同","创建时间", "财务审核次数", "流水号", "订单来源", "企业审核状态", "企业审核人", "企业审核时间","企业名称","完成时间"};

        //根据省市区查询服务站名称(待商议)
        for (DistributorOrderExportConvertDTO result : list) {
			//根据省市区查询服务站名称
        	if(StringUtil.isNotBlank(result.getProvince()) && StringUtil.isNotBlank(result.getCity()) &&StringUtil.isNotBlank(result.getRegion())) {
        		StationCompanyDTO sc=stationCompanyService.getStationCompanyByPCR(result.getProvince(),result.getCity(),result.getRegion());
        		if (sc != null){
                    result.setStationCompanyName(sc.getName());
                }
        	}
        	
		}

        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        if (boo) {
            
        }*/
    }

    /**
     * 财务审核（批量）
     *
     * @param orderIds
     * @param activityStatus
     * @return
     */
    @PatchMapping(value = "/financial/audit/batch")
    @ApiOperation(value = "财务审核（批量）", notes = "财务审核（批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderIds", value = "财务审核订单id", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })

    public ResponseEntity auditBatch(@RequestParam("orderIds") List<Long> orderIds,
                                     @RequestParam("activityStatus") Integer activityStatus,
                                     @RequestParam(value = "cause", required = false) String cause) {
        userFeign.auditBatch(orderIds, activityStatus, cause);
        return ResponseEntity.noContent().build();
    }

    /**
     * 企业审核（批量）
     *
     * @param ids            企业审核主键
     * @param activityStatus 1-通过 ; 2-不通过
     * @param cause          审核不通过原因
     * @return java.lang.Object
     * @author hhf
     * @date 2018/12/18
     */
    @PatchMapping(value = "/user/companyApply/audit/batch")
    @ApiOperation(value = "企业审核（批量）", notes = "企业审核（批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "企业审核ID", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })
    public ResponseEntity CompanyAuditBatch(@RequestParam(value = "ids") List<Long> ids,
                                            @RequestParam(value = "activityStatus") Integer activityStatus,
                                            @RequestParam(value = "cause", required = false) String cause) {
        userFeign.CompanyAuditBatch(ids, activityStatus, cause);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审核记录导出
     *
     * @param distributorOrderId
     * @param orderType
     * @param distributorAccount
     * @param roleId
     * @param auditType
     * @param status
     * @param response
     * @return
     * @author Liu long jie
     * @date 2019/8/28
     */
    @PostMapping(value = "/distributor/audit/export")
    @ApiOperation(value = "审核记录导出")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "审核类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审核状态", dataType = "Long", paramType = "query")}
    )
    public Object exportDistributorOrderAuditRecordAudit(
            @RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
            @RequestParam(value = "orderType", required = false) Integer orderType,
            @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "auditType", required = false) Integer auditType,
            @RequestParam(value = "status", required = false) Integer status) {
       /* List<DistributorOrderAuditRecordExportDTO> list = userFeign.exportDistributorOrderAuditRecordAudit(distributorOrderId, orderType, distributorAccount, roleId, auditType, status);
        String header = "审核记录信息";
        String[] beanProperties = new String[]{"orderId", "address", "orderTypeStr", "distributorAccount", "realName", "distributorType", "destDistributorType",
                "sexStr", "idCard", "phone", "recommendName", "stationCompanyName", "payTypeStr", "payStateStr", "payTimeStr", "payMoney", "orderStateStr", "userSignStateStr", "stationSignStateStr"
                , "ymSignStateStr", "financialStateStr", "financialAuditor", "financialAuditTimeStr", "protocolCreatedState", "protocolCreateTimeStr", "financialAuditCount", "periodValidity"
                , "tradeNo", "orderSource", "enterpriseStateStr", "companyAuditor", "companyAuditTimeStr", "companyName", "completionTimeStr"};
        String[] titles = new String[]{"订单号", "地区", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型",
                "性别", "身份证号", "手机号", "推荐人姓名", "服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "用户合同签署状态", "服务站签署状态", "翼猫签署状态", "财务审核状态"
                , "财务审核人", "财务审核时间", "合同是否创建", "创建时间", "财务审核次数", "有效剩余时间", "流水号", "订单来源", "企业审核状态", "企业审核人", "企业审核时间", "企业名称", "完成时间"
        };
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);
        if (!boo) {
            throw new YimaoException("导出失败");
        }*/

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_DISTRIBUTOR_AUDIT_RECORD_URL, "经销商订单审核记录");
        DistributorOrderAuditRecordQuery query = new DistributorOrderAuditRecordQuery();
        query.setAuditType(auditType);
        query.setDistributorAccount(distributorAccount);
        query.setDistributorOrderId(distributorOrderId);
        query.setOrderType(orderType);
        query.setRoleId(roleId);
        query.setStatus(status);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    }

    /**
     * 财务审核导出
     *
     * @param distributorOrderId
     * @param orderType
     * @param name
     * @param distributorAccount
     * @param roleId
     * @param destRoleId
     * @param payType
     * @param payStartTime
     * @param payEndTime
     * @return
     * @author liulongjie
     * @date 2019/9/26
     */
    @PostMapping(value = "/financial/audit/export")
    @ApiOperation(value = "财务审核导出")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "经销商姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "destRoleId", value = "变更后经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payType", value = "支付方式", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付结束时间", dataType = "String", paramType = "query")
    })
    public Object exportFinancialAudit(@RequestParam(required = false) Long distributorOrderId,
                                       @RequestParam(required = false) Integer orderType,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String distributorAccount,
                                       @RequestParam(required = false) Integer roleId,
                                       @RequestParam(required = false) Integer destRoleId,
                                       @RequestParam(required = false) Integer payType,
                                       @RequestParam(required = false) String payStartTime,
                                       @RequestParam(required = false) String payEndTime) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/financial/audit/export";
        ExportRecordDTO record = exportRecordService.save(url, "经销商订单财务审核列表");

        FinancialAuditQuery query = new FinancialAuditQuery();
        query.setDistributorOrderId(distributorOrderId);
        query.setOrderType(orderType);
        query.setName(name);
        query.setDistributorAccount(distributorAccount);
        query.setRoleId(roleId);
        query.setDestRoleId(destRoleId);
        query.setPayType(payType);
        query.setPayStartTime(payStartTime);
        query.setPayEndTime(payEndTime);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
    }

    @PostMapping(value = "/user/companyAudit/export")
    @ApiOperation(value = "企业审核导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "企业名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleLevel", value = "经销商类型", dataType = "Long", paramType = "query")
    })
    public Object exportCompanyAudit(@RequestParam(value = "orderId", required = false) Long orderId,
                                     @RequestParam(value = "orderType", required = false) Integer orderType,
                                     @RequestParam(value = "companyName", required = false) String companyName,
                                     @RequestParam(value = "account", required = false) String account,
                                     @RequestParam(value = "roleLevel", required = false) Integer roleLevel) {

        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/user/companyAudit/export";
        ExportRecordDTO record = exportRecordService.save(url, "企业审核列表");

        CompanyAuditQuery query = new CompanyAuditQuery();
        query.setOrderId(orderId);
        query.setOrderType(orderType);
        query.setCompanyName(companyName);
        query.setAccount(account);
        query.setRoleLevel(roleLevel);

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_USER, map);
        }
        return CommResult.ok(record.getId());
   /*
        List<CompanyAuditExportDTO> list = userFeign.exportCompanyAudit(orderId, orderType, companyName, account, roleLevel);
        String header = "企业审核信息";
        String[] beanProperties = new String[]{"orderId", "address", "orderTypeStr", "distributorAccount", "realName", "roleName", "destRoleName",
                "sexStr", "idCard", "phone", "recommendName", "companyName", "payTypeStr", "payStateStr", "payTimeStr", "payMoney", "orderStateStr", "companyTypeStr", "industry"
                , "companyName", "companyPhone", "companyEmail", "companyAddress", "creditCode", "taxInformation", "corporateRepresentative", "bankAccount", "bank"
                , "businessLicense", "enterpriseStateStr"};
        String[] titles = new String[]{"订单号", "地区", "订单类型", "经销商账户", "经销商姓名", "经销商类型", "升级经销商类型",
                "性别", "身份证号", "手机号", "推荐人姓名", "服务站公司名称", "支付方式", "支付状态", "支付时间", "支付金额", "订单状态", "企业类型", "企业行业"
                , "公司名称", "联系电话", "公司邮箱", "公司地址", "统一信用代码", "税务信息", "法人代表", "开户账号", "开户银行", "营业执照照片", "企业审核状态"
        };
        boolean boo = ExcelUtil.exportSXSSF(list, header, titles.length - 1, titles, beanProperties, response);*/
    }

    @GetMapping(value = "/user/companyApply/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询企业审核", notes = "分页查询企业审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "orderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "companyName", value = "企业名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleLevel", value = "经销商类型", dataType = "Long", paramType = "query")
    })
    public ResponseEntity pageCompanyAudit(@PathVariable(value = "pageNum") Integer pageNum,
                                           @PathVariable(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "orderId", required = false) Long orderId,
                                           @RequestParam(value = "orderType", required = false) Integer orderType,
                                           @RequestParam(value = "companyName", required = false) String companyName,
                                           @RequestParam(value = "account", required = false) String account,
                                           @RequestParam(value = "roleLevel", required = false) Integer roleLevel
    ) {
        PageVO<UserCompanyApplyDTO> page = userFeign.pageCompanyAudit(pageNum, pageSize, orderId, orderType, companyName, account, roleLevel);
        return ResponseEntity.ok(page);
    }

    /**
     * 财务审核分页   可以带条件
     *
     * @param distributorOrderId
     * @param orderType
     * @param name
     * @param distributorAccount
     * @param roleId
     * @param destRoleId
     * @param payType
     * @param payStartTime
     * @param payEndTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/distributor/financial/{pageNum}/{pageSize}")
    @ApiOperation(value = "财务审核分页", notes = "财务审核分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "经销商姓名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "destRoleId", value = "变更后经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payType", value = "支付方式", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, defaultValue = "10", dataType = "Long", paramType = "path")
    })
    public ResponseEntity pageFinancialAudit(@RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
                                             @RequestParam(value = "orderType", required = false) Integer orderType,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                             @RequestParam(value = "roleId", required = false) Integer roleId,
                                             @RequestParam(value = "destRoleId", required = false) Integer destRoleId,
                                             @RequestParam(value = "payType", required = false) Integer payType,
                                             @RequestParam(value = "payStartTime", required = false) String payStartTime,
                                             @RequestParam(value = "payEndTime", required = false) String payEndTime,
                                             @PathVariable(value = "pageNum") Integer pageNum,
                                             @PathVariable(value = "pageSize") Integer pageSize
    ) {
        PageVO<FinancialAuditDTO> page = userFeign.pageFinancialAudit(distributorOrderId, orderType, name, distributorAccount, roleId, destRoleId, payType, payStartTime, payEndTime, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * 审核记录分页
     *
     * @param pageNum
     * @param pageSize
     * @param distributorOrderId
     * @param orderType
     * @param distributorAccount
     * @param roleId
     * @param auditType
     * @param status
     * @return
     */
    @GetMapping(value = "/distributor/audit/{pageNum}/{pageSize}")
    @ApiOperation(value = "审核记录分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "审核类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审核状态", dataType = "Long", paramType = "query")
    })
    public ResponseEntity pageDistributorAuditRecord(@PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                     @RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
                                                     @RequestParam(value = "orderType", required = false) Integer orderType,
                                                     @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
                                                     @RequestParam(value = "roleId", required = false) Integer roleId,
                                                     @RequestParam(value = "auditType", required = false) Integer auditType,
                                                     @RequestParam(value = "status", required = false) Integer status) {
        PageVO<DistributorOrderAuditRecordDTO> page = userFeign.pageDistributorAuditRecord(pageNum, pageSize, distributorOrderId, orderType, distributorAccount, roleId, auditType, status);
        return ResponseEntity.ok(page);
    }

    /**
     * 财务审核   状态修改
     *
     * @param orderId
     * @param financialState
     * @param cause
     * @return
     */
    @PatchMapping(value = "/distributor/financial/{orderId}")
    @ApiOperation(value = "财务审核 ", notes = "财务审核")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "financialState", value = "财务审核状态", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })

    public Object financialAudit(@PathVariable(value = "orderId") Long orderId,
                                 @RequestParam(value = "financialState") Integer financialState,
                                 @RequestParam(value = "cause", required = false) String cause) {
        userFeign.financialAudit(orderId, financialState, cause);
        return ResponseEntity.noContent().build();


    }

    /**
     * 企业审核
     *
     * @param id             企业审核主键
     * @param activityStatus 1-通过 ; 2-不通过
     * @param cause          审核不通过原因
     * @author liulongjie
     * @date 2019/8/29
     */
    @PatchMapping(value = "/user/companyApply/audit/{id}")
    @ApiOperation(value = "企业审核", notes = "企业审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "企业审核ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public ResponseEntity companyAudit(@PathVariable(value = "id") Long id,
                                       @RequestParam(value = "activityStatus") Integer activityStatus,
                                       @RequestParam(value = "cause", required = false) String cause) {
        userFeign.companyAudit(id, activityStatus, cause);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查看合同页
     *
     * @param distributorOrderId
     * @return
     */
    @GetMapping(value = "/distributor/protocol/view/{distributorOrderId}")
    @ApiOperation(value = "查看合同页 ", notes = "查看合同页")
    @ApiImplicitParam(name = "distributorOrderId", value = "distributorOrderId", required = true, dataType = "Long", paramType = "path")
    public Object previewDistributorProtocol(@PathVariable(value = "distributorOrderId") Long distributorOrderId) {
        Map<String, String> resultMap = new HashMap<>();
        String url = userFeign.previewDistributorProtocol(distributorOrderId);
        resultMap.put("url", url);
        return ResponseEntity.ok(resultMap);
    }
}
