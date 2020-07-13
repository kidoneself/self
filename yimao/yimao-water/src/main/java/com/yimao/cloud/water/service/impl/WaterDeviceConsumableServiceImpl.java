package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.WaterDeviceConsumableMapper;
import com.yimao.cloud.water.po.WaterDeviceConsumable;
import com.yimao.cloud.water.service.WaterDeviceConsumableService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：水机耗材。
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Service
public class WaterDeviceConsumableServiceImpl implements WaterDeviceConsumableService {

    @Resource
    private UserCache userCache;
    @Resource
    private WaterDeviceConsumableMapper waterDeviceConsumableMapper;

    /**
     * 创建水机耗材
     *
     * @param consumable 耗材
     */
    @Override
    public void save(WaterDeviceConsumable consumable) {
        this.check(consumable);
        consumable.setCreator(userCache.getCurrentAdminRealName());
        consumable.setCreateTime(new Date());
        consumable.setId(null);
        waterDeviceConsumableMapper.insert(consumable);
    }

    /**
     * 删除水机耗材
     *
     * @param id 耗材ID
     */
    @Override
    public void delete(Integer id) {
        waterDeviceConsumableMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改水机耗材
     *
     * @param consumable 耗材
     */
    @Override
    public void update(WaterDeviceConsumable consumable) {
        this.check(consumable);
        consumable.setUpdater(userCache.getCurrentAdminRealName());
        consumable.setUpdateTime(new Date());
        waterDeviceConsumableMapper.updateByPrimaryKeySelective(consumable);
    }

    /**
     * 查询水机耗材（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param type     耗材类型：1-滤芯 2-滤网
     * @param model    每页大小
     */
    @Override
    public PageVO<WaterDeviceConsumableDTO> page(Integer pageNum, Integer pageSize, Integer type, String model) {
        Example example = new Example(WaterDeviceConsumable.class);
        Example.Criteria criteria = example.createCriteria();
        if (type != null) {
            //criteria.andEqualTo("typeId", typeId);
            criteria.andEqualTo("type", type);
        }
        if (StringUtil.isNotBlank(model)) {
            criteria.andEqualTo("deviceModel", model);
        }
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceConsumable> page = (Page<WaterDeviceConsumable>) waterDeviceConsumableMapper.selectByExample(example);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page, WaterDeviceConsumable.class, WaterDeviceConsumableDTO.class);
    }


    /**
     * @description   根据百得耗材id查询耗材
     * @author Liu Yi
     * @date 2019/10/21 18:48
     * @param
     * @return com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO
     */
    @Override
    public WaterDeviceConsumableDTO getConsumableByOldId(String oldId) {
        Example example = new Example(WaterDeviceConsumable.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isBlank(oldId)) {
            throw new BadRequestException("缺失原百得耗材id。");
        }
        criteria.andEqualTo("oldId", oldId);
        WaterDeviceConsumable consumable =  waterDeviceConsumableMapper.selectOneByExample(example);

        if(consumable==null){
            return null;
        }
        WaterDeviceConsumableDTO dto=new WaterDeviceConsumableDTO();
        consumable.convert(dto);
        // PO对象转成DTO对象
        return dto;
    }

    /**
     * 参数校验
     *
     * @param consumable 耗材
     */
    private void check(WaterDeviceConsumable consumable) {
        if (StringUtil.isBlank(consumable.getName())) {
            throw new BadRequestException("请输入耗材名称。");
        }
        if (consumable.getType() == null) {
            throw new BadRequestException("请选择耗材对应的类型。");
        }
        if (StringUtil.isBlank(consumable.getDeviceModel())) {
            throw new BadRequestException("请选择耗材对应的水机型号。");
        }
        if (consumable.getTime() == null) {
            throw new BadRequestException("请输入耗材使用有效时长。");
        }
        if (consumable.getFlow() == null) {
            throw new BadRequestException("请输入耗材使用有效流量。");
        }
    }

    /**
     * 查询水机耗材
     *
     * @param deviceModel 设备型号
     */
    @Override
    public List<WaterDeviceConsumable> listByDeviceModel(String deviceModel) {
        WaterDeviceConsumable query = new WaterDeviceConsumable();
        query.setDeviceModel(deviceModel);
        return waterDeviceConsumableMapper.select(query);
    }

    /**
     * 查询水机耗材
     *
     * @param deviceModel 设备型号
     */
    @Override
    public List<WaterDeviceConsumableDTO> listByDeviceModelForFilterSetting(String deviceModel) {
        return waterDeviceConsumableMapper.listByDeviceModelForFilterSetting(deviceModel);
    }

}
