package com.yimao.cloud.system.mapper;

import com.yimao.cloud.system.po.Reason;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReasonMapper extends Mapper<Reason> {

    List<Reason> selectByType(@Param("type") Integer type);

}
