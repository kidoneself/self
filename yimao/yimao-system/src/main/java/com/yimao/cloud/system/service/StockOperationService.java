package com.yimao.cloud.system.service;

import com.yimao.cloud.pojo.dto.system.StockOperationDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.po.StockOperation;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public interface StockOperationService {

    /**
     * 分页查询库存操作记录列表
     */
    PageVO<StockOperationDTO> getStockOperationList(StockOperation stockOperation, Date startTime, Date endTime, Integer pageNum, Integer pageSize);

    /**
     * 分页查询库存操作记录列表
     *//*
    void exportStockOperation(StockOperation stockOperation, Date startTime, Date endTime, HttpServletResponse response);*/
}
