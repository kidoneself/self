package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.pojo.dto.water.WaterDeviceReplaceRecordDTO;
import com.yimao.cloud.pojo.export.water.DeviceReplaceRecordExport;
import com.yimao.cloud.pojo.query.water.WaterDeviceReplaceRecordQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.WaterDeviceReplaceRecordMapper;
import com.yimao.cloud.water.po.WaterDeviceReplaceRecord;
import com.yimao.cloud.water.service.WaterDeviceReplaceRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：水机设备更换记录。
 *
 * @Author Zhang Bo
 * @Date 2019/4/23
 */
@Service
public class WaterDeviceReplaceRecordServiceImpl implements WaterDeviceReplaceRecordService {

    @Resource
    private WaterDeviceReplaceRecordMapper waterDeviceReplaceRecordMapper;

    /**
     * 查询水机设备更换记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<WaterDeviceReplaceRecordDTO> page(Integer pageNum, Integer pageSize, WaterDeviceReplaceRecordQuery query) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceReplaceRecordDTO> page = waterDeviceReplaceRecordMapper.selectPage(query);
        // PO对象转成DTO对象
        return new PageVO<>(pageNum, page);
    }

    /**
     * 查询水机设备更换记录详情
     *
     * @param id 设备ID
     */
    @Override
    public WaterDeviceReplaceRecord getDetailById(Integer id) {
        return waterDeviceReplaceRecordMapper.selectByPrimaryKey(id);
    }

    /**
     * 水机设备更换记录导出
     *
     * @param query 查询条件
     */
    @Override
    public List<DeviceReplaceRecordExport> export(WaterDeviceReplaceRecordQuery query) {
        return waterDeviceReplaceRecordMapper.export(query);
    }

}
