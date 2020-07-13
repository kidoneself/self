package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDurationDTO;
import com.yimao.cloud.water.enums.ProductModelEnum;
import com.yimao.cloud.water.mapper.WaterDeviceDurationMapper;
import com.yimao.cloud.water.po.WaterDeviceDuration;
import com.yimao.cloud.water.service.WaterDeviceDurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

/**
 * 描述：广告位
 *
 * @Author Zhang Bo
 * @Date 2019/1/30 14:50
 * @Version 1.0
 */
@Service
@Slf4j
public class WaterDeviceDurationServiceImpl implements WaterDeviceDurationService {

    @Resource
    private WaterDeviceDurationMapper waterDeviceDurationMapper;

    /**
     * 根据设备型号查询设备时长
     *
     * @param snCode
     * @return
     */
    @Override
    public WaterDeviceDurationDTO getDurationBySn(String snCode) {
        if (StringUtil.isEmpty(snCode)) {
            throw new BadRequestException("设备SN不能为空。");
        }
        // 根据sn截取设备型号
        String deviceModel = snCode.substring(4, 9);
        if (StringUtil.isEmpty(deviceModel) || (StringUtil.isNotEmpty(deviceModel)
                && (!deviceModel.equalsIgnoreCase(ProductModelEnum.MODEL_1601T.value)
                && !deviceModel.equalsIgnoreCase(ProductModelEnum.MODEL_1602T.value)
                && !deviceModel.equalsIgnoreCase(ProductModelEnum.MODEL_1603T.value)
                && !deviceModel.equalsIgnoreCase(ProductModelEnum.MODEL_1601L.value)))) {
            throw new BadRequestException("错误的型号。");
        }

        Example example = new Example(WaterDeviceDuration.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceModel", deviceModel);
        WaterDeviceDuration waterDeviceDuration = waterDeviceDurationMapper.selectOneByExample(example);
        WaterDeviceDurationDTO dto = new WaterDeviceDurationDTO();
        waterDeviceDuration.convert(dto);
        return dto;
    }
}
