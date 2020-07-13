package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.ProductCostModelTypeEnum;
import com.yimao.cloud.base.enums.ProductCostTypeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.query.product.ProductCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import com.yimao.cloud.product.mapper.ProductCostMapper;
import com.yimao.cloud.product.po.ProductCost;
import com.yimao.cloud.product.service.ProductCostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Liu Yi
 * @date 2018/12/1.
 */
@Service
@Slf4j
public class ProductCostServiceImpl implements ProductCostService {

    @Resource
    private UserCache userCache;
    @Resource
    private ProductCostMapper productCostMapper;

    /**
     * 新增计费方式
     *
     * @param productCost 计费方式
     */
    @Override
    public void saveProductCost(ProductCost productCost) {
        if (StringUtil.isEmpty(productCost.getName())) {
            throw new BadRequestException("计费方式名称不能为空。");
        }
        if (productCost.getType() == null) {
            throw new BadRequestException("请选择时流量计费还是时长计费。");
        }
        if (productCost.getValue() == null) {
            throw new BadRequestException("计费方式值不能为空。");
        }
        if (productCost.getUnitPrice() == null) {
            throw new BadRequestException("计费方式单价不能为空。");
        }
        if (productCost.getRentalFee() == null) {
            throw new BadRequestException("租赁费不能为空。");
        }
        if (productCost.getInstallationFee() == null) {
            throw new BadRequestException("安装费（即开户费）不能为空。");
        }
        if (productCost.getTotalFee() == null) {
            throw new BadRequestException("商品总价（商城展示价）不能为空。");
        }
        if (productCost.getProductCategoryId() == null) {
            throw new BadRequestException("计费方式所属产品类目不能为空。");
        }
        if (productCost.getModelType() == null) {
            throw new BadRequestException("计费模板类型不能为空。");
        }
        if (ProductCostModelTypeEnum.RENEW_FEE.value == productCost.getModelType()) {
            if (productCost.getIncomeRuleId() == null) {
                throw new BadRequestException("续费模板佣金设置不能为空。");
            }
        }

        String creator = userCache.getCurrentAdminRealName();
        productCost.setCreator(creator);
        productCost.setCreateTime(new Date());
        productCost.setUpdater(creator);
        productCost.setUpdateTime(productCost.getCreateTime());
        productCost.setId(null);
        productCost.setDeleted(false);
        productCost.setOldId(String.valueOf(System.currentTimeMillis()));
        if (ProductCostTypeEnum.isTime(productCost.getType())) {
            productCost.setThreshold1(15);
            productCost.setThreshold2(1);
            productCost.setThreshold3(0);
        } else {
            productCost.setThreshold1(79);
            productCost.setThreshold2(8);
            productCost.setThreshold3(0);
        }
        productCostMapper.insert(productCost);
    }

    /**
     * 删除计费方式
     *
     * @param id 计费方式ID
     */
    @Override
    public void deleteProductCostById(Integer id) {
        ProductCost productCost = new ProductCost();
        productCost.setId(id);
        productCost.setDeleted(true);
        productCostMapper.updateByPrimaryKeySelective(productCost);
    }

    /**
     * 修改计费方式
     *
     * @param productCost 计费方式
     */
    @Override
    public void updateProductCost(ProductCost productCost) {
        Integer id = productCost.getId();
        if (id == null) {
            throw new BadRequestException("操作失败，更新对象ID为空。");
        }
        if (productCost.getModelType() == null) {
            throw new BadRequestException("计费模板类型不能为空。");
        }
        if (ProductCostModelTypeEnum.RENEW_FEE.value == productCost.getModelType()) {
            if (productCost.getIncomeRuleId() == null) {
                throw new BadRequestException("续费模板佣金设置不能为空。");
            }
        }
        String updater = userCache.getCurrentAdminRealName();
        productCost.setUpdater(updater);
        productCost.setUpdateTime(new Date());
        productCostMapper.updateByPrimaryKeySelective(productCost);
    }

    /**
     * 启用/禁用计费方式
     *
     * @param ids     计费方式ID集合
     * @param deleted 启用/禁用：0-禁用；1-启用；
     */
    @Override
    public void forbiddenProduct(Integer[] ids, Boolean deleted) {
        if (ids == null || ids.length == 0) {
            throw new BadRequestException("请选择计费方式。");
        }
        if (deleted == null) {
            throw new BadRequestException("请选择启用或者禁用。");
        }
        ProductCost productCost = new ProductCost();
        productCost.setDeleted(deleted);
        List<Integer> idList = Arrays.asList(ids);
        if (idList.size() == 1) {
            productCost.setId(idList.get(0));
            productCostMapper.updateByPrimaryKeySelective(productCost);
        } else {
            Example example = new Example(ProductCost.class);
            example.createCriteria().andIn("id", idList);
            productCostMapper.updateByExampleSelective(productCost, example);
        }
    }

    /**
     * 分页获取计费方式
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ProductCostVO> page(Integer pageNum, Integer pageSize, ProductCostQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductCostVO> page = productCostMapper.listProductCost(query);
        return new PageVO<>(pageNum, page);
    }

}
