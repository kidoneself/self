package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.query.water.WaterDeviceQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import com.yimao.cloud.water.handler.SystemFeignHandler;
import com.yimao.cloud.water.mapper.ConditionalAdvertisingMapper;
import com.yimao.cloud.water.po.ConditionalAdvertising;
import com.yimao.cloud.water.service.AdslotService;
import com.yimao.cloud.water.service.DeviceService;
import com.yimao.cloud.water.service.WaterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Chen Hui Yang
 * @date 2019/3/7
 */
@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private AdslotService adslotService;
    @Resource
    private WaterDeviceService waterDeviceService;
    @Resource
    private SystemFeignHandler systemFeignHandler;
    @Resource
    private ConditionalAdvertisingMapper conditionalAdvertisingMapper;

    /**
     * 查询设备列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public PageVO<WaterDeviceVO> queryListByCondition(Integer pageNum, Integer pageSize, WaterDeviceQuery query, Date beginTime, Date endTime) {
        // PageVO<DeviceDTO> devicePage = outFeign.pageDevice(pageNum, pageSize, query);
        query.setStartTime(beginTime);
        query.setEndTime(endTime);
        PageVO<WaterDeviceVO> wdvo = waterDeviceService.pageWaterDevice(pageNum, pageSize, query);
        List<WaterDeviceVO> result = wdvo.getResult();
        if (CollectionUtil.isNotEmpty(result)) {
            for (WaterDeviceVO vo : result) {
                //根据名称查询省市区ID集合
                List<Integer> areaIds = systemFeignHandler.getAreaIdsByName(vo.getRegion());
                ConditionalAdvertising ad = new ConditionalAdvertising();
                ad.setEffectiveBeginTime(beginTime);
                ad.setEffectiveEndTime(endTime);
                // 网络类型
                ad.setConnectionType(query.getConnectionType());
                Set<String> modelSet = new HashSet<>();
                modelSet.add(vo.getDeviceModel());
                //根据条件查询可以用广告位
                Set<String> adslotIdList = conditionalAdvertisingMapper.selectAdslotId(areaIds, modelSet, vo.getConnType(),
                        ad.getEffectiveBeginTime(), ad.getEffectiveEndTime());
                //查询剩余广告位
                Set<String> adslotNameList = adslotService.selectAdslotName(adslotIdList);
                vo.setAdslotStock(adslotNameList);
            }
        }
        return wdvo;
    }

}
