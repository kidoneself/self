package com.yimao.cloud.water.mapper;

import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingAreaDTO;
import com.yimao.cloud.water.po.ConditionalAdvertisingArea;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ConditionalAdvertisingAreaMapper extends Mapper<ConditionalAdvertisingArea> {

    void batchInsert(List<ConditionalAdvertisingArea> list);

    List<ConditionalAdvertisingAreaDTO> selectListByCondition(@Param("advertisingId") Integer advertisingId);
}
