package com.yimao.cloud.user.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.pojo.dto.user.CompanyAuditExportDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.user.service.UserCompanyApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 企业前端控制器
 *
 * @author hhf
 * @date 2018/12/20
 */
@RestController
@Slf4j
@Api(tags = "UserCompanyController")
public class UserCompanyController {
    @Resource
    private UserCompanyApplyService userCompanyApplyService;
    @Resource
    private UserCache userCache;

    /**
     * 根据经销商ID查询经企业审核详情
     *
     * @param id 经销商ID
     * @return org.springframework.http.ResponseEntity
     * @author hhf
     * @date 2018/12/20
     */
    @GetMapping(value = "/user/companyApply/{id}")
    @ApiOperation(value = "根据经销商ID查询经企业审核详情", notes = "根据经销商ID查询经企业审核详情")
    @ApiImplicitParam(name = "id", value = "经销商ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity getCompanyByDistributorId(@PathVariable("id") Integer id) {
        UserCompanyApplyDTO dto = userCompanyApplyService.getCompanyByDistributorId(id);
        return ResponseEntity.ok(dto);
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
    public ResponseEntity page(@PathVariable(value = "pageNum") Integer pageNum,
                               @PathVariable(value = "pageSize") Integer pageSize,
                               @RequestParam(value = "orderId", required = false) Long orderId,
                               @RequestParam(value = "orderType", required = false) Integer orderType,
                               @RequestParam(value = "companyName", required = false) String companyName,
                               @RequestParam(value = "account", required = false) String account,
                               @RequestParam(value = "roleLevel", required = false) Integer roleLevel
    ) {
        PageVO<UserCompanyApplyDTO> dto = userCompanyApplyService.pageQuery(pageNum, pageSize, orderId, orderType, companyName, account, roleLevel);
        if (dto == null) {
            throw new NotFoundException("未找到企业审核信息");
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * 企业审核
     *
     * @param id             企业审核主键
     * @param activityStatus 1-通过 ; 2-不通过
     * @param cause 审核不通过原因
     *
     * @return java.lang.Object
     * @author hhf
     * @date 2018/12/18
     */
    @PatchMapping(value = "/user/companyApply/audit/{id}")
    @ApiOperation(value = "企业审核", notes = "企业审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "企业审核ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "activityStatus", value = "审核状态 1-通过 ; 2-不通过", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "cause", value = "审核不通过原因",  dataType = "String", paramType = "query")
    })
    public ResponseEntity companyAudit(@PathVariable(value = "id") Long id,
                                @RequestParam(value = "activityStatus") Integer activityStatus,
                                @RequestParam(value = "cause",required = false) String cause) {
        String updater = userCache.getCurrentAdminRealName();
        userCompanyApplyService.audit(updater,id, activityStatus, cause);
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
            @ApiImplicitParam(name = "cause", value = "审核不通过原因", dataType = "String", paramType = "query")
    })
    public ResponseEntity auditBatch(@RequestParam(value = "ids") List<Long> ids,
                                     @RequestParam(value = "activityStatus") Integer activityStatus,
                                     @RequestParam(value = "cause",required = false) String cause
    ) {
        String updater = userCache.getCurrentAdminRealName();
        userCompanyApplyService.auditBatch(updater,ids, activityStatus, cause);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "user/companyAudit/export")
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
                       @RequestParam(value = "roleLevel", required = false) Integer roleLevel
    ) {
        List<CompanyAuditExportDTO> list = userCompanyApplyService.exportUserCompanyApplyAudit(orderId, orderType, companyName, account, roleLevel);

        return ResponseEntity.ok(list);
    }

}
