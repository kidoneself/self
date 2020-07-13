package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.query.product.ProductCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import com.yimao.cloud.product.po.ProductCost;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface ProductCostService {
    /**
     * 新增计费方式
     *
     * @param productCost 计费方式
     */
    void saveProductCost(ProductCost productCost);

    /**
     * 删除计费方式
     *
     * @param id 计费方式ID
     */
    void deleteProductCostById(Integer id);

    /**
     * 修改计费方式
     *
     * @param productCost 计费方式
     */
    void updateProductCost(ProductCost productCost);

    /**
     * 启用/禁用计费方式
     *
     * @param ids     计费方式ID集合
     * @param deleted 启用/禁用：0-禁用；1-启用；
     */
    void forbiddenProduct(Integer[] ids, Boolean deleted);

    /**
     * 分页获取计费方式
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ProductCostVO> page(Integer pageNum, Integer pageSize, ProductCostQuery query);

}
