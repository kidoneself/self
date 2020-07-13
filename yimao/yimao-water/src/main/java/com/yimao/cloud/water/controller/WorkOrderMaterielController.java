package com.yimao.cloud.water.controller;


import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import com.yimao.cloud.water.service.WorkOrderMaterielService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工单耗材信息
 *
 * @author Liu Yi
 * @date 2019/3/20.
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderMaterielController")
public class WorkOrderMaterielController {
    @Resource
    private WorkOrderMaterielService workOrderMaterielService;

    @PostMapping(value = "/waterdevice/workOrderMateriel")
    @ApiOperation(value = "新增维修工单耗材", notes = "新增维修工单耗材")
    @ApiImplicitParam(name = "dto", value = "维修工单耗材", required = true,dataType = "WaterDeviceWorkOrderMaterielDTO", paramType = "body")
    public Object create(@RequestBody WaterDeviceWorkOrderMaterielDTO dto) {
        try {
            workOrderMaterielService.create(dto);
        }catch (Exception e) {
            throw new YimaoException("维修工单耗材创建失败！");
        }

     return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/waterdevice/workOrderMateriel/batch")
    @ApiOperation(value = "批量新增维修工单耗材", notes = "批量新增维修工单耗材")
    @ApiImplicitParam(name = "waterDeviceWorkOrderMaterielDTOs", value = "新增维修工单耗材list", required = true,dataType = "waterDeviceWorkOrderMaterielDTO",allowMultiple=true, paramType = "body")
    public Object batchCreate(@RequestBody List<WaterDeviceWorkOrderMaterielDTO>  waterDeviceWorkOrderMaterielDTOs) {
        try {
            workOrderMaterielService.batchCreate(waterDeviceWorkOrderMaterielDTOs);
        }catch (Exception e) {
            throw new YimaoException("维修工单耗材创建失败！");
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/waterdevice/workOrderMateriel")
    @ApiOperation(value = "编辑维修工单耗材", notes = "编辑维修工单耗材")
    @ApiImplicitParam(name = "dto", value = "维修工单耗材信息", required = true, dataType = "WaterDeviceWorkOrderMaterielDTO", paramType = "body")
    public Object update(@RequestBody WaterDeviceWorkOrderMaterielDTO  dto) {
        try {
            workOrderMaterielService.update(dto);
        }catch (Exception e) {
            throw new YimaoException("维修工单耗材更新失败！");
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * @description 查询维修工单耗材列表
     * @param workCode
     * @param workCode
     * @return
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/workOrderMateriel/workCode")
    @ApiOperation(value = "查询维修工单耗材列表", notes = "查询维修工单耗材列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode",required = true, value = "工单号",dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workOrderIndex",required = true, value = "workOrderIndex",dataType = "String", paramType = "query")
    })
    public Object list(@RequestParam(value = "workCode") String workCode,
                       @RequestParam(value = "workOrderIndex") String workOrderIndex){
        List<WaterDeviceWorkOrderMaterielDTO> list=workOrderMaterielService.getWaterDeviceWorkOrderMaterielByWorkCode(workCode,workOrderIndex);

        return ResponseEntity.ok(list);
    }

    /**
     * @description 根据id查询维修工单耗材信息
     * @param id
     * @return
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/workOrderMateriel/{id}")
    @ApiOperation(value = "根据id查询维修工单耗材信息", notes = "根据id查询维修工单耗材信息")
    @ApiImplicitParam(name = "id", value = "id",dataType = "Long", paramType = "path")
    public Object getWaterDeviceWorkOrderMaterielById(@PathVariable("id")Integer id){
        WaterDeviceWorkOrderMaterielDTO waterDeviceWorkOrderMaterielDTO=workOrderMaterielService.getById(id);
        return ResponseEntity.ok(waterDeviceWorkOrderMaterielDTO);
    }

    /**
     * @description 根据id删除维修工单耗材信息
     * @param id
     * @return
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/workOrderMateriel/{id}")
    @ApiOperation(value = "根据id删除维修工单耗材信息", notes = "根据id查询维修工单耗材信息")
    @ApiImplicitParam(name = "id", value = "id",dataType = "Long", paramType = "path")
    public Object deleteById(@PathVariable("id")Integer id){
        workOrderMaterielService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * @description 根据workCode查询删除工单耗材信息
     * @param workCode
     * @param workOrderIndex
     * @return
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/workOrderMateriel/workCode")
    @ApiOperation(value = "根据workCode删除维修工单耗材信息", notes = "根据workCode删除维修工单耗材信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workCode",required = true, value = "工单id",dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "workOrderIndex",required = true, value = "workOrderIndex",dataType = "String", paramType = "query")
    })
    public Object deleteByWorkCode(@RequestParam(value = "workCode") String workCode,
                                   @RequestParam(value = "workOrderIndex") String workOrderIndex) {
         workOrderMaterielService.deleteByWorkCode(workCode,workOrderIndex);
        return ResponseEntity.noContent().build();
    }

}
