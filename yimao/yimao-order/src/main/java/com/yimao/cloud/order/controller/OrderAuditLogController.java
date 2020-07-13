package com.yimao.cloud.order.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.order.service.OrderAuditLogService;
import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 审核记录
 * @author: yu chunlei
 * @create: 2019-08-28 11:11:41
 **/
@RestController
@Slf4j
@Api(tags = "OrderAuditLogController")
public class OrderAuditLogController {

    @Resource
    private OrderAuditLogService auditLogService;
    @Resource
    private UserCache userCache;

    //审核通过
    @PatchMapping(value = "/order/audit/record")
    public void auditAdopt(@RequestParam(name = "id") Long id){
        String updater = userCache.getCurrentAdminRealName();
        auditLogService.auditOrder(id,updater);
    }


    //仅退款订单批量：审核通过
    @PatchMapping(value = "/order/batch/audit/record")
    public void batchAuditAdopt(@RequestParam(name = "ids") List<Long> ids){
        auditLogService.batchAuditAdopt(ids);
    }


    //审核不通过
    @PatchMapping(value = "/order/audit/noPassage")
    public void auditNoPassage(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "auditReason") String auditReason,
                               @RequestParam(name = "detailReason",required = false) String detailReason){
        String updater = userCache.getCurrentAdminRealName();
        auditLogService.auditNoPassage(id,auditReason,detailReason,updater);
    }


    //仅退款订单批量：审核不通过
    @PatchMapping(value = "/order/batch/audit/noPassage")
    public void batchAuditNoPassage(@RequestParam(name = "ids") List<Long> ids,
                                    @RequestParam(name = "auditReason") String auditReason,
                                    @RequestParam(name = "detailReason") String detailReason){
        auditLogService.batchAuditNoPassage(ids,auditReason,detailReason);
    }
}
