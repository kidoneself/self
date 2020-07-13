package com.yimao.cloud.system.mapper;

import com.yimao.cloud.system.po.AppUrl;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AppUrlMapper extends Mapper<AppUrl> {

    AppUrl selectOneByVersion(@Param("version") Integer version);

    AppUrl selectOneByVersionIsNull();

}
