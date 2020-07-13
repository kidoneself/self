package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.AdslotConfigDTO;
import com.yimao.cloud.pojo.dto.water.ConditionalAdvertisingDTO;
import com.yimao.cloud.pojo.dto.water.MaterialsConfigDTO;
import com.yimao.cloud.pojo.dto.water.PlatformConfigDTO;
import com.yimao.cloud.water.enums.DeviceTypeEnum;
import com.yimao.cloud.water.enums.PlatformEnum;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingMapper;
import com.yimao.cloud.water.mapper.MaterialsMapper;
import com.yimao.cloud.water.mapper.PlatformMapper;
import com.yimao.cloud.water.mapper.ServiceStationWaterDeviceMapper;
import com.yimao.cloud.water.po.Materials;
import com.yimao.cloud.water.po.Platform;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.GetAdslotConfigService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 描述：客户端获取所有广告位配置信息。
 *
 * @Author Zhang Bo
 * @Date 2019/2/20 17:35
 * @Version 1.0
 */
@Service
@Slf4j
public class GetAdslotConfigServiceImpl implements GetAdslotConfigService {

    @Resource
    private ConditionalAdvertisingMapper conditionalAdvertisingMapper;
    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private PlatformMapper platformMapper;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private ServiceStationWaterDeviceMapper serviceStationWaterDeviceMapper;
    @Resource
    private WaterDeviceService waterDeviceService;

    /**
     * 根据SN码查询所有广告位配置信息
     *
     * @param snCode SN码
     * @return
     */
    @Override
    public Map<String, Object> listBySnCode(String snCode, Date currentTime, Integer deviceGroup) {
        if (StringUtil.isEmpty(snCode)) {
            throw new BadRequestException("设备编码不能为空！");
        }
        if (Objects.isNull(currentTime)) {
            // 当入参为空时，去当前时间
            currentTime = new Date();
        }
        Map<String, Object> map = new HashMap<>();
        List<AdslotConfigDTO> bigConfigList = new ArrayList<>();
        List<AdslotConfigDTO> smallConfigList = new ArrayList<>();

        //根据sn查询设备信息
        WaterDevice device = new WaterDevice();
        if (Objects.equals(deviceGroup, DeviceTypeEnum.USER.value)) {
            // 查询用户组设备数据
            // device = outFeign.getDeviceBySnCode(snCode);
            device = waterDeviceService.getBySnCode(snCode);
            if (Objects.isNull(device)) {
                map.put("bigConfigList", bigConfigList);
                map.put("smallConfigList", smallConfigList);
                return map;
            }
        } else if (Objects.equals(deviceGroup, DeviceTypeEnum.STATION.value)) {
            //查询服务站设备数据
            Example example = new Example(ServiceStationWaterDevice.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("snCode", snCode);
            ServiceStationWaterDevice deviceDTO = serviceStationWaterDeviceMapper.selectOneByExample(example);
            if (Objects.nonNull(deviceDTO)) {
                //将查询出来的数据进行赋值
                deviceDTO.convert(device);
                device.setPlace(null);
            } else {
                map.put("bigConfigList", bigConfigList);
                map.put("smallConfigList", smallConfigList);
                return map;
            }
        } else {
            //表示传入的设备组类型不正确，直接返回
            map.put("bigConfigList", bigConfigList);
            map.put("smallConfigList", smallConfigList);
            return map;
        }

        //根据设备的区域信息查询区域ID
        List<Integer> areaIds = systemFeignHandler.getAreaIdsByName(device.getRegion());
        if (areaIds == null || areaIds.size() == 0) {
            map.put("bigConfigList", bigConfigList);
            map.put("smallConfigList", smallConfigList);
            return map;
        }
        //当精准投放条件和条件投放策略冲突时以精准投放主
        //先查询精准投放，再查找条件投放策略
        List<ConditionalAdvertisingDTO> precisionList = conditionalAdvertisingMapper.listPrecisionByDeviceInfo(snCode, device.getConnectionType(),
                currentTime, device.getPlace(), deviceGroup);
        Set<String> adSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(precisionList)) {
            //查询出已经有广告的广告位
            for (ConditionalAdvertisingDTO dto : precisionList) {
                adSet.add(dto.getOwnAdslotId());
            }
        }

        List<ConditionalAdvertisingDTO> advertisingList = conditionalAdvertisingMapper.listByDeviceInfo(areaIds, device.getDeviceModel(),
                device.getConnectionType(), currentTime, adSet, device.getPlace(), deviceGroup);

        if (CollectionUtil.isEmpty(precisionList) && CollectionUtil.isEmpty((advertisingList))) {
            map.put("bigConfigList", bigConfigList);
            map.put("smallConfigList", smallConfigList);
            return map;
        }

        List<ConditionalAdvertisingDTO> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(advertisingList) && CollectionUtil.isNotEmpty(precisionList)) {
            list.addAll(precisionList);
            list.addAll(advertisingList);
        } else if (CollectionUtil.isEmpty(precisionList) && CollectionUtil.isNotEmpty((advertisingList))) {
            list.addAll(advertisingList);
        } else if (CollectionUtil.isEmpty(advertisingList) && CollectionUtil.isNotEmpty((precisionList))) {
            list.addAll(precisionList);
        }

