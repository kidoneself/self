package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.water.po.WaterDevicePlaceChangeRecord;

public interface WaterDevicePlaceChangeRecordService {

    /**
     * 保存水机摆放位置更换记录
     *
     * @param placeChangeRecord 水机摆放位置更换记录
     */
    void save(WaterDevicePlaceChangeRecord placeChangeRecord);

    /**
     * 更新水机摆放位置更换记录
     *
     * @param placeChangeRecord 水机摆放位置更换记录
     */
    void update(WaterDevicePlaceChangeRecordDTO placeChangeRecord);

    /**
     * 查询水机摆放位置更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    PageVO<WaterDevicePlaceChangeRecordVO> page(Integer pageNum, Integer pageSize, String sn);

    /**
     * 根据SN码查询水机摆放位置更换记录
     *
     * @param sn SN码
     */
    WaterDevicePlaceChangeRecordDTO getBySn(String sn);

}
