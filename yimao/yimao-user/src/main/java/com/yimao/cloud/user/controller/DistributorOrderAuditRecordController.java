package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.ExcelUtil;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAuditRecordExportDTO;
import com.yimao.cloud.pojo.query.user.DistributorOrderAuditRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.service.DistributorOrderAuditRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Lizhqiang
 * @date 2018/12/21
 */
@RestController
@Slf4j
@Api(value = "distributorOrderAuditRecordController")
public class DistributorOrderAuditRecordController {


    @Resource
    private DistributorOrderAuditRecordService distributorOrderAuditRecordService;

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
    @GetMapping(value = "distributor/audit/{pageNum}/{pageSize}")
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
        DistributorOrderAuditRecordDTO query = new DistributorOrderAuditRecordDTO();
        query.setDistributorOrderId(distributorOrderId);
        query.setOrderType(orderType);
        query.setDistributorAccount(distributorAccount);
        query.setRoleId(roleId);
        query.setAuditType(auditType);
        query.setStatus(status);
        PageVO<DistributorOrderAuditRecordDTO> page = distributorOrderAuditRecordService.page(pageNum, pageSize, query);
        if (page == null) {
            throw new NotFoundException("未找到审核记录信息。");
        }
        return ResponseEntity.ok(page);
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
     * @return
     */
    @PostMapping(value = "distributor/audit/export")
    @ApiOperation(value = "审核记录导出")
    @ApiImplicitParams({@ApiImplicitParam(name = "distributorOrderId", value = "订单号", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderType", value = "订单类型",dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "distributorAccount", value = "经销商账号",  dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "原经销商类型Id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "审核类型", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "审核状态",  dataType = "Long", paramType = "query")}
    )
    public ResponseEntity exportDistributorOrderAuditRecordAudit(
            @RequestParam(value = "distributorOrderId", required = false) Long distributorOrderId,
            @RequestParam(value = "orderType", required = false) Integer orderType,
            @RequestParam(value = "distributorAccount", required = false) String distributorAccount,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "auditType", required = false) Integer auditType,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        DistributorOrderAuditRecordQuery query = new DistributorOrderAuditRecordQuery();
        query.setDistributorOrderId(distributorOrderId);
        query.setOrderType(orderType);
        query.setDistributorAccount(distributorAccount);
        query.setRoleId(roleId);
        query.setAuditType(auditType);
        query.setStatus(status);
        List<DistributorOrderAuditRecordExportDTO> list = distributorOrderAuditRecordService.exportDistributorOrderAuditRecordAudit(query);

        return ResponseEntity.ok(list);
    }


}
