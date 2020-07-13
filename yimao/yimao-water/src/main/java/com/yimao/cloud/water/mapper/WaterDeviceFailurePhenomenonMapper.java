package com.yimao.cloud.water.mapper;


import com.yimao.cloud.water.po.WaterDeviceFailurePhenomenon;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface WaterDeviceFailurePhenomenonMapper extends Mapper<WaterDeviceFailurePhenomenon> {
    /**
     * @description   批量保存
     * @author Liu Yi
     * @date 2019/11/2 13:58
     * @param
     * @return java.lang.Integer
     */
    Integer batchSave(@Param("list") List list);
}
