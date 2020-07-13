package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.InvestmentIncomeRecordPart;
import com.yimao.cloud.pojo.dto.order.IncomeRecordPartResultDTO;
import com.yimao.cloud.pojo.dto.order.InvestmentIncomeRecordPartDTO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InvestmentIncomeRecordPartMapper extends Mapper<InvestmentIncomeRecordPart> {

    List<IncomeRecordPartResultDTO> getInvestmentIncomeRecordByPid(Integer incomeRecordId);

    Integer insertBatch(List<InvestmentIncomeRecordPart> list);
}