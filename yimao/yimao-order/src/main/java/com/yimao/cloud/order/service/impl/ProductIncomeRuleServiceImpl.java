package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.mapper.IncomeRulePartMapper;
import com.yimao.cloud.order.mapper.ProductIncomeRuleMapper;
import com.yimao.cloud.order.po.ProductIncomeRule;
import com.yimao.cloud.order.service.ProductIncomeRuleService;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 描述：产品-收益规则关联
 *
 * @Author Zhang Bo
 * @Date 2019/3/21
 */
@Service
public class ProductIncomeRuleServiceImpl implements ProductIncomeRuleService {


    @Resource
    private IncomeRulePartMapper incomeRulePartMapper;

    @Resource
    private ProductIncomeRuleMapper productIncomeRuleMapper;

    /**
     * 保存产品-收益分配规则关联关系
     *
     * @param productId     产品ID
     * @param incomeRuleIds 收益分配规则ID
     */
    @Override
    public void saveProductIncomeRule(Integer productId, Set<Integer> incomeRuleIds) {
        //第一步先删除之前的关联
        ProductIncomeRule record = new ProductIncomeRule();
        record.setProductId(productId);
        productIncomeRuleMapper.delete(record);

        //第二步保存现在的关联
        List<ProductIncomeRule> pirList = new ArrayList<>();
        for (Integer incomeRuleId : incomeRuleIds) {
            ProductIncomeRule pir = new ProductIncomeRule();
            pir.setProductId(productId);
            pir.setIncomeRuleId(incomeRuleId);
            pirList.add(pir);
        }
        productIncomeRuleMapper.batchInsert(pirList);
    }

    /**
     * 根据产品ID查询收益分配规则ID
     *
     * @param productId 产品ID
     */
    @Override
    public Set<Integer> listIncomeRuleIdByProductId(Integer productId) {
        return productIncomeRuleMapper.listIncomeRuleIdByProductId(productId);
    }

    /**
     * app 调用获取商品的收益规则
     *
     * @param id
     * @return
     */
    @Override
    public IncomeRuleDTO getIncomeValue(Integer id) {
        IncomeRuleDTO incomeRuleDTO = productIncomeRuleMapper.getIncomeValue(id);
        if (incomeRuleDTO != null) {
            List<IncomeRulePartDTO> incomeRulePartDTOS = incomeRulePartMapper.getIncomeRulePartByIncomeRuleId(incomeRuleDTO.getId());
            incomeRuleDTO.setIncomeRuleParts(incomeRulePartDTOS);
        }
        return incomeRuleDTO;
    }
}
