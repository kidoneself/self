package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.query.system.StoreHouseOperationLogQuery;
import com.yimao.cloud.system.service.StoreHouseOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@Api(tags = "StoreHouseOperationLogController")
public class StoreHouseOperationLogController {

    @Resource
    private StoreHouseOperationLogService storeHouseOperationLogService;

    /**
     * 分页库存管理操作日志
     */
    @PostMapping(value = "/store/house/operation/log/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页库存管理操作日志", notes = "分页库存管理操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "StoreHouseOperationLogQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(@RequestBody StoreHouseOperationLogQuery query,
                       @PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(storeHouseOperationLogService.page(query, pageNum, pageSize));
    }

}
