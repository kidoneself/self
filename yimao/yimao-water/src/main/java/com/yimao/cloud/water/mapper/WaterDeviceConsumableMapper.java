package com.yimao.cloud.water.mapper;

import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WaterDeviceConsumableMapper extends Mapper<WaterDeviceConsumable> {
    List<WaterDeviceConsumableDTO> listByDeviceModelForFilterSetting(@Param("deviceModel") String deviceModel);
}
