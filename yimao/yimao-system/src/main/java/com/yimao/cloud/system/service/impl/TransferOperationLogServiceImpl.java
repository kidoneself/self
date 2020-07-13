package com.yimao.cloud.system.service.impl;

import javax.annotation.Resource;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.pojo.dto.system.StationDTO;
import com.yimao.cloud.pojo.query.system.TransferOperationLogQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.stereotype.Service;

import com.yimao.cloud.pojo.dto.system.TransferOperationLogDTO;
import com.yimao.cloud.system.mapper.TransferOperationLogMapper;
import com.yimao.cloud.system.po.TransferOperationLog;
import com.yimao.cloud.system.service.TransferOperationLogService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransferOperationLogServiceImpl implements TransferOperationLogService {

    @Resource
    private TransferOperationLogMapper transferOperationLogMapper;

    /**
     * 保存服务区域承包转让操作日志
     *
     * @param dto
     */
    @Override
    public void save(TransferOperationLogDTO dto) {
    	TransferOperationLog trans = new TransferOperationLog(dto);
    	transferOperationLogMapper.insertSelective(trans);
    }

    @Override
    public PageVO<TransferOperationLogDTO> page(Integer pageNum, Integer pageSize, TransferOperationLogQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TransferOperationLogDTO> page = transferOperationLogMapper.page(query);
        return new PageVO<>(pageNum, page);
    }
}
