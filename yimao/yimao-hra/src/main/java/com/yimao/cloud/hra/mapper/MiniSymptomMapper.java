package com.yimao.cloud.hra.mapper;


import com.yimao.cloud.hra.po.MiniSymptom;
import com.yimao.cloud.pojo.dto.hra.MiniSymptomDTO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface MiniSymptomMapper extends Mapper<MiniSymptom> {


    List<MiniSymptom> findBodySymptom(Map<String, String> map);

    List<MiniSymptom> findAllSymptom();

    List<MiniSymptomDTO> findSymptomByFlag(Integer flag);

    MiniSymptom findSymptomName(Integer symptomId);

    MiniSymptom findZhengZhuangName(Integer aLong);

    MiniSymptom findSymptomDetail(Integer symptomId);

    List<MiniSymptom> findSymptomListBySecondId(Integer classifyId);
}
