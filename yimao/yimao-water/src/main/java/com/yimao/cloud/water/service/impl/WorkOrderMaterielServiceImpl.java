package com.yimao.cloud.water.service.impl;


import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceWorkOrderMaterielDTO;
import com.yimao.cloud.water.mapper.WaterDeviceWorkOrderMaterielMapper;
import com.yimao.cloud.water.po.WaterDeviceWorkOrderMateriel;
import com.yimao.cloud.water.service.WorkOrderMaterielService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:工单耗材服务
 *
 * @param:
 * @auther: liu yi
 * @date: 2019/3/28 10:28
 */
@Service
@Slf4j
public class WorkOrderMaterielServiceImpl implements WorkOrderMaterielService {
    @Resource
    private WaterDeviceWorkOrderMaterielMapper waterDeviceWorkOrderMaterielMapper;

    @Override
    public WaterDeviceWorkOrderMaterielDTO getById(Integer id) {
        if (null ==id) {
            throw new BadRequestException("id不能为空！");
        }

        WaterDeviceWorkOrderMaterielDTO waterDeviceWorkOrderMaterielDTO = new WaterDeviceWorkOrderMaterielDTO();
        WaterDeviceWorkOrderMateriel entity = this.waterDeviceWorkOrderMaterielMapper.selectByPrimaryKey(id);
        entity.convert(waterDeviceWorkOrderMaterielDTO);
        return waterDeviceWorkOrderMaterielDTO;
    }

    @Override
    public void create(WaterDeviceWorkOrderMaterielDTO waterDeviceWorkOrderMaterielDTO) {
        WaterDeviceWorkOrderMateriel waterDeviceWorkOrderMateriel = new WaterDeviceWorkOrderMateriel(waterDeviceWorkOrderMaterielDTO);
        waterDeviceWorkOrderMateriel.setWorkOrderIndex(WorkOrderTypeEnum.ORDER_TYPE_REPAIR.getType());
        waterDeviceWorkOrderMateriel.setDelStatus(StatusEnum.NO.value());
        waterDeviceWorkOrderMateriel.setIdStatus(StatusEnum.YES.value());
        int result = this.waterDeviceWorkOrderMaterielMapper.insert(waterDeviceWorkOrderMateriel);
        if (result < 1) {
            throw new YimaoException("创建工单耗材信息失败");
        }
    }

    @Override
    public void update(WaterDeviceWorkOrderMaterielDTO dto) {
        if (null == dto) {
            throw new BadRequestException("操作失败，更新对象不能为空！");
        }
        if (null == dto.getId()) {
            throw new BadRequestException("操作失败，更新对象主键不能为空！");
        }

        WaterDeviceWorkOrderMateriel materiel = new WaterDeviceWorkOrderMateriel(dto);
        Integer result = waterDeviceWorkOrderMaterielMapper.updateByPrimaryKeySelective(materiel);

        if (result < 1) {
            throw new BadRequestException("更新工单耗材信息失败！");
        }
    }

    @Override
    public List<WaterDeviceWorkOrderMaterielDTO> getWaterDeviceWorkOrderMaterielByWorkCode(String workCode, String workOrderIndex) {
        Example example = new Example(WaterDeviceWorkOrderMateriel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workCode);
        criteria.andEqualTo("workOrderIndex", workOrderIndex);
        criteria.andEqualTo("delStatus", StatusEnum.NO.value());

        List<WaterDeviceWorkOrderMateriel> list = this.waterDeviceWorkOrderMaterielMapper.selectByExample(example);
        List<WaterDeviceWorkOrderMaterielDTO> dtoList = new ArrayList<>();
        WaterDeviceWorkOrderMaterielDTO dto;

        for (WaterDeviceWorkOrderMateriel waterDeviceWorkOrderMateriel : list) {
            dto = new WaterDeviceWorkOrderMaterielDTO();
            waterDeviceWorkOrderMateriel.convert(dto);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public void delete(Integer id) {
        WaterDeviceWorkOrderMateriel materiel = new WaterDeviceWorkOrderMateriel();
        materiel.setId(id);
        materiel.setDelStatus(StatusEnum.YES.value());
        materiel.setDeleteTime(new Date());
        waterDeviceWorkOrderMaterielMapper.updateByPrimaryKeySelective(materiel);
    }

    @Override
    public void deleteByWorkCode(String workcode, String workOrderIndex) {
        Example example = new Example(WaterDeviceWorkOrderMateriel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("workCode", workcode);
        criteria.andEqualTo("workOrderIndex", workOrderIndex);

        WaterDeviceWorkOrderMateriel materiel = new WaterDeviceWorkOrderMateriel();
        materiel.setDelStatus(StatusEnum.YES.value());
        materiel.setDeleteTime(new Date());
        waterDeviceWorkOrderMaterielMapper.updateByExampleSelective(materiel, example);
    }

    @Override
    public void batchCreate(List<WaterDeviceWorkOrderMaterielDTO> materiels) {
        for (WaterDeviceWorkOrderMaterielDTO dto : materiels) {
            WaterDeviceWorkOrderMateriel materiel = new WaterDeviceWorkOrderMateriel(dto);
            materiel.setDelStatus(StatusEnum.NO.value());
            materiel.setIdStatus(StatusEnum.YES.value());
            this.waterDeviceWorkOrderMaterielMapper.insert(materiel);
        }
    }
}
