package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.water.mapper.DevicePasswordMapper;
import com.yimao.cloud.water.po.DevicePassword;
import com.yimao.cloud.water.service.DevicePasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/2/25 15:41
 * @Version 1.0
 */
@Service
@Slf4j
public class DevicePasswordServiceImpl implements DevicePasswordService {

    private static final String WATER_DEVICE_PASSWORD_CACHE_NAME = "WATER_DEVICE_PASSWORD_CACHED";

    @Resource
    private DevicePasswordMapper devicePasswordMapper;
    @Resource
    private RedisCache redisCache;

    /**
     * 获取设备密码
     */
    @Override
    public String getPwd() {
        String pwd = redisCache.get(WATER_DEVICE_PASSWORD_CACHE_NAME);
        if (StringUtil.isNotEmpty(pwd)) {
            return pwd;
        } else {
            DevicePassword password = devicePasswordMapper.selectAll().get(0);
            if (password != null) {
                redisCache.set(WATER_DEVICE_PASSWORD_CACHE_NAME, password.getPwd(), -1L);
                return password.getPwd();
            }
            return null;
        }
    }

}
