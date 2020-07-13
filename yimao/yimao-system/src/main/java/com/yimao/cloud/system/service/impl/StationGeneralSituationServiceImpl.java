package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.pojo.dto.system.StationGeneralSituationDTO;
import com.yimao.cloud.system.mapper.StationGeneralSituationMapper;
import com.yimao.cloud.system.service.StationGeneralSituationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Lizhqiang
 * @date 2019/2/13
 */
@Service
@Slf4j
public class StationGeneralSituationServiceImpl implements StationGeneralSituationService {

    @Resource
    private StationGeneralSituationMapper stationGeneralSituationMapper;


    /**
     * 服务站模块概况
     *
     * @return
     */
    @Override
    public StationGeneralSituationDTO getStationGeneralSituation() {
        StationGeneralSituationDTO dto = new StationGeneralSituationDTO();
        //服务站公司数量
        int companyNumber = stationGeneralSituationMapper.getCompanyNumber();
        dto.setStationCompanyNum(companyNumber);
        //服务站数量
        int stationNumber = stationGeneralSituationMapper.getStationNumber();
        dto.setStationNum(stationNumber);
        //已承包数量
        int contractNumber = stationGeneralSituationMapper.getContractNumber();
        dto.setContractNum(contractNumber);
        //TODO 服务站站务系统开通数量
        return dto;
    }

    /**
     * 根据天数获取服务站模块变化趋势
     *
     * @param days
     */
    @Override
    public List<Map<String, Integer>> getStationVariationTrend(Integer days) {
        Date time = new Date();
        List<Map<String, Integer>> mapList = stationGeneralSituationMapper.getStationVariationTrend(days, time);
        return mapList;
    }

}
