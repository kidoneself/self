package com.yimao.cloud.hra.service;


import com.yimao.cloud.pojo.dto.hra.MiniSicknessDTO;
import com.yimao.cloud.pojo.dto.hra.SicknessResultDTO;

/**
 * 疾病结果 api
 */
public interface SicknessService {

    MiniSicknessDTO getSicknessById(Integer sicknessId);

    /**
     * 根据选择症状得到可能的患病结果
     * @param symptomId
     * @param symptomIds
     * @return
     */
    SicknessResultDTO getSicknessBySymptomId(Integer symptomId, String[] symptomIds);

}
