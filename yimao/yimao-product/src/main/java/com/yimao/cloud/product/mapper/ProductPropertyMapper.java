package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.query.product.ProductPropertyQuery;
import com.yimao.cloud.pojo.vo.product.ProductPropertyVO;
import com.yimao.cloud.product.po.ProductProperty;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author liu.lin
 * @date 2018/11/28.
 */
public interface ProductPropertyMapper extends Mapper<ProductProperty> {

    /**
     * 根据产品大类查询所有产品属性
     *
     * @param query 查询条件
     */
    Page<ProductPropertyVO> listWithValueByType(ProductPropertyQuery query);

}
