package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.IncomeAllotType;
import com.yimao.cloud.base.enums.IncomeType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.IncomeRuleMapper;
import com.yimao.cloud.order.mapper.IncomeRulePartMapper;
import com.yimao.cloud.order.mapper.IncomeSubjectMapper;
import com.yimao.cloud.order.po.IncomeRule;
import com.yimao.cloud.order.po.IncomeRulePart;
import com.yimao.cloud.order.po.IncomeSubject;
import com.yimao.cloud.order.service.IncomeRuleService;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 收益规则服务
 *
 * @author Liu Yi
 * @date 2019/1/14.
 */
@Service
@Slf4j
public class IncomeRuleServiceImpl implements IncomeRuleService {

    @Resource
    private UserCache userCache;
    @Resource
    private IncomeRuleMapper incomeRuleMapper;
    @Resource
    private IncomeRulePartMapper incomeRulePartMapper;
    @Resource
    private IncomeSubjectMapper incomeSubjectMapper;

    /**
     * 创建收益分配规则
     *
     * @param incomeRule      收益分配规则
     * @param incomeRuleParts 收益分配规则所含收益主体
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void saveIncomeRule(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts) {
        //参数检验
        checkIncomeInfo(incomeRule, incomeRuleParts);
        String creator = userCache.getCurrentAdminRealName();
        Date now = new Date();
        incomeRule.setCreator(creator);
        incomeRule.setCreateTime(now);
        incomeRule.setUpdater(creator);
        incomeRule.setUpdateTime(now);
        incomeRule.setId(null);
        incomeRule.setDeleted(false);
        incomeRuleMapper.insert(incomeRule);
        this.batchUpdateIncomePart(incomeRule, incomeRuleParts, creator, now);
    }

    private void batchUpdateIncomePart(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts, String creator, Date now) {
        List<IncomeRulePart> partList = new ArrayList<>();
        for (IncomeRulePartDTO dto : incomeRuleParts) {
            if (dto.getSubjectId() == null) {
                throw new BadRequestException("收益分配规则所含收益主体错误。");
            }
            IncomeSubject subject = incomeSubjectMapper.selectByPrimaryKey(dto.getSubjectId());
            if (subject == null) {
                throw new BadRequestException("收益分配规则所含收益主体不存在。");
            } else {
                IncomeRulePart part = new IncomeRulePart(dto);
                part.setId(null);
                part.setRuleId(incomeRule.getId());
                part.setSubjectCode(subject.getIncomeSubjectCode());
                part.setDeleted(false);
                part.setCreator(creator);
                part.setCreateTime(now);
                part.setUpdater(creator);
                part.setUpdateTime(now);
                partList.add(part);
            }
        }
        incomeRulePartMapper.batchInsert(partList);
    }

    private void checkIncomeInfo(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts) {
        if (StringUtil.isBlank(incomeRule.getName())) {
            throw new BadRequestException("请填写收益分配规则名称。");
        }
        if (incomeRule.getIncomeType() == null) {
            throw new BadRequestException("请选择收益类型。");
        }
        if (IncomeType.find(incomeRule.getIncomeType()) == null) {
            throw new BadRequestException("收益类型不正确。");
        }
        if (incomeRule.getAllotType() == null) {
            throw new BadRequestException("请选择按比例分配或按金额分配。");
        }
        if (IncomeAllotType.find(incomeRule.getAllotType()) == null) {
            throw new BadRequestException("收益分配类型不正确。");
        }
        if (CollectionUtil.isEmpty(incomeRuleParts)) {
            throw new BadRequestException("请选择收益分配规则所含收益主体。");
        }
    }

    /**
     * 查询收益分配规则（单个）
     *
     * @param id 收益分配规则ID
     */
    @Override
    public IncomeRuleDTO getIncomeRuleById(Integer id) {
        Page<IncomeRuleDTO> page = incomeRuleMapper.listIncomeRuleWithPart(id, null, null, null, null, null);
        List<IncomeRuleDTO> result = page.getResult();
        if (CollectionUtil.isEmpty(result)) {
            return null;
        }
        IncomeRuleDTO dto = result.get(0);
        dto.setIncomeRuleParts(this.getIncomeRulePartByRuleId(dto.getId()));
        return dto;
    }

