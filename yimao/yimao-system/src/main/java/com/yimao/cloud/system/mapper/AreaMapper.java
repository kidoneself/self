package com.yimao.cloud.system.mapper;

import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.system.po.Area;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AreaMapper extends Mapper<Area> {

    /**
     * 查询全部省市区
     * @return list
     */
    List<AreaDTO> areaList();

    Integer getRegionIdByPCR(@Param("province") String province, @Param("city") String city, @Param("region") String region);
}
