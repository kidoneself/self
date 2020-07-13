package com.yimao.cloud.water.handler;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.water.feign.SystemFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/12/5
 */
@Component
@Slf4j
public class SystemFeignHandler {

    private static final String GET_AREAIDS_BY_NAME = "LIST::GET_AREAIDS_BY_NAME::";
    private static final String GET_AREA_BY_ID = "A::GET_AREA_BY_ID::";

    @Resource
    private SystemFeign systemFeign;
    @Resource
    private RedisCache redisCache;

    /**
     * water并发较高，改用redis缓存一段时间
     *
     * @param id 地区ID
     */
    public AreaDTO getAreaById(Integer id) {
        AreaDTO area = redisCache.get(GET_AREA_BY_ID + id, AreaDTO.class);
        if (area != null) {
            return area;
        } else {
            //根据设备的区域信息查询区域ID
            area = systemFeign.getAreaById(id);
            if (area != null) {
                redisCache.set(GET_AREA_BY_ID + id, area, 3600L);
            }
            return area;
        }
    }

    /**
     * water并发较高，改用redis缓存一段时间
     *
     * @param name 地区名称
     */
    public List<Integer> getAreaIdsByName(String name) {
        List<Integer> areaIds = redisCache.getCacheList(GET_AREAIDS_BY_NAME + name, Integer.class);
        if (CollectionUtil.isNotEmpty(areaIds)) {
            if (!Constant.PRO_ENVIRONMENT) {
                log.info("从缓存里获取GET_AREAIDS_BY_NAME成功{}", JSONObject.toJSONString(areaIds));
            }
            return areaIds;
        } else {
            //根据设备的区域信息查询区域ID
            areaIds = systemFeign.getAreaIdsByName(name);
            if (CollectionUtil.isNotEmpty(areaIds)) {
                redisCache.setCacheList(GET_AREAIDS_BY_NAME + name, areaIds, Integer.class, 3600L);
            }
            return areaIds;
        }
    }
}
