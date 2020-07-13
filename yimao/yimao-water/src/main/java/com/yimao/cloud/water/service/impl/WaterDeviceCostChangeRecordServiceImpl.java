package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.water.WaterDeviceCostChangeRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.feign.ProductFeign;
import com.yimao.cloud.water.mapper.WaterDeviceCostChangeRecordMapper;
import com.yimao.cloud.water.po.WaterDeviceCostChangeRecord;
import com.yimao.cloud.water.service.WaterDeviceCostChangeRecordService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@Service
public class WaterDeviceCostChangeRecordServiceImpl implements WaterDeviceCostChangeRecordService {

    @Resource
    private WaterDeviceCostChangeRecordMapper waterDeviceCostChangeRecordMapper;

    @Resource
    private ProductFeign productFeign;

    /**
     * 查询水机计费方式修改记录（分页）
     *
     * @param pageNum  第几页
     * @param pageSize 每页大小
     * @param sn       SN码
     */
    @Override
    public PageVO<WaterDeviceCostChangeRecordDTO> page(Integer pageNum, Integer pageSize, String sn) {
        Example example = new Example(WaterDeviceCostChangeRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(sn)) {
            criteria.andEqualTo("sn", sn);
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<WaterDeviceCostChangeRecord> page = (Page<WaterDeviceCostChangeRecord>) waterDeviceCostChangeRecordMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, WaterDeviceCostChangeRecord.class, WaterDeviceCostChangeRecordDTO.class);
    }

}
