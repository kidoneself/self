package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.FilterData;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface FilterDataMapper extends Mapper<FilterData> {

    FilterData selectOneByDeviceId(@Param("deviceId") Integer deviceId);

}
