package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniChoice;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MiniChoiceMapper extends Mapper<MiniChoice> {

    //获取题干列表
    List<MiniChoice> findChoiceListByEvaId(Integer evaluateId);

}
