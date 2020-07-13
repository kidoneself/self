package com.yimao.cloud.water.mapper;

import com.yimao.cloud.water.po.ConditionalAdvertisingModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ConditionalAdvertisingModelMapper extends Mapper<ConditionalAdvertisingModel> {

    void batchInsert(List<ConditionalAdvertisingModel> list);

}
