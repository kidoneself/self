package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WaterDeviceReplaceRecordMapper extends Mapper<WaterDeviceReplaceRecord> {
    Page<WaterDeviceReplaceRecordDTO> selectPage(WaterDeviceReplaceRecordQuery query);

    Page<DeviceReplaceRecordExport> export(WaterDeviceReplaceRecordQuery query);

    WaterDeviceReplaceRecord selectOneByNewSn(@Param("newSn") String newSn);
}
