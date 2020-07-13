package com.yimao.cloud.system.mapper;

import com.yimao.cloud.system.po.NewAppVersion;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface NewAppVersionMapper extends Mapper<NewAppVersion> {

    NewAppVersion selectNew(@Param("systemType") Integer systemType, @Param("appType") Integer appType);

}
