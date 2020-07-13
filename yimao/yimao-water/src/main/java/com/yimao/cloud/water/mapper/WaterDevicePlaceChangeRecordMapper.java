package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.water.po.WaterDevicePlaceChangeRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WaterDevicePlaceChangeRecordMapper extends Mapper<WaterDevicePlaceChangeRecord> {
    WaterDevicePlaceChangeRecordDTO selectBySn(@Param("sn") String sn);

    Page<WaterDevicePlaceChangeRecordVO> selectPage(@Param("sn") String sn);
}
