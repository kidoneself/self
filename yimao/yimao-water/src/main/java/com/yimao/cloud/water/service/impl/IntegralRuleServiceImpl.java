package com.yimao.cloud.water.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.IntegralRuleStatus;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.framework.aop.OperationType;
import com.yimao.cloud.framework.aop.annotation.EnableOperationLog;
import com.yimao.cloud.framework.aop.parser.DefaultContentParse;
import com.yimao.cloud.pojo.dto.water.IntegralConfigurationDTO;
import com.yimao.cloud.pojo.dto.water.IntegralRuleDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.water.mapper.IntegralRuleMapper;
import com.yimao.cloud.water.po.IntegralDetail;
import com.yimao.cloud.water.po.IntegralRule;
import com.yimao.cloud.water.service.IntegralConfigurationService;
import com.yimao.cloud.water.service.IntegralRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * 功能描述:积分规则
 *
 * @auther: liu yi
 * @date: 2019/6/3 16:39
 */
@Service
@Slf4j
public class IntegralRuleServiceImpl implements IntegralRuleService {
    @Resource
    private UserCache userCache;
    @Resource
    private IntegralRuleMapper integralRuleMapper;
    @Resource
    private IntegralConfigurationService integralConfigurationService;

    /**
     * 创建积分规则信息
     *
     * @param dto 积分规则信息
     */
    @EnableOperationLog(
            name = "创建积分规则",
            type = OperationType.SAVE,
            daoClass = IntegralRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"name"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void save(IntegralRuleDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传入参数不能为空！");
        }

        IntegralRule rule = new IntegralRule(dto);
        rule.setStatus(IntegralRuleStatus.NOTONSHELF.value);
        String user = userCache.getCurrentAdminRealName();
        rule.setCreator(user);
        rule.setCreateTime(new Date());
        rule.setId(null);
        integralRuleMapper.insert(rule);

        List<IntegralConfigurationDTO> list = dto.getIntegralConfigurationList();
        for (IntegralConfigurationDTO configuration : list) {
            configuration.setId(null);
            configuration.setIntegralRuleId(rule.getId());
            integralConfigurationService.save(configuration);
        }
    }

    /**
     * 根据ID获取积分规则信息
     *
     * @param id 积分规则ID
     */
    @Override
    public IntegralRuleDTO getById(Integer id) {
        if (id == null) {
            throw new BadRequestException("传入参数id不能为空！");
        }

        IntegralRule rule = integralRuleMapper.selectByPrimaryKey(id);
        IntegralRuleDTO dto = new IntegralRuleDTO();
        rule.convert(dto);

        return dto;
    }

    /**
     * 根据ID获取积分规则信息
     *
     * @param ids 积分规则ID
     */
    @Override
    public List<IntegralRuleDTO> getByIds(List<Integer> ids) {
        if (ids == null) {
            throw new BadRequestException("传入参数id不能为空！");
        }

        Example example = new Example(IntegralDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        List<IntegralRule> ruleList = integralRuleMapper.selectByExample(example);
        List<IntegralConfigurationDTO> list;
        List<IntegralRuleDTO> dtoList = new ArrayList<>();
        IntegralRuleDTO dto;
        for (IntegralRule rule : ruleList) {
            dto = new IntegralRuleDTO();
            rule.convert(dto);
            list = integralConfigurationService.getByRuleId(rule.getId(), null);
            dto.setIntegralConfigurationList(list);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 更新
     *
     * @param dto 积分规则
     */
    @EnableOperationLog(
            name = "更新积分规则",
            type = OperationType.UPDATE,
            daoClass = IntegralRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "name"},
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void update(IntegralRuleDTO dto) {
        if (dto == null) {
            throw new BadRequestException("传入对象不能为空！");
        }
        IntegralRuleDTO dtotemp = getById(dto.getId());
        if (IntegralRuleStatus.ONSHELF.value == dto.getStatus()) {
            Example example = new Example(IntegralRule.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", IntegralRuleStatus.ONSHELF.value);
            List<IntegralRule> list = integralRuleMapper.selectByExample(example);
            for(IntegralRule rule:list){
                if (new Date().compareTo(rule.getActivityEndTime()) <= 0) {
                    throw new YimaoException("目前已有生效中的规则，请先下架该规则！");
                }
            }

            int compareTo = new Date().compareTo(dtotemp.getActivityStartTime());
            if (compareTo > 0) {
                throw new BadRequestException("当前时间不能大于活动开始时间!");
            }
        }
        IntegralRule rule = new IntegralRule(dto);
        String user = userCache.getCurrentAdminRealName();
        rule.setUpdater(user);
        rule.setUpdateTime(new Date());
        integralRuleMapper.updateByPrimaryKeySelective(rule);
    }

    /**
     * 根据条件分页获取信息
     *
     * @param pageNum  页码
     * @param pageSize 页面大小
     * @param name
     * @param status
     */
    @Override
    public PageVO<IntegralRuleDTO> page(Integer pageNum, Integer pageSize, String name, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(IntegralRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("status", IntegralRuleStatus.DELETED.value);
        if (StringUtil.isNotBlank(name)) {
            criteria.andEqualTo("name", name);
        }
        if (status != null) {
            /*List<Integer> statusList = StringUtil.convertProcesIds(status);
            criteria.andIn("status", statusList);*/
            criteria.andEqualTo("status", status);
        }
        example.orderBy("status").asc();
        Page<IntegralRule> page = (Page<IntegralRule>) integralRuleMapper.selectByExample(example);

        return new PageVO<>(pageNum, page, IntegralRule.class, IntegralRuleDTO.class);
    }

    /**
     * 根据当前生效的积分规则
     */
    @Override
    public IntegralRuleDTO getEffectiveIntegralRule() {
        Example example = new Example(IntegralRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", IntegralRuleStatus.ONSHELF.value);
        criteria.andGreaterThanOrEqualTo("activityEndTime", new Date());
        List<IntegralRule> list = integralRuleMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            IntegralRule rule = list.get(0);
            IntegralRuleDTO dto = new IntegralRuleDTO();
            rule.convert(dto);
            dto.setStatusText("生效中");
            List<IntegralConfigurationDTO> configurationList = integralConfigurationService.getByRuleId(rule.getId(), null);
            dto.setIntegralConfigurationList(configurationList);

            return dto;
        }
        return null;
    }

    /**
     * 根据积分规则ID删除积分规则
     *
     * @param rule 积分规则ID
     */
    @EnableOperationLog(
            name = "根据积分规则ID删除积分规则",
            type = OperationType.DELETE,
            daoClass = IntegralRuleMapper.class,
            parseClass = DefaultContentParse.class,
            index = 0,
            queue = RabbitConstant.WATER_OPERATION_LOG
    )
    @Override
    public void deleteIntegralRule(IntegralRule rule) {
        rule.setStatus(IntegralRuleStatus.DELETED.value);
        int result = integralRuleMapper.updateByPrimaryKeySelective(rule);
        if (result < 1) {
            throw new YimaoException("删除积分规则失败！");
        }
    }
}
