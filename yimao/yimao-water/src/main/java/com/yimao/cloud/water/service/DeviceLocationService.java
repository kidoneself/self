package com.yimao.cloud.water.service;

import com.yimao.cloud.water.po.DeviceLocation;

import java.util.List;

public interface DeviceLocationService {

    /**
     * 获取所有设备位置信息
     *
     * @return
     */
    List<DeviceLocation> listAll();

}
