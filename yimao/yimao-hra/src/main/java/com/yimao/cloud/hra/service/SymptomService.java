package com.yimao.cloud.hra.service;


import com.yimao.cloud.hra.po.MiniSymptom;
import com.yimao.cloud.pojo.dto.hra.*;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 身体部位下所有症状 api
 */
public interface SymptomService {

    List<MiniSymptom> findBodySymptom(Map<String, String> map);

    //获取子症状和患病结果
    SubSymptomResultDTO getSubsymptomAndResult(Map<String, String> map);

    //分页对象
    PageVO<MiniSymptom> findSymptomList(Integer pageNum, Integer pageSize, Integer pid);

    PageVO<MiniSymptom> findAllList(Integer pageNum, Integer pageSize);

    /**
     * 获取身体部位下症状
     * @param pageNum
     * @param pageSize
     * @param secondId
     * @return
     */
    PageVO<MiniSymptomDTO> findBodyPartSymptoms(Integer pageNum, Integer pageSize, Integer secondId);

    /**
     * 获取症状信息
     * @param symptomId
     * @return
     */
    SymptomInfoDTO getZhengZhuangInfo(Integer symptomId);

    /**
     * 查询症状详情
     * @param symptomId
     * @return
     */
    SymptomDetailDTO getZhengZhuangDetail(Integer symptomId);

    /**
     * 获取分类名称以及包含热门症状
     * @return
     */
    List<ClassfySymptomDTO> getClassNameAndHotSymptom();
}
