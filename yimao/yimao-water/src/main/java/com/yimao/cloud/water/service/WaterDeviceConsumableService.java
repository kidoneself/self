package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.WaterDeviceConsumable;

import java.util.List;

public interface WaterDeviceConsumableService {

    /**
     * 创建水机耗材
     *
     * @param consumable 耗材
     */
    void save(WaterDeviceConsumable consumable);

    /**
     * 删除水机耗材
     *
     * @param id 耗材ID
     */

    void delete(Integer id);

    /**
     * 修改水机耗材
     *
     * @param consumable 耗材
     */
    void update(WaterDeviceConsumable consumable);

    /**
     * 查询水机耗材（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param typeId   每页大小
     * @param model    每页大小
     */
    PageVO<WaterDeviceConsumableDTO> page(Integer pageNum, Integer pageSize, Integer typeId, String model);

    /**
     * 查询水机耗材
     *
     * @param deviceModel 设备型号
     */
    List<WaterDeviceConsumable> listByDeviceModel(String deviceModel);

    /**
     * 查询水机耗材-滤芯参数配置模块用
     *
     * @param deviceModel 设备型号
     */
    List<WaterDeviceConsumableDTO> listByDeviceModelForFilterSetting(String deviceModel);

    /**
     * @description   根据百得耗材id查询耗材
     * @author Liu Yi
     * @date 2019/10/21 18:48
     * @param
     * @return com.yimao.cloud.pojo.dto.water.WaterDeviceConsumableDTO
     */
    WaterDeviceConsumableDTO getConsumableByOldId(String oldId);

}
