package com.yimao.cloud.system.controller.export;

import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：导出记录
 *
 * @Author Zhang Bo
 * @Date 2019/11/26
 */
@RestController
@Slf4j
@Api(tags = "ExportRecordController")
public class ExportRecordController {

    @Resource
    private ExportRecordService exportRecordService;

    /**
     * 发起导出操作后，定时器循环调用此方法
     *
     * @param id 导出记录ID
     */
    @GetMapping(value = "/export/record/{id}")
    @ApiOperation(value = "发起导出操作后，定时器循环调用此方法")
    @ApiImplicitParam(name = "id", value = "导出记录ID", dataType = "Long", paramType = "path", required = true)
    public ExportRecordDTO getById(@PathVariable Integer id) {
        return exportRecordService.getById(id);
    }

}
