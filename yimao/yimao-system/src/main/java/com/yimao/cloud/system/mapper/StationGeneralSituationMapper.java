package com.yimao.cloud.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/2/13
 */
public interface StationGeneralSituationMapper {

    Integer getCompanyNumber();

    Integer getStationNumber();

    Integer getContractNumber();

    List<Map<String, Integer>> getStationVariationTrend(Integer days, Date time);
}
