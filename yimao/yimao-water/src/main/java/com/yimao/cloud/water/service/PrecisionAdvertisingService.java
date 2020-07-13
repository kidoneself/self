package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.po.ConditionalAdvertising;

import java.util.List;
import java.util.Set;

public interface PrecisionAdvertisingService {

    Set<String> selectEffectiveDevice(ConditionalAdvertising advertising, Set<String> snList);

    void save(ConditionalAdvertising advertising, Set<String> snList);

    List<WaterDeviceVO> deviceList(Set<String> deviceList);
}
