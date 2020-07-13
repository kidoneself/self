package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.WaterDeviceConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WaterDeviceConfigMapper extends Mapper<WaterDeviceConfig> {

    WaterDeviceConfig selectByDeviceModel(@Param("deviceModel") String deviceModel);

}
