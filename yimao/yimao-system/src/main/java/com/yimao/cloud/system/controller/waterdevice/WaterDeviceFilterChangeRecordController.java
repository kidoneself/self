package com.yimao.cloud.system.controller.waterdevice;

import com.yimao.cloud.base.constant.ExportUrlConstant;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ExportRecordStatus;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.WaterFeign;
import com.yimao.cloud.system.service.ExportRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 维护记录
 *
 * @author Liu Yi
 * @date 2019/4/1.
 */
@RestController
@Slf4j
@Api(tags = "WaterDeviceFilterChangeRecordController")
public class WaterDeviceFilterChangeRecordController {
    @Resource
    private WaterFeign waterFeign;
    @Resource
    private ExportRecordService exportRecordService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /***
     * 功能描述:查询水机滤芯更换记录列表
     *
     * @param: [queryDTO, pageNum, pageSize]
     * @auther: liu yi
     * @date: 2019/5/15 14:24
     * @return: java.lang.Object
     */
    @GetMapping(value = "/waterdevice/filterChangeRecord/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询水机滤芯更换记录列表", notes = "查询水机滤芯更换记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(WaterDeviceFilterChangeRecordQueryDTO queryDTO,
                       @PathVariable("pageNum") Integer pageNum,
                       @PathVariable("pageSize") Integer pageSize) {
        PageVO<WaterDeviceFilterChangeRecordDTO> page =waterFeign .waterDeviceFilterChangeRecordist(queryDTO, pageNum, pageSize);

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
    @ApiImplicitParam(name = "id", required = true, value = "id", dataType = "Integer", paramType = "path")
    public Object getWaterDeviceFilterChangeRecordById(@PathVariable("id") Integer id) {
        WaterDeviceFilterChangeRecordDTO dto = waterFeign.getWaterDeviceFilterChangeRecordById(id);

        return ResponseEntity.ok(dto);
    }

    /***
     * 功能描述:根据条件查询导出水机滤芯更换记录
     *
     * @param: [queryDTO, response]
     * @auther: liu yi
     * @date: 2019/5/15 14:22
     * @return: java.lang.Object
     */
    @PostMapping(value = "/waterdevice/filterChangeRecord/export")
    @ApiOperation(value = "根据条件查询导出水机滤芯更换记录", notes = "根据条件查询导出水机滤芯更换记录")
    public Object exportWaterDeviceFilterChangeRecord(WaterDeviceFilterChangeRecordQueryDTO query) {
       /* List<WaterDeviceFilterChangeRecordExportDTO> dtoList=waterFeign.exportWaterDeviceFilterChangeRecord(queryDTO);
        
        String header = "滤芯维护记录_" + DateUtil.getCurrentTimeStr(DateUtil.DATEFORMAT_01);
        String[] beanPropertys = new String[]{ "maintenanceWorkOrderId", "province", "city", "region", "address", "filterName", "consumerName", "consumerPhone", "deviceBatchCode", "deviceModelName", "deviceSncode", "sourceTxt", "effectiveTxt", "createTimeTxt","activatingTime","deviceScope","deviceSimcard"};
        String[] titles = new String[]{"维护工单号", "省", "市", "区", "详细地址", "滤芯类型", "客户姓名", "客户电话", "批次码", "设备类型", "SN码", "来源", "是否有效", "更换时间","设备激活时间","产品范围","设备ICCID"};
        boolean boo = ExcelUtil.exportSXSSF(dtoList, header, titles.length - 1, titles, beanPropertys, response);
        if (!boo) {
            throw new YimaoException("导出滤芯维护记录失败!");
        }
*/
        //保存导出记录，如果短时间操作多次，会提示操作过于频繁
        ExportRecordDTO record = exportRecordService.save(ExportUrlConstant.EXPORT_FILTERCHANGERECORD_URL, "滤芯维护记录");

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
            @ApiImplicitParam(name = "ids", value = "记录ID集合", required = true, dataType = "Integer", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "effective", value = "生效状态：0-否；1-是", defaultValue = "1", dataType = "Long", paramType = "query")
    })
    public Object forbidden(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "effective", defaultValue = "1") Integer effective) {
        waterFeign.forbiddenWaterDeviceFilterChangeRecord(ids, effective);
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
            @ApiImplicitParam(name = "createTime", value = "开始时间", dataType = "String", paramType = "query", required = true,format = "yyyy-MM-dd HH:mm:ss")
    })
    public Object getWaterDeviceFilterChangeRecordBySnCode(@RequestParam("deviceSncode") String deviceSncode,
                       @RequestParam("createTime") Date createTime) {
        List<WaterDeviceFilterChangeRecordDTO> list = waterFeign.getWaterDeviceFilterChangeRecordBySnCode(deviceSncode,createTime,2);

        return ResponseEntity.ok(list);
    }
}
