package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.MaterialsDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.enums.MaterielTypeEnum;
import com.yimao.cloud.water.enums.ScreenLocationEnum;
import com.yimao.cloud.water.mapper.MaterialsMapper;
import com.yimao.cloud.water.po.Materials;
import com.yimao.cloud.water.service.MaterialsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Zhang Bo
 * @Date 2019/1/30 14:50
 * @Version 1.0
 */
@Service
@Slf4j
public class MaterialsServiceImpl implements MaterialsService {

    @Resource
    private MaterialsMapper materialsMapper;
    @Resource
    private UserCache userCache;

    /**
     * 创建物料
     *
     * @param materials
     */
    @EnableOperationLog(
            name = "创建物料",
            type = OperationType.SAVE,
            daoClass = MaterialsMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"screenLocation", "name"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void save(Materials materials) {
        if (Objects.isNull(materials)) {
            throw new BadRequestException("物料信息不能为空。");
        }
        if (Objects.isNull(materials.getScreenLocation())) {
            throw new BadRequestException("请选择屏幕。");
        }
        if (materials.getScreenLocation() != ScreenLocationEnum.ONE.value
                && materials.getScreenLocation() != ScreenLocationEnum.TWO.value) {
            throw new BadRequestException("请正确选择屏幕。");
        }
        if (Objects.isNull(materials.getImage())) {
            throw new BadRequestException("关联图不能为空。");
        }
        if (Objects.isNull(materials.getUrl())) {
            throw new BadRequestException("物料地址不能为空。");
        }
        if (Objects.isNull(materials.getName())) {
            throw new BadRequestException("物料名称不能为空。");
        }
        if (Objects.isNull(materials.getAdvertisers())) {
            throw new BadRequestException("广告主名称不能为空。");
        }

        if (Objects.isNull(materials.getMaterielType())) {
            throw new BadRequestException("物料类型不能为空。");
        }
        if (materials.getMaterielType() != MaterielTypeEnum.ONE.value
                && materials.getMaterielType() != MaterielTypeEnum.TWO.value
                && materials.getMaterielType() != MaterielTypeEnum.THREE.value) {
            throw new BadRequestException("请正确选择物料类型。");
        }
        if (materials.getMaterielType() == MaterielTypeEnum.ONE.value) {
            if (Objects.isNull(materials.getDuration())) {
                throw new BadRequestException("物料时长不能为空。");
            }
        }
        if (materials.getMaterielType() != MaterielTypeEnum.THREE.value) {
            if (Objects.isNull(materials.getSize())) {
                throw new BadRequestException("物料文件大小不能为空。");
            }
        }
        materials.setSpecificationAudit(0);
        materials.setContentAudit(0);
        materials.setPayAudit(0);
        materials.setDeleted(false);
        materials.setCreator(userCache.getCurrentAdminRealName());
        materials.setCreateTime(new Date());
        materialsMapper.insert(materials);

    }

    /**
     * 分页查询物料列表
     *
     * @param materialsDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageVO<MaterialsDTO> list(MaterialsDTO materialsDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<MaterialsDTO> page = materialsMapper.pageQueryList(materialsDTO);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 删除物料信息
     *
     * @param materials
     */
    @EnableOperationLog(
            name = "删除物料信息",
            type = OperationType.DELETE,
            daoClass = MaterialsMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void deleteMaterials(Materials materials) {
        materials.setDeleted(true);
        materials.setUpdater(userCache.getCurrentAdminRealName());
        materials.setUpdateTime(new Date());
        materialsMapper.updateByPrimaryKeySelective(materials);
    }

    /**
     * 审核物料信息
     */
    @EnableOperationLog(
            name = "审核物料信息",
            type = OperationType.UPDATE,
            daoClass = MaterialsMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "payAudit", "contentAudit", "specificationAudit"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void auditMaterials(Materials materials) {
        if (Objects.isNull(materials.getId())) {
            throw new BadRequestException("物料信息ID不能为空。");
        }
        if (Objects.isNull(materials.getPayAudit()) && Objects.isNull(materials.getContentAudit()) && Objects.isNull(materials.getSpecificationAudit())) {
            throw new BadRequestException("审核状态不能为空。");
        }
        materials.setUpdater(userCache.getCurrentAdminRealName());
        materials.setUpdateTime(new Date());
        materialsMapper.updateByPrimaryKeySelective(materials);
    }


    /**
     * 查询物料待审核以及可投放的数量
     *
     * @return
     */
    @Override
    public Map<String, Object> queryCount() {
        Map<String, Object> map = new HashMap<>();

        //查询规格待审核的数量
        Example example = new Example(Materials.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("specificationAudit", 0);
        criteria.andEqualTo("deleted", 0);
        int specificationCount = materialsMapper.selectCountByExample(example);

        //查询支付待审核的数量
        Example example1 = new Example(Materials.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("payAudit", 0);
        criteria1.andEqualTo("deleted", 0);
        int payCount = materialsMapper.selectCountByExample(example1);

        //查询内容待审核的数量
        Example example2 = new Example(Materials.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("contentAudit", 0);
        criteria2.andEqualTo("deleted", 0);
        int contentCount = materialsMapper.selectCountByExample(example2);

        //查询可投数量
        Example example3 = new Example(Materials.class);
        Example.Criteria criteria3 = example3.createCriteria();
        criteria3.andEqualTo("contentAudit", 1);
        criteria1.andEqualTo("payAudit", 1);
        criteria.andEqualTo("specificationAudit", 1);
        criteria3.andEqualTo("deleted", 0);
        int count = materialsMapper.selectCountByExample(example3);

        map.put("specificationCount", specificationCount);
        map.put("payCount", payCount);
        map.put("contentCount", contentCount);
        map.put("count", count);
        return map;
    }
}
