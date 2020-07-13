package com.yimao.cloud.hra.service;


import com.yimao.cloud.pojo.dto.hra.SickenResultDTO;

/**
 * 患病结果 详情 接口API
 */
public interface SickenResultService {

    SickenResultDTO getResultDetail(Integer resultId);

}