        //区分大小屏返回数据
        if (CollectionUtil.isNotEmpty(list)) {
            List<Platform> platformList = platformMapper.selectAll();
            for (ConditionalAdvertisingDTO advertising : list) {
                if (Objects.equals(advertising.getScreenLocation(), ScreenLocationEnum.ONE.value)) {
                    AdslotConfigDTO config = getPara(advertising, platformList);
                    bigConfigList.add(config);
                } else if (Objects.equals(advertising.getScreenLocation(), ScreenLocationEnum.TWO.value)) {
                    AdslotConfigDTO config = getPara(advertising, platformList);
                    smallConfigList.add(config);
                }
            }
        }

        map.put("bigConfigList", bigConfigList);
        map.put("smallConfigList", smallConfigList);
        return map;
    }

    /**
     * 封装返回数据
     *
     * @param advertising
     * @return
     */
    public AdslotConfigDTO getPara(ConditionalAdvertisingDTO advertising, List<Platform> platformList) {
        AdslotConfigDTO config = new AdslotConfigDTO();
        config.setId(advertising.getId());
        config.setOwnAdslotId(advertising.getOwnAdslotId());
        config.setAdslotId(advertising.getAdslotId());
        config.setDuration(advertising.getDuration());
        config.setConnectionType(advertising.getConnectionType());
        config.setAfterConnectionType(advertising.getAfterConnectionType());
        config.setBeginTime(advertising.getEffectiveBeginTime());
        config.setEndTime(advertising.getEffectiveEndTime());
        config.setScreenLocation(advertising.getScreenLocation());

        //封装广告平台信息（翼猫、百度、京东、科大讯飞等）
        for (Platform platform : platformList) {
            if (platform.getId().equals(advertising.getPlatform())) {
                PlatformConfigDTO platformConfig = new PlatformConfigDTO();
                platformConfig.setId(platform.getId());
                platformConfig.setName(platform.getName());
                platformConfig.setAppId(platform.getAppId());
                platformConfig.setApiVersion(platform.getApiVersion());
                platformConfig.setUrl(platform.getUrl());
                config.setPlatform(platformConfig);
            }
        }

        //如果是翼猫自有广告，则封装物料信息
        if (advertising.getPlatform() == PlatformEnum.YIMAO.value) {
            Materials materials = materialsMapper.selectByPrimaryKey(Integer.parseInt(advertising.getAdslotId()));
            MaterialsConfigDTO materialsConfig = new MaterialsConfigDTO();
            materialsConfig.setId(materials.getId());
            materialsConfig.setName(materials.getName());
            materialsConfig.setSize(materials.getSize());
            materialsConfig.setDuration(materials.getDuration());
            materialsConfig.setScreenLocation(materials.getScreenLocation());
            materialsConfig.setImage(materials.getImage());
            materialsConfig.setAdvertisers(materials.getAdvertisers());
            materialsConfig.setUrl(materials.getUrl());
            materialsConfig.setType(materials.getMaterielType());
            materialsConfig.setDescription(materials.getDescription());
            config.setMaterials(materialsConfig);//设置物料信息
        }

        return config;
    }

}
