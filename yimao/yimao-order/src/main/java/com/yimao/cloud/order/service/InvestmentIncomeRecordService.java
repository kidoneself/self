package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.query.order.InvestmentIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.InvestmentIncomeVO;


/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface InvestmentIncomeRecordService {
    /**
     * 招商收益
     * @param dto 经销商订单实体类
     */
    void investmentIncomeAllot(DistributorOrderDTO dto);

    /**
     * 招商收益列表分页查询
     * @param investmentIncomeQueryDTO
     * @param pageNum
     * @param pageSize
     * @return  PageVO<InvestmentIncomeRecordDTO>
     */
    PageVO<InvestmentIncomeVO> page(InvestmentIncomeQueryDTO investmentIncomeQueryDTO, Integer pageNum, Integer pageSize);


    /**
     * 根据id查询招商收益
     *
     * @param id
     * @return InvestmentIncomeRecordDTO
     */
    IncomeRecordResultDTO getInvestmentIncomeById(Integer id);


}
