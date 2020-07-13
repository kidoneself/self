package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.pojo.dto.user.FinancialAuditDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.service.FinancialAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */
@RestController
@Api(tags = "FinancialAuditController")
public class FinancialAuditController {
    @Resource
    private FinancialAuditService financialAuditService;


    /**
     * 财务审核   状态修改
     *
     * @param orderId
     * @param financialState
     * @return
     */
    @PatchMapping(value = "distributor/financial/{orderId}")
    @ApiOperation(value = "财务审核 ", notes = "财务审核")
    @ApiImplicitParams({@ApiImplicitParam(name = "orderId", value = "订单号", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "financialState", value = "财务审核状态", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })

    public Object financialAudit(@PathVariable(value = "orderId") Long orderId,
                                 @RequestParam(value = "financialState") Integer financialState,
                                 @RequestParam(value = "cause", required = false) String cause) {
        financialAuditService.audit(orderId, financialState, cause);
        return ResponseEntity.noContent().build();

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
    @GetMapping(value = "distributor/financial/{pageNum}/{pageSize}")
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
    public Object pageFinancialAudit(@RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
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
        FinancialAuditDTO query = new FinancialAuditDTO();
        query.setDistributorOrderId(distributorOrderId);
        query.setOrderType(orderType);
        query.setName(name);
        query.setDistributorAccount(distributorAccount);
        query.setDestRoleId(destRoleId);
        query.setRoleId(roleId);
        query.setPayType(payType);
        query.setPayStartTime(payStartTime);
        query.setPayEndTime(payEndTime);

        PageVO<FinancialAuditDTO> page = financialAuditService.page(pageNum, pageSize, query);
        if (page == null) {
            throw new NotFoundException("未找到财务审核信息。");
        }
        return ResponseEntity.ok(page);
    }

    /**
     * 财务审核（批量）
     *
     * @param orderIds
     * @param activityStatus
     * @return
     */
    @PatchMapping(value = "financial/audit/batch")
    @ApiOperation(value = "财务审核（批量）", notes = "财务审核（批量）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderIds", value = "财务审核订单id", required = true, dataType = "Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "string", paramType = "query")
    })

    public ResponseEntity auditBatch(@RequestParam("orderIds") List<Long> orderIds,
                                     @RequestParam("activityStatus") Integer activityStatus,
                                     @RequestParam(value = "cause", required = false) String cause) {
        financialAuditService.auditBatch(orderIds, activityStatus, cause);
        return ResponseEntity.noContent().build();
    }

}
