package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import com.yimao.cloud.water.service.PrecisionAdvertisingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 描述：广告精准投放。
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:43
 * @Version 1.0
 */
@RestController
@Api(tags = "PrecisionAdvertisingController")
@Slf4j
public class PrecisionAdvertisingController {

    @Resource
    private PrecisionAdvertisingService precisionAdvertisingService;


    @PostMapping(value = "/precision/filter")
    @ApiOperation(value = "根据条件筛选有效的设备")
    @ApiImplicitParam(name = "dto", value = "投放条件", required = true, dataType = "ConditionalAdvertisingDTO", paramType = "body")
    public Object selectEffectiveDevice(@RequestBody ConditionalAdvertisingDTO dto) {
        ConditionalAdvertising advertising = new ConditionalAdvertising(dto);
        Set<String> list = precisionAdvertisingService.selectEffectiveDevice(advertising, dto.getSnList());
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/precision")
    @ApiOperation(value = "保存精准投放配置")
    @ApiImplicitParam(name = "dto", value = "投放条件", required = true, dataType = "ConditionalAdvertisingDTO", paramType = "body")
    public Object save(@RequestBody ConditionalAdvertisingDTO dto) {
        ConditionalAdvertising advertising = new ConditionalAdvertising(dto);
        precisionAdvertisingService.save(advertising, dto.getSnList());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/precision/device")
    @ApiOperation(value = "根据设备sn集合查询设备详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceList", value = "sn集合", allowMultiple = true, dataType = "String", paramType = "query")
    })
    public Object deviceList(@RequestParam(name = "deviceList", required = false) Set<String> deviceList) {
        return precisionAdvertisingService.deviceList(deviceList);
    }


}
