package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.water.mapper.FlowRuleMapper;
import com.yimao.cloud.water.po.FlowRule;
import com.yimao.cloud.water.service.FlowRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:流量规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 16:39
 */
@Service
@Slf4j
public class FlowRuleServiceImpl implements FlowRuleService {
    @Resource
    private UserCache userCache;
    @Resource
    private RedisCache redisCache;
    @Resource
    private FlowRuleMapper flowRuleMapper;
    private static final String FLOW_RULE_CACHE = "flowRuleCache_";
    /**
     * 创建流量规则信息
     *
     * @param rule 流量规则信息
     */
    @EnableOperationLog(
            name = "新增流量规则",
            type = OperationType.SAVE,
            daoClass = FlowRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(FlowRule rule) {
        if (rule == null) {
            throw new BadRequestException("传入参数不能为空！");
        }

        String user = userCache.getCurrentAdminRealName();
        rule.setCreator(user);
        rule.setCreateTime(new Date());
        rule.setId(null);
        flowRuleMapper.insert(rule);
        redisCache.set(FLOW_RULE_CACHE,rule);
    }

    @Override
    public FlowRule getFlowRule() {
        FlowRule rule=redisCache.get(FLOW_RULE_CACHE,FlowRule.class);
        if(rule==null){
            List<FlowRule> rules = flowRuleMapper.selectAll();
            if (rules == null || rules.size() == 0) {
                return null;
            }
            redisCache.set(FLOW_RULE_CACHE,rules.get(0));
        }
        return redisCache.get(FLOW_RULE_CACHE,FlowRule.class);
    }

    @EnableOperationLog(
            name = "更新流量规则",
            type = OperationType.UPDATE,
            daoClass = FlowRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void update(FlowRule rule) {
        if (rule == null) {
            throw new BadRequestException("传入对象不能为空！");
        }
        if (rule.getId() == null) {
            throw new BadRequestException("传入id不能为空！");
        }
        if (rule.getMinFlow() != null && rule.getMaxFlow() != null) {
            if (rule.getMinFlow() > rule.getMaxFlow()) {
                throw new BadRequestException("流量值最小值不能大于最大值！");
            }
        }

        String user = userCache.getCurrentAdminRealName();
        rule.setUpdater(user);
        rule.setUpdateTime(new Date());
        flowRuleMapper.updateByPrimaryKeySelective(rule);

        redisCache.set(FLOW_RULE_CACHE,rule);
    }
}
