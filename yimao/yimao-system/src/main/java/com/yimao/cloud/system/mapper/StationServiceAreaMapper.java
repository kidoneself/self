package com.yimao.cloud.system.mapper;


import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import com.yimao.cloud.system.po.StationServiceArea;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/1/23
 */
public interface StationServiceAreaMapper extends Mapper<StationServiceArea> {

    void batchInsert(List<StationServiceArea> list);

    List<StationServiceAreaDTO> getServiceAreaByStationId(@Param("stationId") Integer stationId);

    List<StationServiceAreaDTO> getAfterServiceAreaByStationId(@Param("stationId") Integer stationId);
}
