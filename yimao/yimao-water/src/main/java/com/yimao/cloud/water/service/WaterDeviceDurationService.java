package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDurationDTO;


public interface WaterDeviceDurationService {

    WaterDeviceDurationDTO getDurationBySn(String snCode);
}
