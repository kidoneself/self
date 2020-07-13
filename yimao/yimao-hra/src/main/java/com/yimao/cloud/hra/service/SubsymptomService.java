package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.MiniSubsymptom;
import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import com.yimao.cloud.pojo.dto.hra.SubSymptomDTO;
import com.yimao.cloud.pojo.dto.hra.SymptomAndSubsymDTO;
import java.util.List;
import java.util.Map;

/**
 * 子症状 api
 */
public interface SubsymptomService {
    /**
     *
     * @param
     * @return
     */
    List<MiniSubsymptom> findSubsymptomList(Map<String, String> map);

    /**
     * 根据标识获取热门搜索列表
     * @param flag
     * @return
     */
    SymptomAndSubsymDTO findHotList(Integer flag);

    /**
     * 根据子症状得到患病结果
     * @param subsymId
     * @return
     */
    SubSymptomDTO findResultBySubsymId(Integer subsymId);

    /**
     * 根据输入关键词 搜索结果
     * @param keyWords
     * @return
     */
    MiniSubsymptomDTO getSearchResult(String keyWords);


}
