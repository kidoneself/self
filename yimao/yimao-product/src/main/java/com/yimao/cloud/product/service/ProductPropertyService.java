package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.query.product.ProductPropertyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductPropertyVO;
import com.yimao.cloud.product.po.ProductProperty;

import java.util.List;

public interface ProductPropertyService {

    /**
     * 保存产品属性和产品属性值
     *
     * @param property         产品属性
     * @param propertyValueStr 产品属性值
     */
    void savePropertyAndValue(ProductProperty property, String propertyValueStr);

    /**
     * 修改产品属性和产品属性值
     *
     * @param property         产品属性
     * @param propertyValueStr 产品属性值
     */
    void updatePropertyAndValue(ProductProperty property, String propertyValueStr);

    /**
     * 批量更新产品属性
     *
     * @param ids 产品属性ID集合
     */
    void batchUpdate(List<Integer> ids);

    /**
     * 根据产品大类查询所有产品属性
     *
     * @param typeId 产品大类：1-实物商品；2-电子卡券；3-租赁商品；
     */
    List<ProductPropertyVO> listProductPropertyByType(Integer typeId);

    /**
     * 查询产品属性的分页列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ProductPropertyVO> page(Integer pageNum, Integer pageSize, ProductPropertyQuery query);

}
