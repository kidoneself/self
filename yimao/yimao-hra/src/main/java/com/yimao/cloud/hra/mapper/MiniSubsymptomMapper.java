package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniSubsymptom;
import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;
import java.util.Map;

public interface MiniSubsymptomMapper extends Mapper<MiniSubsymptom> {


    List<MiniSubsymptom> findSubsymptomList(Map<String, String> map);

    //根据结果获取症状
    List<MiniSubsymptom> findListByResultId(Integer resultId);

    //热门搜索
    List<MiniSubsymptomDTO> findHotList(Integer flag);

    MiniSubsymptom getSearchResult(Map<String, String> map);


    List<MiniSubsymptom> findListBySymId(Long symptomId);
}
