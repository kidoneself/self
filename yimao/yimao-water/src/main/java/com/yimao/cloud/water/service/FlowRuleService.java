package com.yimao.cloud.water.service;

import com.yimao.cloud.water.po.FlowRule;

public interface FlowRuleService {

    /**
     * 创建流量规则
     *
     * @param rule 流量规则
     */
    void save(FlowRule rule);

    /**
     * 获取流量规则
     *
     */
    FlowRule getFlowRule();

    /**
     * 更新流量规则
     *
     * @param rule
     */
    void update(FlowRule rule);

}
