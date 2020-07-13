package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.AdslotConfigDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GetAdslotConfigService {
    /**
     * 根据SN码查询所有广告位配置信息
     *
     * @param snCode SN码
     * @return
     */
    Map<String, Object> listBySnCode(String snCode, Date currentTime, Integer deviceGroup);
}
