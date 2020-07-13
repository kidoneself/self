package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceOnlineDTO;
import com.yimao.cloud.pojo.dto.hra.HraDeviceQuery;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.HraFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: HRA设备模块
 * @author: yu chunlei
 * @create: 2019-02-12 10:35:16
 **/
@RestController
@Slf4j
@Api(tags = "HraDeviceController")
public class HraDeviceController {

    @Resource
    private HraFeign hraFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * HRA设备管理
     *
     * @param pageNum  第几页
     * @param pageSize 每页显示条数
     * @Description: HRA评估-HRA设备管理
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/14 10:40
     */
    @PostMapping(value = {"/device/{pageNum}/{pageSize}"})
    @ApiOperation(value = "HRA设备管理", notes = "HRA设备管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", value = "设备查询", dataType = "HraDeviceQuery", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "分页页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public ResponseEntity<PageVO<HraDeviceDTO>> queryStationOnline(@RequestBody(required = false) HraDeviceQuery query,
                                                                   @PathVariable(value = "pageNum") Integer pageNum,
                                                                   @PathVariable(value = "pageSize") Integer pageSize) {
        PageVO<HraDeviceDTO> json = hraFeign.queryStationOnline(query, pageNum, pageSize);
        return ResponseEntity.ok(json);
    }


    /**
     * @Description: HRA设备管理-设备上线
     * @author ycl
     * @param: hraCardDTO
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/18 10:05
     */
    @PostMapping(value = {"/device/online"})
    @ApiOperation(value = "HRA设备管理-设备上线", notes = "HRA设备管理-设备上线")
    @ApiImplicitParam(name = "hraDeviceOnlineDTO", value = "设备信息", required = true, dataType = "HraDeviceOnlineDTO", paramType = "body")
    public ResponseEntity saveStationOnline(@RequestBody HraDeviceOnlineDTO hraDeviceOnlineDTO) {
        hraFeign.saveStationOnline(hraDeviceOnlineDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 删除设备
     * @author ycl
     * @param: id
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/21 9:42
     */
    @DeleteMapping("/device/{id}")
    @ApiOperation(value = "删除设备", notes = "删除设备")
    @ApiImplicitParam(name = "id", value = "设备ID", required = true, dataType = "Long", paramType = "path")
    public ResponseEntity deleteDevice(@PathVariable(value = "id") Integer id) {
        hraFeign.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 修改设备上下线
     * @author ycl
     * @param: * @param onlineId
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/2/21 10:01
     */
    @PutMapping("/device")
    @ApiOperation(value = "修改设备上下线", notes = "修改设备上下线")
    @ApiImplicitParam(name = "hraDeviceOnlineDTO", value = "设备信息", required = true, dataType = "HraDeviceOnlineDTO", paramType = "body")
    public ResponseEntity updateDevice(@RequestBody HraDeviceOnlineDTO hraDeviceOnlineDTO) {
        hraFeign.updateDevice(hraDeviceOnlineDTO);
        return ResponseEntity.noContent().build();
    }


    /**
     * @Description: 设备管理-批量删除功能
     * @author ycl
     * @param: * @param ids
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/3/1 10:23
     */
    @DeleteMapping("/device")
    @ApiOperation(value = "批量删除功能", notes = "批量删除功能")
    @ApiImplicitParam(name = "ids", value = "设备ID数组集", required = true, paramType = "query", allowMultiple = true, dataType = "String")
    public ResponseEntity batchDelete(@RequestParam("ids") Integer[] ids) {
        hraFeign.batchDelete(ids);
        return ResponseEntity.noContent().build();
    }


    /**
     * HRA设备管理导出
     *
     * @Description: HRA评估-HRA设备管理
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/1/12 17:03
     */
    @PostMapping(value = {"/device/export"})
    @ApiOperation(value = "HRA设备管理导出", notes = "HRA设备管理导出")
    @ApiImplicitParam(name = "dto", value = "设备查询", dataType = "HraDeviceQuery", paramType = "body")
    public Object exportDevice(@RequestBody(required = false) HraDeviceQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/device/export";
        ExportRecordDTO record = exportRecordService.save(url, "HRA设备管理");
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_HRA, map);
        }
        return CommResult.ok(record.getId());
    }
}
