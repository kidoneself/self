package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.order.po.ServiceIncomeRecord;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ServiceIncomeRecordMapper extends Mapper<ServiceIncomeRecord>{
    Page<ProductIncomeVO> queryServiceIncomeList(ProductIncomeQueryDTO productIncomQueryDTO);

    IncomeRecordResultDTO selectIncomeRecordByPrimaryKey(Integer id);

    Page<IncomeExportDTO> serviceIncomeExport(ProductIncomeQueryDTO query);
}