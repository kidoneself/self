package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.IncomeRule;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 收益规则服务
 *
 * @author Liu Yi
 * @date 2019/1/14.
 */
public interface IncomeRuleService {

    /**
     * 创建收益分配规则
     *
     * @param incomeRule      收益分配规则
     * @param incomeRuleParts 收益分配规则所含收益主体
     */
    void saveIncomeRule(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts);

    /**
     * 查询收益分配规则（单个）
     *
     * @param id 收益分配规则ID
     */
    IncomeRuleDTO getIncomeRuleById(Integer id);

    /**
     * 查询收益分配规则
     *
     * @param ids        收益分配规则ID
     * @param incomeType 1-产品收益；2-续费收益；3-服务收益；4-招商收益
     */
    IncomeRuleDTO getIncomeRuleByIdAndIncomeType(Set<Integer> ids, Integer incomeType);

    /**
     * 查询收益分配规则（条件、分页）
     *
     * @param pageNum    页码
     * @param pageSize   分页大小
     * @param id         收益分配规则ID
     * @param name       收益分配规则名称
     * @param incomeType 收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；
     * @param allotType  分配类型：1-按比例分配；2-按金额分配；
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    PageVO<IncomeRuleDTO> listIncomeRule(Integer pageNum, Integer pageSize, Integer id, String name, Integer incomeType, Integer allotType, Date startTime, Date endTime);

    /**
     * 根据产品ID获取收益分配规则列表
     *
     * @param productId 产品ID
     */
    List<IncomeRuleDTO> listIncomeRuleByProductId(Integer productId);

    /**
     * 删除收益分配规则
     *
     * @param incomeRule 收益分配规则ID
     * @return void
     * @author hhf
     * @date 2019/4/23
     */
    void delete(IncomeRule incomeRule);

    void update(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts);
}
