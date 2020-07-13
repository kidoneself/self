package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniSickenResult;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface MiniSickenResultMapper extends Mapper<MiniSickenResult> {

    //获取患病结果列表
    List<MiniSickenResult> findResultList(Map<String, String> map);

    List<MiniSickenResult> findListBySymptomId(Integer dtoSymptomId);


}
