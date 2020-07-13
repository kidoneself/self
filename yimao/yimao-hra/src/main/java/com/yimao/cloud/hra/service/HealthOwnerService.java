package com.yimao.cloud.hra.service;

import com.yimao.cloud.pojo.dto.hra.ChoiceOptionDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingDTO;
import com.yimao.cloud.pojo.dto.hra.EvaluatingImageDTO;

import java.util.List;
import java.util.Map;

/**
 * 健康测评 API接口
 */
public interface HealthOwnerService {

    /**
     * @param pageNum
     * @param pageSize
     * @param pid
     * @Description: 查询每个分类下的列表
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.hra.EvaluatingDTO
     * @Create: 2019/4/22 14:09
     */
    EvaluatingDTO getHealthCatory(Integer pageNum, Integer pageSize, Integer pid);

    /**
     * @param pageNum
     * @param pageSize
     * @Description: 查询健康自测分类
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.EvaluatingDTO>
     * @Create: 2019/4/22 14:05
     */
    List<EvaluatingDTO> findAllHealthList(Integer pageNum, Integer pageSize);

    /**
     * @param evaluateId
     * @Description: 获取选择题列表
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.hra.ChoiceOptionDTO>
     * @Create: 2019/4/22 14:10
     */
    List<ChoiceOptionDTO> findChoiceList(Integer evaluateId);

    /**
     * 获取每个选题信息
     *
     * @param map
     * @return
     */
    EvaluatingImageDTO getEvaluateInfo(Map<String, String> map);

}
