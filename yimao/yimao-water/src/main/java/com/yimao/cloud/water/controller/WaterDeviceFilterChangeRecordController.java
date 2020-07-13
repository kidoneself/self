package com.yimao.cloud.water.controller;

import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordExportDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.service.WaterDeviceFilterChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 描述：水机滤芯更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@RestController
@Api(tags = "WaterDeviceFilterChangeRecordController")
public class WaterDeviceFilterChangeRecordController {

    @Resource
    private WaterDeviceFilterChangeRecordService waterDeviceFilterChangeRecordService;

    /***
     * 功能描述:新增水机滤芯更换记录
     *
     * @param: [dto]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/filterChangeRecord")
    @ApiOperation(value = "新增水机滤芯更换记录", notes = "新增水机滤芯更换记录")
    @ApiImplicitParam(name = "dto", value = "新增水机滤芯更换记录", required = true, dataType = "WaterDeviceFilterChangeRecordDTO", paramType = "body")
    public Object create(@RequestBody WaterDeviceFilterChangeRecordDTO dto) {
        try {
            waterDeviceFilterChangeRecordService.save(dto);
        } catch (Exception e) {
            throw new YimaoException("水机滤芯更换记录创建失败！");
        }
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:查询水机滤芯更换记录列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/filterChangeRecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询水机滤芯更换记录列表", notes = "查询水机滤芯更换记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDTO", value = "查询条件", dataType = "WaterDeviceFilterChangeRecordQueryDTO", paramType = "body"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(@RequestBody WaterDeviceFilterChangeRecordQueryDTO queryDTO,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<WaterDeviceFilterChangeRecordDTO> page = waterDeviceFilterChangeRecordService.pageList(queryDTO, pageNum, pageSize);

        return ResponseEntity.ok(page);
    }

    /**
     * @param id
     * @return
     * @description 根据id查询水机滤芯更换记录
     * @author Liu Yi
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/{id}")
    @ApiOperation(value = "根据id查询水机滤芯更换记录", notes = "根据id查询水机滤芯更换记录")
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Long", paramType = "path")
    public Object getFilterChangeRecordById(@PathVariable(value = "id") Integer id) {
        WaterDeviceFilterChangeRecordDTO dto = waterDeviceFilterChangeRecordService.getFilterChangeRecordById(id);

        return ResponseEntity.ok(dto);
    }

    /***
     * 功能描述:批量记录生效状态改变
     *
     * @param: [ids, effective]
     * @auther: liu yi
     * @date: 2019/5/16 16:53
     * @return: java.lang.Object
     */
    @PatchMapping(value = "/waterdevice/filterChangeRecord")
    @ApiOperation(value = "批量记录生效状态改变")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "记录ID集合", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "effective", value = "生效状态：0-否；1-是", defaultValue = "1", dataType = "Long", paramType = "query")
    })
    public Object forbidden(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "effective", defaultValue = "1") Integer effective) {
        waterDeviceFilterChangeRecordService.forbiddenWaterDeviceFilterChangeRecord(ids, effective);
        return ResponseEntity.noContent().build();
    }

    /***
     * 功能描述:根据SN和时间查询水机滤芯更换记录
     *
     * @param: [deviceSncode, createTime]
     * @auther: liu yi
     * @date: 2019/5/18 10:10
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/sncode")
    @ApiOperation(value = "根据SN和时间查询水机滤芯更换记录", notes = "根据SN和时间查询水机滤芯更换记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceSncode", value = "SN码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "createTime", value = "开始时间", dataType = "String", paramType = "query", required = true, format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "source", value = "来源：1-安装工提交 2-客户提交 3-自动生成", dataType = "Long", paramType = "query")
    })
    public Object getFilterChangeRecordBySnCode(@RequestParam("deviceSncode") String deviceSncode,
                                                @RequestParam("createTime") Date createTime,
                                                @RequestParam(value = "source",required = false)  Integer source) {
        List<WaterDeviceFilterChangeRecordDTO> list = waterDeviceFilterChangeRecordService.getWaterDeviceFilterChangeRecordBySN(deviceSncode,createTime, source);

        return ResponseEntity.ok(list);
    }

}
