package com.yimao.cloud.water.controller;

import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import com.yimao.cloud.water.service.WaterDeviceReplaceRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 水机设备更换记录。
 *
 * @author Zhang Bo
 * @date 2017/12/15.
 */
@RestController
public class WaterDeviceReplaceRecordController {

    @Resource
    private WaterDeviceReplaceRecordService waterDeviceReplaceRecordService;

    /**
     * 查询水机设备更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @PostMapping(value = "/waterdevice/replacerecord/{pageNum}/{pageSize}")
    public PageVO<WaterDeviceReplaceRecordDTO> page(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                    @RequestBody WaterDeviceReplaceRecordQuery query) {
        return waterDeviceReplaceRecordService.page(pageNum, pageSize, query);
    }

    /**
     * 查询水机设备更换记录详情
     *
     * @param id 更换记录ID
     */
    @GetMapping(value = "/waterdevice/replacerecord/{id}/detail")
    public WaterDeviceReplaceRecordDTO getDetailById(@PathVariable Integer id) {
        WaterDeviceReplaceRecord replaceRecord = waterDeviceReplaceRecordService.getDetailById(id);
        if (replaceRecord == null) {
            return null;
        }
        WaterDeviceReplaceRecordDTO dto = new WaterDeviceReplaceRecordDTO();
        replaceRecord.convert(dto);
        return dto;
    }

    /**
     * 业务系统-水机物联-设备更换管理-导出
     *
     * @param query 查询条件
     */
    @PostMapping(value = "/waterdevice/replacerecord/export")
    public List<DeviceReplaceRecordExport> export(@RequestBody WaterDeviceReplaceRecordQuery query) {
        return waterDeviceReplaceRecordService.export(query);
    }

}
