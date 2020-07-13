package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.query.system.TransferOperationLogQuery;
import com.yimao.cloud.system.service.ExportRecordService;
import com.yimao.cloud.system.service.TransferOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liu Long Jie
 * @Date 2020-7-2 08:57:03
 */
@RestController
@Slf4j
@Api(tags = "TransferOperationLogController")
public class TransferOperationLogController {

    @Resource
    private TransferOperationLogService transferOperationLogService;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 售后承包转让操作日志分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/transfer/operation/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "售后承包转让操作日志分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "页数", defaultValue = "10", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询信息", dataType = "TransferOperationLogQuery", paramType = "body")
    })
    public Object page(@PathVariable Integer pageNum,
                       @PathVariable Integer pageSize,
                       @RequestBody TransferOperationLogQuery query) {

        return ResponseEntity.ok(transferOperationLogService.page(pageNum, pageSize, query));
    }

    /**
     * 导出售后承包转让操作日志
     *
     * @return
     */
    @PostMapping(value = "/transfer/operation/log/export")
    @ApiOperation(value = "导出售后承包转让操作日志")
    @ApiImplicitParam(name = "query", value = "查询信息", dataType = "TransferOperationLogQuery", paramType = "body")
    public Object export(@RequestBody TransferOperationLogQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/transfer/operation/log/export";
        ExportRecordDTO record = exportRecordService.save(url, "售后承包转让操作日志");

        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_SYSTEM, map);
        }
        return CommResult.ok(record.getId());
    }
}
