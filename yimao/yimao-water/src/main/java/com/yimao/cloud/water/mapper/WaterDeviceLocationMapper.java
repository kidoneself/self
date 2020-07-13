package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.WaterDeviceLocation;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

public interface WaterDeviceLocationMapper extends Mapper<WaterDeviceLocation> {

    Set<String> selectLocation();

}
