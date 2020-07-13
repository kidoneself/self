package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.IncomeRule;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface IncomeRuleMapper extends Mapper<IncomeRule> {

    /**
     * 查询收益分配规则（条件、分页）
     *
     * @param id         收益分配规则ID
     * @param name       收益分配规则名称
     * @param incomeType 收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；
     * @param allotType  分配类型：1-按比例分配；2-按金额分配；
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    Page<IncomeRuleDTO> listIncomeRuleWithPart(@Param("id") Integer id,
                                               @Param("name") String name,
                                               @Param("incomeType") Integer incomeType,
                                               @Param("allotType") Integer allotType,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime);

    /**
     * 根据产品ID获取收益分配规则列表
     *
     * @param productId 产品ID
     */
    List<IncomeRuleDTO> listIncomeRuleByProductId(@Param("productId") Integer productId);

}
