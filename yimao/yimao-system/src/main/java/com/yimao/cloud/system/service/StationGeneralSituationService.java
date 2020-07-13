package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.StationGeneralSituationDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Lizhqiang
 * @date 2019/2/13
 */
public interface StationGeneralSituationService {

    StationGeneralSituationDTO getStationGeneralSituation();

    List<Map<String, Integer>> getStationVariationTrend(Integer days);
}
