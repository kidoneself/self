package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.order.po.InvestmentIncomeRecord;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.export.order.InvestmentIncomeExportDTO;
import com.yimao.cloud.pojo.vo.order.InvestmentIncomeVO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InvestmentIncomeRecordMapper extends Mapper<InvestmentIncomeRecord> {

    Page<InvestmentIncomeVO> queryInvestmentIncomeList(InvestmentIncomeQueryDTO investmentIncomQueryDTO);

    IncomeRecordResultDTO selectIncomeRecordByPrimaryKey(Integer id);

    Page<InvestmentIncomeExportDTO> exportInvestmentIncome(InvestmentIncomeQueryDTO incomeQueryDTO);
}