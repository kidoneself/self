
package com.yimao.cloud.water.service.impl;


import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceFailurePhenomenonDTO;
import com.yimao.cloud.water.mapper.WaterDeviceFailurePhenomenonMapper;
import com.yimao.cloud.water.po.WaterDeviceFailurePhenomenon;
import com.yimao.cloud.water.service.WaterDeviceFailurePhenomenonService;
import com.yimao.cloud.water.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:
 *
 * @auther: liu yi
 * @date: 2019/4/10 14:40
 */
@Service
@Slf4j
public class WaterDeviceFailurePhenomenonServiceImpl implements WaterDeviceFailurePhenomenonService {
    @Resource
    private WaterDeviceFailurePhenomenonMapper waterDeviceFailurePhenomenonMapper;
    @Resource
    private UserCache userCache;

    @Override
    public List<WaterDeviceFailurePhenomenonDTO> getByWorkCode(String workCode, WorkOrderTypeEnum workOrderTypeEnum) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("工单号不能为空!");
        } else {
            Example example = new Example(WaterDeviceFailurePhenomenon.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("workCode", workCode);
            criteria.andEqualTo("workOrderIndex", workOrderTypeEnum.getType());
            List<WaterDeviceFailurePhenomenon> list = waterDeviceFailurePhenomenonMapper.selectByExample(example);
            List<WaterDeviceFailurePhenomenonDTO> dtoList = new ArrayList<>();
            WaterDeviceFailurePhenomenonDTO waterDeviceFailurePhenomenonDTO;
            for (WaterDeviceFailurePhenomenon waterDeviceFailurePhenomenon : list) {
                waterDeviceFailurePhenomenonDTO = new WaterDeviceFailurePhenomenonDTO();
                waterDeviceFailurePhenomenon.convert(waterDeviceFailurePhenomenonDTO);
                dtoList.add(waterDeviceFailurePhenomenonDTO);
            }

            return dtoList;
        }
    }

    @Override
    public void batchSave(List<WaterDeviceFailurePhenomenonDTO> list, String workCode) {
        Integer engineerId = userCache.getCurrentEngineerId();
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("工单号不能为空!");
        }

        if (null == list || list.size() <= 0) {
            throw new BadRequestException("缺少故障原因信息!");
        }

        List<WaterDeviceFailurePhenomenon> wpList=new ArrayList<>();
        WaterDeviceFailurePhenomenon wp;
        for (WaterDeviceFailurePhenomenonDTO dto : list) {
            wp=new WaterDeviceFailurePhenomenon(dto);
            wp.setWorkCode(workCode);
            wp.setCreateTime(new Date());
            wp.setUpdateTime(new Date());
            if(StringUtil.isBlank(dto.getWorkOrderIndex())){
                wp.setWorkOrderIndex( WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
            }
            if (engineerId != null) {
                wp.setCreateUser(String.valueOf(engineerId));
            }
            wpList.add(wp);
        }
        waterDeviceFailurePhenomenonMapper.batchSave(wpList);
    }

    @Override
    public void delete(String workCode, String workOrderType) {
        if (StringUtil.isBlank(workCode)) {
            throw new BadRequestException("工单号不能为空!");
        }

        Example example = new Example(WaterDeviceFailurePhenomenon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workCode);
        criteria.andEqualTo("workOrderIndex", workOrderType);

        WaterDeviceFailurePhenomenon waterDeviceFailurePhenomenon = new WaterDeviceFailurePhenomenon();
        waterDeviceFailurePhenomenon.setWorkCode(workCode);
        waterDeviceFailurePhenomenon.setDelStatus(StatusEnum.YES.value());
        waterDeviceFailurePhenomenon.setDeleteTime(new Date());

        waterDeviceFailurePhenomenonMapper.updateByExampleSelective(waterDeviceFailurePhenomenon, example);
    }

}
