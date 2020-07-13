package com.yimao.cloud.product.mapper;

import com.yimao.cloud.product.po.ProductProductCost;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductProductCostMapper extends Mapper<ProductProductCost> {

    Integer batchInsert(@Param("list") List<ProductProductCost> list);

}
