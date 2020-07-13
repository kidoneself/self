package com.yimao.cloud.product.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.query.product.ProductPropertyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductPropertyVO;
import com.yimao.cloud.product.mapper.ProductPropertyMapper;
import com.yimao.cloud.product.mapper.ProductPropertyValueMapper;
import com.yimao.cloud.product.po.ProductProperty;
import com.yimao.cloud.product.po.ProductPropertyValue;
import com.yimao.cloud.product.service.ProductPropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 产品属性和属性值
 *
 * @author liulin
 * @date 2018/12/1
 */
@Service
@Slf4j
public class ProductPropertyServiceImpl implements ProductPropertyService {

    @Resource
    private UserCache userCache;
    @Resource
    private ProductPropertyMapper productPropertyMapper;
    @Resource
    private ProductPropertyValueMapper productPropertyValueMapper;

    /**
     * 保存产品属性和产品属性值
     *
     * @param property         产品属性
     * @param propertyValueStr 产品属性值
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void savePropertyAndValue(ProductProperty property, String propertyValueStr) {
        if (StringUtil.isBlank(propertyValueStr)) {
            throw new BadRequestException("请填写属性值。");
        }
        //step1 保存基本属性
        property.setId(null);
        String creator = userCache.getCurrentAdminRealName();
        property.setCreator(creator);
        property.setUpdater(creator);
        property.setCreateTime(new Date());
        property.setUpdateTime(property.getCreateTime());
        property.setDeleted(false);
        productPropertyMapper.insert(property);
        //step2 添加productPropertyValue

        saveProductPropertyValue(property.getId(), propertyValueStr, creator);
    }

    /**
     * 修改产品属性和产品属性值
     *
     * @param property         产品属性
     * @param propertyValueStr 产品属性值
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updatePropertyAndValue(ProductProperty property, String propertyValueStr) {
        Integer propertyId = property.getId();
        if (propertyId == null) {
            throw new BadRequestException("操作失败，更新对象ID为空。");
        }
        String updater = userCache.getCurrentAdminRealName();
        property.setUpdater(updater);
        property.setUpdateTime(new Date());
        productPropertyMapper.updateByPrimaryKeySelective(property);

        //删除产品属性值信息
        Example ppvExample = new Example(ProductPropertyValue.class);
        ppvExample.createCriteria().andEqualTo("propertyId", propertyId);
        productPropertyValueMapper.deleteByExample(ppvExample);

        saveProductPropertyValue(propertyId, propertyValueStr, updater);
    }

    /**
     * 批量保存产品属性值
     *
     * @param propertyId       产品属性ID
     * @param propertyValueStr 产品属性值
     * @param adminName        管理员姓名
     */
    private void saveProductPropertyValue(Integer propertyId, String propertyValueStr, String adminName) {
        List<ProductPropertyValue> list = new ArrayList<>();
        String[] propertyValues = propertyValueStr.split(",");
        for (int i = 0; i < propertyValues.length; i++) {
            String value = propertyValues[i];
            ProductPropertyValue propertyValue = new ProductPropertyValue();
            propertyValue.setPropertyId(propertyId);
            propertyValue.setPropertyValue(value);
            propertyValue.setCreateTime(new Date());
            propertyValue.setUpdateTime(propertyValue.getCreateTime());
            propertyValue.setCreator(adminName);//当前登录人
            propertyValue.setUpdater(adminName);//当前登录人
            list.add(propertyValue);
        }
        productPropertyValueMapper.insertBatch(list);
    }

    /**
     * @param ids 产品属性ID集合
     */
    @Override
    public void batchUpdate(List<Integer> ids) {
        Example example = new Example(ProductProperty.class);
        example.createCriteria().andIn("id", ids);
        ProductProperty productProperty = new ProductProperty();
        productProperty.setDeleted(true);
        productPropertyMapper.updateByExampleSelective(productProperty, example);
    }

    /**
     * 根据产品大类查询所有产品属性
     *
     * @param typeId 产品大类：1-实物商品；2-电子卡券；3-租赁商品；
     */
    @Override
    public List<ProductPropertyVO> listProductPropertyByType(Integer typeId) {
        ProductPropertyQuery query = new ProductPropertyQuery();
        query.setTypeId(typeId);
        return productPropertyMapper.listWithValueByType(query);
    }

    /**
     * 查询产品属性的分页列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    @Override
    public PageVO<ProductPropertyVO> page(Integer pageNum, Integer pageSize, ProductPropertyQuery query) {
        PageHelper.startPage(pageNum, pageSize);
        Page<ProductPropertyVO> page = productPropertyMapper.listWithValueByType(query);
        return new PageVO<>(pageNum, page);
    }

}