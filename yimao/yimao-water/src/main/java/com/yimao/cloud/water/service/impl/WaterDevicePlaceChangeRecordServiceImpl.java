package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.pojo.dto.water.WaterDevicePlaceChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.water.WaterDevicePlaceChangeRecordVO;
import com.yimao.cloud.water.mapper.WaterDevicePlaceChangeRecordMapper;
import com.yimao.cloud.water.po.WaterDevicePlaceChangeRecord;
import com.yimao.cloud.water.service.WaterDevicePlaceChangeRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 描述：水机摆放位置更换记录
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@Service
public class WaterDevicePlaceChangeRecordServiceImpl implements WaterDevicePlaceChangeRecordService {

    @Resource
    private WaterDevicePlaceChangeRecordMapper waterDevicePlaceChangeRecordMapper;

    /**
     * 保存水机摆放位置更换记录
     *
     * @param placeChangeRecord 水机摆放位置更换记录
     */
    @Override
    public void save(WaterDevicePlaceChangeRecord placeChangeRecord) {
        waterDevicePlaceChangeRecordMapper.insert(placeChangeRecord);
    }

    /**
     * 查询水机摆放位置更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @Override
    public PageVO<WaterDevicePlaceChangeRecordVO> page(Integer pageNum, Integer pageSize, String sn) {
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDevicePlaceChangeRecordVO> page = waterDevicePlaceChangeRecordMapper.selectPage(sn);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据SN码查询水机摆放位置更换记录
     *
     * @param sn SN码
     */
    @Override
    public WaterDevicePlaceChangeRecordDTO getBySn(String sn) {
        return waterDevicePlaceChangeRecordMapper.selectBySn(sn);
    }

    /**
     * 保存水机摆放位置更换记录
     *
     * @param placeChangeRecord 水机摆放位置更换记录
     */
    @Override
    public void update(WaterDevicePlaceChangeRecordDTO placeChangeRecord) {
        if (placeChangeRecord == null) {
            throw new YimaoException("水机摆放位置更换记录不能为空！");
        }
        waterDevicePlaceChangeRecordMapper.updateByPrimaryKeySelective(new WaterDevicePlaceChangeRecord(placeChangeRecord));
    }

}
