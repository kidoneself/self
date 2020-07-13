package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordExportDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordQueryDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFilterChangeRecordDTO;
import com.yimao.cloud.water.po.WaterDeviceFilterChangeRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WaterDeviceFilterChangeRecordMapper extends Mapper<WaterDeviceFilterChangeRecord> {

    Page<WaterDeviceFilterChangeRecordDTO> findPage(WaterDeviceFilterChangeRecordQueryDTO queryDTO);

    Page<WaterDeviceFilterChangeRecordExportDTO> waterDeviceFilterChangeRecordExport(WaterDeviceFilterChangeRecordQueryDTO queryDTO);


    WaterDeviceFilterChangeRecordDTO getFilterChangeRecordById(Integer id);

    Integer batchInsert(List<WaterDeviceFilterChangeRecordDTO> list);
}
