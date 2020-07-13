package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 水机设备更换记录。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
@Api(tags = "WaterDeviceReplaceRecordController")
public class WaterDeviceReplaceRecordController {

    @Resource
    private WaterFeign waterFeign;

    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 业务系统-水机物联-设备更换管理-分页查询
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/waterdevice/replacerecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询水机设备更换记录（分页）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "WaterDeviceReplaceRecordQuery", paramType = "body")
    })
    public PageVO<WaterDeviceReplaceRecordDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                    @RequestBody WaterDeviceReplaceRecordQuery query) {
        return waterFeign.pageWaterDeviceReplaceRecord(pageNum, pageSize, query);
    }

    /**
     * 业务系统-水机物联-设备更换管理-详情
     *
     * @param id 更换记录ID
     */
    @GetMapping(value = "/waterdevice/replacerecord/{id}/detail")
    @ApiOperation(value = "查询水机设备更换记录详情")
    public WaterDeviceReplaceRecordDTO getDetailById(@PathVariable Integer id) {
        return waterFeign.getWaterDeviceReplaceRecordDetail(id);
    }

    /**
     * 业务系统-水机物联-设备更换管理-导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/waterdevice/replacerecord/export")
    @ApiOperation(value = "水机设备更换记录导出")
    @ApiImplicitParam(name = "query", value = "查询条件", dataType = "WaterDeviceReplaceRecordQuery", paramType = "body")
    public Object export(@RequestBody WaterDeviceReplaceRecordQuery query) {
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        String url = "/waterdevice/replacerecord/export";
        ExportRecordDTO record = exportRecordService.save(url, "水机设备更换记录");
        //导出记录添加成功，并且状态为"等待导出"，才入列
        if (record.getStatus() == ExportRecordStatus.WAITING.value) {
            //异步队列执行导出
            Map<String, Object> map = new HashMap<>();
            map.put("exportRecordDTO", record);
            map.put("query", query);
            rabbitTemplate.convertAndSend(RabbitConstant.EXPORT_ACTION_WATER, map);
        }
        return CommResult.ok(record.getId());
    }

}
