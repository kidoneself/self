package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.Tds;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TdsMapper extends Mapper<Tds> {
    Tds selectByKT(@Param("k") double k, @Param("t") double t);
}
