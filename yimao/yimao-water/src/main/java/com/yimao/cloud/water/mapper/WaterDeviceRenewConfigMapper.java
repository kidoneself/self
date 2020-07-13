package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.WaterDeviceRenewConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface WaterDeviceRenewConfigMapper extends Mapper<WaterDeviceRenewConfig> {

    WaterDeviceRenewConfig selectByType(@Param("type") Integer type);

}
