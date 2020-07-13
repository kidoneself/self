package com.yimao.cloud.system.controller;

import com.yimao.cloud.framework.aop.dto.OperationLogDTO;
import com.yimao.cloud.framework.aop.service.OperationLogService;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 描述：操作日志
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:36
 * @Version 1.0
 */
@RestController
@Api(tags = "OperationLogController")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @GetMapping(value = "/log/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询操作日志列表", notes = "查询操作日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Long", required = true, paramType = "path", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量", dataType = "Long", required = true, paramType = "path", defaultValue = "10"),
            @ApiImplicitParam(name = "operator", value = "操作人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "操作开始时间", dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "操作结束时间", dataType = "Date", paramType = "query")
    })
    public PageVO<OperationLogDTO> pageOperationLog(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                    @RequestParam(required = false) String operator,
                                                    @RequestParam(required = false) Date startTime,
                                                    @RequestParam(required = false) Date endTime) {
        return operationLogService.pageOperationLog(pageNum, pageSize, operator, startTime, endTime);
    }


}
