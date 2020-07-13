package com.yimao.cloud.system.service.impl;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.system.AreaDTO;
import com.yimao.cloud.pojo.dto.system.AreaInfoDTO;
import com.yimao.cloud.system.mapper.AreaMapper;
import com.yimao.cloud.system.po.Area;
import com.yimao.cloud.system.service.AreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-02-13 10:10:29
 **/
@Service
public class AreaServiceImpl implements AreaService {

    @Resource
    private AreaMapper areaMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 根据区域ID查询区域
     *
     * @param id 区域ID
     */
    @Override
    public Area getById(Integer id) {
        return areaMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据区域ID获取省市区名称
     *
     * @param id 区域ID
     */
    @Override
    public Map<String, String> getPCRNamesById(Integer id) {
        Map<String, String> map = new HashMap<>();
        map.put("region", "");
        map.put("city", "");
        map.put("province", "");
        Area area = areaMapper.selectByPrimaryKey(id);
        if (area.getLevel() == 3) {
            map.put("region", area.getName());
            Area city = areaMapper.selectByPrimaryKey(area.getPid());
            Area province = areaMapper.selectByPrimaryKey(city.getPid());
            map.put("city", city.getName());
            map.put("province", province.getName());
        } else if (area.getLevel() == 2) {
            map.put("city", area.getName());
            Area province = areaMapper.selectByPrimaryKey(area.getPid());
            map.put("province", province.getName());
        } else if (area.getLevel() == 1) {
            map.put("province", area.getName());
        }
        return map;
    }

    /**
     * 根据名称查询省市区ID集合
     *
     * @param name 名称
     */
    @Override
    public List<Integer> getAreaIdsByName(String name) {
        Area query = new Area();
        query.setName(name);
        query.setLevel(3);
        List<Area> areas = areaMapper.select(query);
        if (areas == null || areas.size() == 0) {
            return null;
        }
        List<Integer> ids = new ArrayList<>(3);
        Area region = areas.get(0);
        ids.add(region.getId());//区ID
        Area city = areaMapper.selectByPrimaryKey(region.getPid());
        ids.add(city.getId());//市ID
        ids.add(city.getPid());//省ID
        return ids;
    }

    /**
     * 查询所有省
     */
    @Override
    public List<AreaDTO> listProvince() {
        Area query = new Area();
        query.setLevel(1);
        query.setDeleted(false);
        List<Area> list = areaMapper.select(query);
        return CollectionUtil.batchConvert(list, Area.class, AreaDTO.class);
    }

    /**
     * 查询所有下级区域
     *
     * @param pid 上级ID
     */
    @Override
    public List<AreaDTO> listSubArea(Integer pid) {
        Area query = new Area();
        query.setPid(pid);
        query.setDeleted(false);
        List<Area> list = areaMapper.select(query);
        return CollectionUtil.batchConvert(list, Area.class, AreaDTO.class);
    }


    /**
     * 根据区域ID获取省市区名称信息
     *
     * @param id 区域ID
     */
    @Override
    public AreaInfoDTO getAreaInfoById(Integer id) {
        AreaInfoDTO areaInfoDTO = new AreaInfoDTO();
        Area area = areaMapper.selectByPrimaryKey(id);
        areaInfoDTO.setRegionId(id);
        areaInfoDTO.setRegionName(area.getName());
        Area city = areaMapper.selectByPrimaryKey(area.getPid());
        Area province = areaMapper.selectByPrimaryKey(city.getPid());
        areaInfoDTO.setCityId(city.getId());
        areaInfoDTO.setCityName(city.getName());
        areaInfoDTO.setProvinceId(province.getId());
        areaInfoDTO.setProvinceName(province.getName());
        return areaInfoDTO;
    }

    @Override
    public List<AreaDTO> areaList() {
        List<AreaDTO> areaList;
        areaList = redisCache.getCacheList(Constant.AREA_CACHE, AreaDTO.class);
        if (CollectionUtil.isEmpty(areaList)) {
            areaList = areaMapper.areaList();
            redisCache.setCacheList(Constant.AREA_CACHE, areaList, AreaDTO.class, -1);
        }
        return areaList;
    }

    /**
     * 根据省市区获取区域ID
     *
     * @param province 省
     * @param city     市
     * @param region   区
     */
    @Override
    public Integer getRegionIdByPCR(String province, String city, String region) {
        Integer regionId = redisCache.get(Constant.AREA_CACHE + province + "_" + city + "_" + region, Integer.class);
        if (regionId == null) {
            regionId = areaMapper.getRegionIdByPCR(province, city, region);
            if (regionId != null) {
                redisCache.set(Constant.AREA_CACHE + province + "_" + city + "_" + region, regionId);
            }
        }
        return regionId;
    }
}
