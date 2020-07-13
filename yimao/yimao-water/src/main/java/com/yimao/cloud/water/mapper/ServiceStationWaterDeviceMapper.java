package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ServiceStationWaterDeviceMapper extends Mapper<ServiceStationWaterDevice> {

    Integer selectCountByCondition(@Param("areaList") List<Map<String, String>> areaList,
                                   @Param("models") List<String> models,
                                   @Param("connectionType") Integer connectionType);

}
