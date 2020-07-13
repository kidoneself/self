package com.yimao.cloud.water.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import com.yimao.cloud.pojo.query.water.FilterSettingQuery;
import com.yimao.cloud.water.po.FilterSetting;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface FilterSettingMapper extends Mapper<FilterSetting> {
    Page<FilterSettingDTO> selectPage(FilterSettingQuery query);

    FilterSetting selectByPCR(@Param("province") String province,
                              @Param("city") String city,
                              @Param("region") String region,
                              @Param("deviceModel") String deviceModel);
}
