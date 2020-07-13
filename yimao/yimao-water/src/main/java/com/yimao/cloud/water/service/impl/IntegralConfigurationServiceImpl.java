package com.yimao.cloud.water.service.impl;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.IntegralConfigurationDTO;
import com.yimao.cloud.water.mapper.IntegralConfigurationMapper;
import com.yimao.cloud.water.po.IntegralConfiguration;
import com.yimao.cloud.water.service.IntegralConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/***
 * 功能描述:积分规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 16:39
 */
@Service
@Slf4j
public class IntegralConfigurationServiceImpl implements IntegralConfigurationService {
    @Resource
    private IntegralConfigurationMapper integralConfigurationMapper;

    /**
     * 创建积分规则信息
     *
     * @param dto 积分规则信息
     */
    @EnableOperationLog(
            name = "新增积分规则配置",
            type = OperationType.SAVE,
            daoClass = IntegralConfigurationMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"integralRuleId"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(IntegralConfigurationDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传入参数不能为空！");
        }
        IntegralConfiguration configuration=new IntegralConfiguration(dto);

        integralConfigurationMapper.insert(configuration);
    }

    /**
     * 根据ID获取积分规则信息
     *
     * @param ruleId 积分规则ID
     */
    @Override
    public List<IntegralConfigurationDTO> getByRuleId(Integer ruleId,Integer type) {
        if (ruleId == null) {
            throw new BadRequestException("传入参数id不能为空！");
        }

        Example example = new Example(IntegralConfiguration.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("integralRuleId", ruleId);
        if(type != null && type !=0){
            criteria.andEqualTo("type", type);
        }

        List<IntegralConfiguration> list = integralConfigurationMapper.selectByExample(example);
        List<IntegralConfigurationDTO> dtoList = new ArrayList<>();
        IntegralConfigurationDTO dto;

        for (IntegralConfiguration configuration : list) {
            dto = new IntegralConfigurationDTO();
            configuration.convert(dto);
            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * 根据ID获取积分规则信息
     *
     * @param ids 积分规则ID
     */
    @Override
    public List<IntegralConfiguration> getByIds(List<Integer> ids) {
        if (ids == null) {
            throw new BadRequestException("传入参数id不能为空！");
        }

        Example example = new Example(IntegralConfiguration.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        List<IntegralConfiguration> configucrationList = integralConfigurationMapper.selectByExample(example);

        return configucrationList;
    }

}
