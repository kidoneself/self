package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.MiniEvaluating;
import com.yimao.cloud.pojo.dto.hra.EvaluatingImageDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface MiniEvaluatingMapper extends Mapper<MiniEvaluating> {

    /**
     * 根据ID 获取分类列表
     * @param secId
     * @return
     */
    List<MiniEvaluating> findEvaluatingList(@Param("secId") Integer secId);

    int updateJoinNumber(Map<String, String> map);

    /**
     * 获取图片
     * @param map
     * @return
     */
    EvaluatingImageDTO findImageById(Map<String, String> map);
}
