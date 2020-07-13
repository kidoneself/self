package com.yimao.cloud.water.service;

import com.yimao.cloud.pojo.dto.water.IntegralConfigurationDTO;
import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.po.IntegralConfiguration;
import com.yimao.cloud.water.po.IntegralRule;

import java.util.List;

public interface IntegralConfigurationService {

    /**
     * 创建积分配置
     *
     * @param dto 积分规则
     */
    void save(IntegralConfigurationDTO dto);

    /**
     * 根据ID获取积分规则配置
     *
     * @param ruleId ID
     */
    List<IntegralConfigurationDTO> getByRuleId(Integer ruleId,Integer type);
    /***
     * 功能描述:根据多个id获取规则配置
     *
     * @param: [ids]
     * @auther: liu yi
     * @date: 2019/6/4 16:02
     * @return: java.util.List<com.yimao.cloud.water.po.IntegralConfiguration>
     */
    List<IntegralConfiguration> getByIds(List<Integer> ids);
}
