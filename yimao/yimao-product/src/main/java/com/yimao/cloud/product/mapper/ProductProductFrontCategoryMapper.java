package com.yimao.cloud.product.mapper;

import com.yimao.cloud.product.po.ProductProductFrontCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 产品前台分类关系
 *
 * @auther: liu.lin
 * @date: 2019/1/4
 */
public interface ProductProductFrontCategoryMapper extends Mapper<ProductProductFrontCategory> {

    /**
     * 批量保存
     * @param list
     * @return
     */
   Integer batchInsert(@Param("list") List<ProductProductFrontCategory> list);

    List<Integer> listProductProductFrontCategoryId(Integer id);
}
