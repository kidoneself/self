package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.IntegralRule;

import java.util.List;

public interface IntegralRuleService {

    /**
     * 创建积分规则
     *
     * @param dto 积分规则
     */
    void save(IntegralRuleDTO dto);

    /**
     * 根据ID获取积分规则
     *
     * @param id ID
     */
    IntegralRuleDTO getById(Integer id);
    /***
     * 功能描述:根据多个id获取规则
     *
     * @param: [ids]
     * @auther: liu yi
     * @date: 2019/6/4 16:02
     * @return: java.util.List<com.yimao.cloud.water.po.IntegralRule>
     */
    List<IntegralRuleDTO> getByIds(List<Integer> ids);

    /**
     * 更新
     *
     * @param dto
     */
    void update(IntegralRuleDTO dto);

    /**
     * 根据条件分页获取信息
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param name 规则名称
     * @param status 状态
     */
    PageVO<IntegralRuleDTO> page(Integer pageNum, Integer pageSize,String name,Integer status);

    /**
     * 根据ID删除
     *
     * @param rule ID
     */
    void deleteIntegralRule(IntegralRule rule);

    /**
     * 获取当前生效的积分规则
     */
    IntegralRuleDTO getEffectiveIntegralRule();
}
