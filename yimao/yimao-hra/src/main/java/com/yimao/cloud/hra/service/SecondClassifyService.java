package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.MiniSecondClassify;
import com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO;

import java.util.List;

/**
 * @Description:
 * @author ycl
*/
public interface SecondClassifyService {

    /**
     * @param pageNum
     * @param pageSize
     * @param pid
     * @Description: 症状自查类型
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO
     */
    MiniSecondClassifyDTO getAllClassify(Integer pageNum, Integer pageSize, Integer pid);

    List<MiniSecondClassifyDTO> findAllSymptom();

    /**
     * @Description: 查询健康二级分类
     * @author ycl
     * @param
     * @Return: java.util.List<com.yimao.cloud.hra.po.HealthySecondClassify>
    */
    List<MiniSecondClassify> findAllHealthSecond();

}
