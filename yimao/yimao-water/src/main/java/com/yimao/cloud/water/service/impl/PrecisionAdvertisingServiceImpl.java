package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.ConnectionTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.enums.DeviceGroupEnum;
import com.yimao.cloud.water.enums.DeviceTypeEnum;
import com.yimao.cloud.water.enums.PlatformEnum;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingMapper;
import com.yimao.cloud.water.mapper.PrecisionAdvertisingMapper;
import com.yimao.cloud.water.mapper.ServiceStationWaterDeviceMapper;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import com.yimao.cloud.water.po.ServiceStationWaterDevice;
import com.yimao.cloud.water.po.WaterDevice;
import com.yimao.cloud.water.service.PrecisionAdvertisingService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 描述：广告精准投放。
 *
 * @Author Zhang Bo
 * @Date 2019/2/19 11:48
 * @Version 1.0
 */
@Service
@Slf4j
public class PrecisionAdvertisingServiceImpl implements PrecisionAdvertisingService {

    @Resource
    private ConditionalAdvertisingMapper conditionalAdvertisingMapper;
    @Resource
    private PrecisionAdvertisingMapper precisionAdvertisingMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private ServiceStationWaterDeviceMapper serviceStationWaterDeviceMapper;
    @Resource
    private WaterDeviceService waterDeviceService;

