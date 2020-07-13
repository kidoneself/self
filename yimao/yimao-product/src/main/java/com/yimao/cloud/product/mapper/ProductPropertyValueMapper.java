package com.yimao.cloud.product.mapper;

import com.yimao.cloud.pojo.dto.product.ProductPropertyValueDTO;
import com.yimao.cloud.product.po.ProductPropertyValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liu.lin
 * @date 2018/11/28.
 */
public interface ProductPropertyValueMapper extends Mapper<ProductPropertyValue> {

    /**
     * 新增产品属性值
     *
     * @param list
     */
    Integer insertBatch(@Param("list") List<ProductPropertyValue> list);

    /**
     * 产品某个产品属性下的产品属性值
     *
     * @param productPropertyId
     */
    List<ProductPropertyValueDTO> selectProductPropertyValue(@Param("productPropertyId") Integer productPropertyId);
}
