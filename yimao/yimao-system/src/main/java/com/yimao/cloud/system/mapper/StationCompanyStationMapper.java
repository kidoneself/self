package com.yimao.cloud.system.mapper;

import com.yimao.cloud.system.po.StationCompanyStation;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StationCompanyStationMapper extends Mapper<StationCompanyStation> {

    /**
     * 批量插入
     *
     * @param list
     */
    void batchInsert(List<StationCompanyStation> list);

}