    /**
     * 判断导入的设备信息是否有效，将无效的数据踢出
     *
     * @param advertising
     * @param snList
     * @return
     */
    @Override
    public Set<String> selectEffectiveDevice(ConditionalAdvertising advertising, Set<String> snList) {
        if (CollectionUtil.isEmpty(snList)) {
            throw new BadRequestException("请添加设备信息。");
        }
        advertising.setPlatform(PlatformEnum.YIMAO.value); //精准投放只有自有平台能投放
        advertising.setEffective(true);
        advertising.setForbidden(false);
        advertising.setDeleted(false);
        checkParameter(advertising, 1);
        Set<String> result = new HashSet<>();
        for (String sn : snList) {
            //查询数据是是否是服务站设备
            Example example = new Example(ServiceStationWaterDevice.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("snCode", sn);
            ServiceStationWaterDevice deviceDTO = serviceStationWaterDeviceMapper.selectOneByExample(example);
            if (Objects.nonNull(deviceDTO)) {
                //根据省市区名称获取区域ID
                if (StringUtil.isNotEmpty(deviceDTO.getRegion())) {
                    List<Integer> ids = systemFeignHandler.getAreaIdsByName(deviceDTO.getRegion());
                    Boolean aBoolean = this.checkAdvertisingExists(advertising, ids, null, sn);
                    if (!aBoolean) {
                        result.add(sn);
                    }
                }
            }else {
                //根据sn查询用户组设备详情
                // DeviceDTO device = outFeign.getDeviceBySnCode(sn);
                WaterDevice device = waterDeviceService.getBySnCode(sn);
                if (Objects.nonNull(device)) {
                    //根据省市区名称获取区域ID
                    if (StringUtil.isNotEmpty(device.getRegion())) {
                        List<Integer> ids = systemFeignHandler.getAreaIdsByName(device.getRegion());
                        Boolean aBoolean = this.checkAdvertisingExists(advertising, ids, device.getPlace(), sn);
                        if (!aBoolean) {
                            result.add(sn);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 保存精准投放配置
     *
     * @param advertising
     * @param snList
     */
    @EnableOperationLog(
            name = "新增精准投放配置",
            type = OperationType.SAVE,
            daoClass = ConditionalAdvertisingMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"ownAdslotId"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(ConditionalAdvertising advertising, Set<String> snList) {
        if (Objects.isNull(snList) || snList.size() == 0) {
            throw new BadRequestException("请添加设备信息。");
        }
        advertising.setPlatform(0); //精准投放只有自有平台能投放
        advertising.setEffective(true);
        advertising.setForbidden(false);
        advertising.setDeleted(false);
        checkParameter(advertising, 2);
        //除去不符合要求的
        Set<String> set = selectEffectiveDevice(advertising, snList);
        advertising.setDeleted(false);
        advertising.setCreator(userCache.getCurrentAdminRealName());
        advertising.setCreateTime(new Date());
        //保存广告投放策略配置
        conditionalAdvertisingMapper.insert(advertising);
        // 批量插入
        precisionAdvertisingMapper.batchInsert(set, advertising.getId());
    }


    /**
     * 根据设备sn集合查询设备详情
     *
     * @param deviceList
     * @return
     */
    @Override
    public List<WaterDeviceVO> deviceList(Set<String> deviceList) {
        List<WaterDeviceVO> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(deviceList)) {
            for (String sn : deviceList) {
                //查询数据是是否是服务站设备
                Example example = new Example(ServiceStationWaterDevice.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("snCode", sn);
                ServiceStationWaterDevice deviceDTO = serviceStationWaterDeviceMapper.selectOneByExample(example);
                if (Objects.nonNull(deviceDTO)) {
                    //封装数据
                    WaterDeviceVO vo = new WaterDeviceVO();
                    deviceDTO.convert(vo);
                    vo.setDeviceGroup(DeviceTypeEnum.STATION.name);
                    list.add(vo);
                } else {
                    // DeviceDTO device = outFeign.getDeviceBySnCode(sn);
                    WaterDevice device = waterDeviceService.getBySnCode(sn);
                    if (Objects.nonNull(device)) {
                        WaterDeviceVO vo = new WaterDeviceVO();
                        device.convert(vo);
                        //根据省市区名称获取区域ID
                        vo.setDeviceGroup(DeviceTypeEnum.USER.name);
                        list.add(vo);
                    }
                }
            }
        }
        return list;
    }


    /**
     * 查询本次配置信息和现有条件配置信息是否有交集。
     *
     * @param ad
     * @param areaIdSet
     * @return
     */
    public Boolean checkAdvertisingExists(ConditionalAdvertising ad, List<Integer> areaIdSet, String localtionTab, String snCode) {
        Set<String> modelSet = new HashSet<>();
        if (StringUtil.isNotBlank(ad.getModels())) {
            String[] models = ad.getModels().split(",");
            for (String model : models) {
                modelSet.add(model);
            }
        }
        return conditionalAdvertisingMapper.checkAdvertisingId(areaIdSet, modelSet,
                ad.getPlatform(), ad.getOwnAdslotId(), ad.getAdslotId(), ad.getScreenLocation(),
                ad.getAdvertisingType(), ad.getConnectionType(),ad.getAfterConnectionType(), ad.getEffectiveBeginTime(), ad.getEffectiveEndTime(), ad.getId(), localtionTab, snCode, ad.getDeviceGroup());
    }

    /**
     * 校验入参合法性
     *
     * @param advertising
     */
    public void checkParameter(ConditionalAdvertising advertising, int type) {
        if (Objects.isNull(advertising)) {
            throw new BadRequestException("投放配置不能为空。");
        }
        if (advertising.getPlatform() != PlatformEnum.YIMAO.value) {
            throw new BadRequestException("请正确选择广告平台。");
        }
        if (Objects.isNull(advertising.getOwnAdslotId())) {
            throw new BadRequestException("请选择广告位。");
        }
        if (Objects.isNull(advertising.getScreenLocation())) {
            throw new BadRequestException("请选择投放屏幕。");
        }
        if (advertising.getScreenLocation() != ScreenLocationEnum.ONE.value
                && advertising.getScreenLocation() != ScreenLocationEnum.TWO.value) {
            throw new BadRequestException("请正确选择屏幕。");
        }
        if (Objects.isNull(advertising.getAdvertisingType())) {
            throw new BadRequestException("请选择投放类型。");
        }
        if (!advertising.getAdvertisingType().equals(2)) {
            throw new BadRequestException("投放类型必须是精准投放。");
        }
        if (Objects.equals(type, 2)) {
            if (Objects.isNull(advertising.getAdslotId())) {
                throw new BadRequestException("请填写物料ID。");
            }
        }

        if (Objects.isNull(advertising.getConnectionType())) {
            advertising.setConnectionType(ConnectionTypeEnum.UNLIMITED.value);
        } else if (!Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.UNLIMITED.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.WIFI.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS2.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS3.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS4.value)
                && !Objects.equals(advertising.getConnectionType(), ConnectionTypeEnum.GPRS5.value)) {
            throw new BadRequestException("请正确选择网络类型。");
        }

        if (Objects.isNull(advertising.getAfterConnectionType())) {
            advertising.setConnectionType(ConnectionTypeEnum.UNLIMITED.value);
        } else if (!Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.UNLIMITED.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.WIFI.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS2.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS3.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS4.value)
                && !Objects.equals(advertising.getAfterConnectionType(), ConnectionTypeEnum.GPRS5.value)) {
            throw new BadRequestException("请正确选择后续网络类型。");
        }
        if (Objects.isNull(advertising.getEffectiveBeginTime())) {
            throw new BadRequestException("请选择开始时间。");
        }
        if (Objects.isNull(advertising.getEffectiveEndTime())) {
            throw new BadRequestException("请选择结束时间。");
        }
        if (advertising.getEffectiveEndTime().before(new Date())) {
            throw new BadRequestException("结束时间不能早于当前时间。");
        }
        if (Objects.nonNull(advertising.getDeviceGroup())
                && !Objects.equals(advertising.getDeviceGroup(), DeviceGroupEnum.USER.value)
                && !Objects.equals(advertising.getDeviceGroup(), DeviceGroupEnum.STATION.value)) {
            throw new BadRequestException("请选择正确的设备组。");
        }

    }
}
