package com.yimao.cloud.water.controller;


import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.water.WaterDeviceRepairFactFaultDescribeInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceRepairFactFaultDescribeInfo;
import com.yimao.cloud.water.service.WaterDeviceRepairFactFaultDescribeInfoService;
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
 * 维修工单解决措施
 *
 * @author Liu Yi
 * @date 2019/3/20.
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceRepairFactFaultDescribeInfoController")
public class WaterDeviceRepairFactFaultDescribeInfoController {
    @Resource
    private WaterDeviceRepairFactFaultDescribeInfoService waterDeviceRepairFactFaultDescribeInfoService;

    @PostMapping(value = "/waterdevice/repairFactFaultDescribeInfo")
    @ApiOperation(value = "新增维修工单解决措施", notes = "新增维修工单解决措施")
    @ApiImplicitParam(name = "dto", value = "解决措施", dataType = "waterDeviceRepairFactFaultDescribeInfoDTO", paramType = "body")
    public Object create(@RequestBody WaterDeviceRepairFactFaultDescribeInfoDTO dto) {
        try {
            waterDeviceRepairFactFaultDescribeInfoService.create(dto);
        } catch (Exception e) {
            throw new YimaoException("维修工单创建失败！");
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * @param pageNum
     * @param pageSize
     * @return
     * @description 查询维修工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/repairFactFaultDescribeInfo/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询维修工单列表", notes = "查询维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workCode", value = "工单ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "Long", paramType = "path")
    })
    @Deprecated
    public Object page(@RequestParam(value = "id", required = false) Integer id,
                       @RequestParam(value = "workCode", required = false) String workCode,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<WaterDeviceRepairFactFaultDescribeInfoDTO> page = waterDeviceRepairFactFaultDescribeInfoService.page(pageNum, pageSize, id, workCode);

        return ResponseEntity.ok(page);
    }

    /**
     * @return
     * @description 查询维修工单列表
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/repairFactFaultDescribeInfo/list")
    @ApiOperation(value = "查询维修工单列表", notes = "查询维修工单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "workCode", value = "工单ID", dataType = "String", paramType = "query")
    })
    public Object list(@RequestParam(value = "id", required = false) Integer id,
                       @RequestParam(value = "workCode", required = false) String workCode) {
        List<WaterDeviceRepairFactFaultDescribeInfoDTO> list = waterDeviceRepairFactFaultDescribeInfoService.list(id, workCode);

        return ResponseEntity.ok(list);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询维修工单
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/repairFactFaultDescribeInfo/{id}")
    @ApiOperation(value = "根据id查询维修工单", notes = "根据id查询维修工单")
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Long", paramType = "path")
    public Object getById(@PathVariable("id") Integer id) {
        WaterDeviceRepairFactFaultDescribeInfo repairFactFaultDescribeInfo = waterDeviceRepairFactFaultDescribeInfoService.getById(id);

        if(repairFactFaultDescribeInfo==null){
            return ResponseEntity.noContent().build();
        }

        WaterDeviceRepairFactFaultDescribeInfoDTO dto=new WaterDeviceRepairFactFaultDescribeInfoDTO();
        repairFactFaultDescribeInfo.convert(dto);
        return ResponseEntity.ok(dto);
    }

    /**
     * @param id
     * @return
     * @description 删除
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/repairFactFaultDescribeInfo/{id}")
    @ApiOperation(value = "删除", notes = "删除")
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Long", paramType = "path")
    public Object delete(@PathVariable(value = "id") Integer id) {
        waterDeviceRepairFactFaultDescribeInfoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * @param workCode
     * @return
     * @description 根据工单号删除解决措施
     * @author Liu Yi
     */
    @DeleteMapping(value = "/waterdevice/repairFactFaultDescribeInfo")
    @ApiOperation(value = "根据工单号删除解决措施", notes = "根据工单号删除解决措施")
    @ApiImplicitParam(name = "workCode", required = true, value = "工单id", dataType = "String", paramType = "query")
    public Object deleteByWorkCode(@RequestParam(value = "workCode") String workCode) {
        waterDeviceRepairFactFaultDescribeInfoService.deleteByWorkCode(workCode);
        return ResponseEntity.noContent().build();
    }
}
