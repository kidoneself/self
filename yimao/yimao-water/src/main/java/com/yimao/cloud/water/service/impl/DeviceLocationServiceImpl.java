package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.water.mapper.DeviceLocationMapper;
import com.yimao.cloud.water.po.DeviceLocation;
import com.yimao.cloud.water.service.DeviceLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：设备位置
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 14:29
 * @Version 1.0
 */
@Service
@Slf4j
public class DeviceLocationServiceImpl implements DeviceLocationService {

    @Resource
    private DeviceLocationMapper deviceLocationMapper;

    /**
     * 获取所有设备位置信息
     *
     * @return
     */
    @Override
    public List<DeviceLocation> listAll() {
        return deviceLocationMapper.selectAll();
    }

}
