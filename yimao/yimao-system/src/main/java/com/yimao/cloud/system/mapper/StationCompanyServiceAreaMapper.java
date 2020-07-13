package com.yimao.cloud.system.mapper;

import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import com.yimao.cloud.system.po.StationCompanyServiceArea;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author Lizhqiang
 * @date 2019/1/18
 */
public interface StationCompanyServiceAreaMapper extends Mapper<StationCompanyServiceArea> {

    void batchInsert(List<StationCompanyServiceArea> list);

    //根据服务站公司id查询该服务站公司的售后服务区域
    Set<StationCompanyServiceAreaDTO> selectAfterServiceAreaByStationCompanyId(@Param("stationCompanyId") Integer id);
}
