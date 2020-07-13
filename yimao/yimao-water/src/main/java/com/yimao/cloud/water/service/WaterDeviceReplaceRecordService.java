package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;

import java.util.List;

public interface WaterDeviceReplaceRecordService {

    /**
     * 查询水机耗材（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<WaterDeviceReplaceRecordDTO> page(Integer pageNum, Integer pageSize, WaterDeviceReplaceRecordQuery query);

    /**
     * 查询水机设备更换记录详情
     *
     * @param id 设备ID
     */
    WaterDeviceReplaceRecord getDetailById(Integer id);

    /**
     * 水机设备更换记录导出
     *
     * @param query 查询条件
     */
    List<DeviceReplaceRecordExport> export(WaterDeviceReplaceRecordQuery query);
}
