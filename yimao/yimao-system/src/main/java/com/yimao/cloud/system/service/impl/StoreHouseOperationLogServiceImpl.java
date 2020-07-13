package com.yimao.cloud.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.pojo.dto.system.StoreHouseOperationLogDTO;
import com.yimao.cloud.pojo.query.system.StoreHouseOperationLogQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.mapper.StoreHouseOperationLogMapper;
import com.yimao.cloud.system.po.StoreHouseOperationLog;
import com.yimao.cloud.system.service.StoreHouseOperationLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StoreHouseOperationLogServiceImpl implements StoreHouseOperationLogService {

    @Resource
    private StoreHouseOperationLogMapper storeHouseOperationLogMapper;

    /**
     * 保存操作记录
     *
     * @param dto
     */
    @Override
    public void save(StoreHouseOperationLogDTO dto) {
        StoreHouseOperationLog storeHouseOperationLog = new StoreHouseOperationLog(dto);
        storeHouseOperationLogMapper.insertSelective(storeHouseOperationLog);
    }

    @Override
    public PageVO<StoreHouseOperationLogDTO> page(StoreHouseOperationLogQuery query, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<StoreHouseOperationLogDTO> page = storeHouseOperationLogMapper.page(query);
        return new PageVO<>(pageNum, page);
    }
}