    /**
     * 查询收益分配规则
     *
     * @param ids        收益分配规则ID
     * @param incomeType 1-产品收益；2-续费收益；3-服务收益；4-招商收益
     */
    @Override
    public IncomeRuleDTO getIncomeRuleByIdAndIncomeType(Set<Integer> ids, Integer incomeType) {
        try {
            Example example = new Example(IncomeRule.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", ids);
            criteria.andEqualTo("incomeType", incomeType);
            criteria.andEqualTo("deleted", 0);
            IncomeRule incomeRule = incomeRuleMapper.selectOneByExample(example);
            if (incomeRule == null) {
                return null;
            }
            IncomeRuleDTO dto = new IncomeRuleDTO();
            incomeRule.convert(dto);
            dto.setIncomeRuleParts(this.getIncomeRulePartByRuleId(dto.getId()));
            return dto;
        } catch (Exception e) {
            return null;
        }
    }

    private List<IncomeRulePartDTO> getIncomeRulePartByRuleId(Integer ruleId) {
        Example example = new Example(IncomeRulePart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ruleId", ruleId);
        criteria.andEqualTo("deleted", 0);
        List<IncomeRulePart> incomeRuleParts = incomeRulePartMapper.selectByExample(example);
        return CollectionUtil.batchConvert(incomeRuleParts, IncomeRulePart.class, IncomeRulePartDTO.class);
    }

    /**
     * 查询收益分配规则（条件、分页）
     *
     * @param pageNum    页码
     * @param pageSize   分页大小
     * @param name       收益分配规则名称
     * @param incomeType 收益类型：1-产品收益；2-续费收益；3-服务收益；4-招商收益；
     * @param allotType  分配类型：1-按比例分配；2-按金额分配；
     * @param startTime  开始时间
     * @param endTime    结束时间
     */
    @Override
    public PageVO<IncomeRuleDTO> listIncomeRule(Integer pageNum, Integer pageSize, Integer id, String name, Integer incomeType, Integer allotType, Date startTime, Date endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<IncomeRuleDTO> page = incomeRuleMapper.listIncomeRuleWithPart(id, name, incomeType, allotType, startTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 根据产品ID获取收益分配规则列表
     *
     * @param productId 产品ID
     */
    @Override
    public List<IncomeRuleDTO> listIncomeRuleByProductId(Integer productId) {
        List<IncomeRuleDTO> incomeRuleDTOS = incomeRuleMapper.listIncomeRuleByProductId(productId);
        //添加收益分配内容
        if (CollectionUtil.isNotEmpty(incomeRuleDTOS)) {
            Iterator<IncomeRuleDTO> iterator = incomeRuleDTOS.iterator();
            while (iterator.hasNext()) {
                IncomeRuleDTO next = iterator.next();
                List<IncomeRulePartDTO> incomeRulePart = incomeRulePartMapper.getIncomeRulePartByIncomeRuleId(next.getId());
                if (CollectionUtil.isNotEmpty(incomeRulePart)) {
                    next.setIncomeRuleParts(incomeRulePart);
                }
            }
        }
        return incomeRuleDTOS;
    }

    /**
     * 删除收益分配规则
     *
     * @param incomeRule 收益分配规则ID
     * @return void
     * @author hhf
     * @date 2019/4/23
     */
    // TODO 日志记录
    /*@EnableOperationLog(
            name = "删除收益分配规则",
            type = OperationType.UPDATE,
            daoClass = IncomeRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "deleted", "updater", "updateTime"},
            index = 0,
            queue = "OrderConstant.INCOME_RULE_OPERATION_LOG"
    )*/
    @Override
    public void delete(IncomeRule incomeRule) {
        String creator = userCache.getCurrentAdminRealName();
        Date now = new Date();
        incomeRule.setUpdater(creator);
        incomeRule.setUpdateTime(now);
        incomeRule.setDeleted(true);
        incomeRuleMapper.updateByPrimaryKeySelective(incomeRule);
    }

    /*@EnableOperationLog(
            name = "删除收益分配规则",
            type = OperationType.UPDATE,
            daoClass = IncomeRuleMapper.class,
            parseClass = DefaultContentParse.class,
            fields = {"id", "updater", "updateTime"},
            index = 0,
            queue = "OrderConstant.INCOME_RULE_OPERATION_LOG"
    )*/
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void update(IncomeRule incomeRule, List<IncomeRulePartDTO> incomeRuleParts) {
        checkIncomeInfo(incomeRule, incomeRuleParts);
        String creator = userCache.getCurrentAdminRealName();
        Date now = new Date();
        incomeRule.setUpdateTime(now);
        incomeRule.setUpdater(creator);
        incomeRuleMapper.updateByPrimaryKeySelective(incomeRule);

        // 明细更改之前删除
        Example example = new Example(IncomeRulePart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ruleId", incomeRule.getId());
        criteria.andEqualTo("deleted", 0);
        List<IncomeRulePart> ruleParts = incomeRulePartMapper.selectByExample(example);
        ruleParts.forEach(o -> {
            o.setDeleted(true);
            incomeRulePartMapper.updateByPrimaryKeySelective(o);
        });
        this.batchUpdateIncomePart(incomeRule, incomeRuleParts, creator, now);
    }

}
