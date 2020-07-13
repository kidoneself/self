package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherRecordDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.WaterDeviceDynamicCipherConfigMapper;
import com.yimao.cloud.water.mapper.WaterDeviceDynamicCipherRecordMapper;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherConfig;
import com.yimao.cloud.water.po.WaterDeviceDynamicCipherRecord;
import com.yimao.cloud.water.service.WaterDeviceDynamicCipherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@Service
@Slf4j
public class WaterDeviceDynamicCipherServiceImpl implements WaterDeviceDynamicCipherService {

    @Resource
    private WaterDeviceDynamicCipherRecordMapper waterDeviceDynamicCipherRecordMapper;
    @Resource
    private WaterDeviceDynamicCipherConfigMapper waterDeviceDynamicCipherConfigMapper;

    @Override
    public void saveDynamicCipherRecord(WaterDeviceDynamicCipherRecord record) {
        waterDeviceDynamicCipherRecordMapper.insert(record);
    }

    @Override
    public void updateDynamicCipherRecord(WaterDeviceDynamicCipherRecord record) {
        waterDeviceDynamicCipherRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public WaterDeviceDynamicCipherRecord getDynamicCipherRecordBySnCode(String sn) {
        Example example = new Example(WaterDeviceDynamicCipherRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sn", sn);
        criteria.andEqualTo("validStatus", "Y");
        criteria.andGreaterThan("validTime", new Date());
        //按创建时间倒序
        example.orderBy("createTime").desc();
        List<WaterDeviceDynamicCipherRecord> recordList = waterDeviceDynamicCipherRecordMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(recordList)) {
            return recordList.get(0);
        }
        return null;
    }

    @Override
    public PageVO<WaterDeviceDynamicCipherRecordDTO> pageDynamicCipherRecord(Integer pageNum, Integer pageSize, Integer engineerId) {
        Example example = new Example(WaterDeviceDynamicCipherRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("engineerId", engineerId);
        criteria.andEqualTo("validStatus", "Y");
        criteria.andGreaterThan("validTime", new Date());
        PageHelper.startPage(pageNum, pageSize);
        example.orderBy("createTime").desc();
        Page<WaterDeviceDynamicCipherRecord> page = (Page<WaterDeviceDynamicCipherRecord>) waterDeviceDynamicCipherRecordMapper.selectByExample(example);
        return new PageVO<>(pageNum, page, WaterDeviceDynamicCipherRecord.class, WaterDeviceDynamicCipherRecordDTO.class);
    }

    @Override
    public WaterDeviceDynamicCipherConfig getDynamicCipherConfig() {
        WaterDeviceDynamicCipherConfig query = new WaterDeviceDynamicCipherConfig();
        query.setDeleted(false);
        WaterDeviceDynamicCipherConfig config = waterDeviceDynamicCipherConfigMapper.selectOne(query);
        if (Objects.nonNull(config)) {
            return config;
        }
        return null;
    }

    /**
     * 还在有效期内的密码全部置为无效
     */
    @Override
    public void setDeviceAllDynamicCipherInValid(String sn) {
        Example example = new Example(WaterDeviceDynamicCipherRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sn", sn);
        criteria.andEqualTo("validStatus", "Y");

        WaterDeviceDynamicCipherRecord record = new WaterDeviceDynamicCipherRecord();
        record.setValidStatus("N");
        waterDeviceDynamicCipherRecordMapper.updateByExampleSelective(record, example);
    }

}
