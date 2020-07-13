package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;
import com.yimao.cloud.water.service.WaterDeviceFailurePhenomenonService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维修工单故障原因
 *
 * @author Liu Yi
 * @date 2019/1/23.
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceFailurePhenomenonController")
public class WaterDeviceFailurePhenomenonController {

    @Resource
    private WaterDeviceFailurePhenomenonService waterDeviceFailurePhenomenonService;

    /**
     *
     * @param workCode
     * @return
     * @description 新增维修工单故障原因
     * @author Liu Yi
     */
    @PostMapping(value = "/waterdevice/waterDeviceFailurePhenomenon")
    @ApiOperation(value = "新增维修工单故障原因", notes = "新增维修工单故障原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode", value = "工单id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "list", value = "新增维修工单故障原因list",dataType = "WaterDeviceFailurePhenomenonDTO", allowMultiple = true, paramType = "body")
    })
        public Object create(@RequestParam("workCode") String workCode,
                             @RequestBody List<WaterDeviceFailurePhenomenonDTO> list) {

        waterDeviceFailurePhenomenonService.batchSave(list,workCode);

        return ResponseEntity.noContent().build();
    }

    /**
     *@description 查询维修工单故障原因
     * @param workCode
     * @return
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/waterDeviceFailurePhenomenon")
    @ApiOperation(value = "查询维修工单故障原因", notes = "查询维修工单故障原因")
    @ApiImplicitParam(name = "workCode", value = "工单Code", required = true, dataType = "String", paramType = "query")
    public Object getByWorkCode(@RequestParam("workCode") String workCode) {
        List<WaterDeviceFailurePhenomenonDTO> list=waterDeviceFailurePhenomenonService.getByWorkCode(workCode, WorkOrderTypeEnum.ORDER_TYPE_REPAIR);
        return ResponseEntity.ok(list);
    }

    /**
     *@description 删除维修工单故障原因
     * @param workCode
     * @return
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/waterDeviceFailurePhenomenon")
    @ApiOperation(value = "删除维修工单故障原因", notes = "删除维修工单故障原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode", value = "工单Code", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workOrderType", value = "工单类型", required = true, dataType = "String", paramType = "query")
    })
    public Object deleteWaterDeviceFailurePhenomenonByWorkCode(@RequestParam("workCode") String workCode,
                                                               @RequestParam("workOrderType")String workOrderType) {
        waterDeviceFailurePhenomenonService.delete(workCode, workOrderType);
        return ResponseEntity.noContent().build();
    }

}
