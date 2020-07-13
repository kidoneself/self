package com.yimao.cloud.product.mapper;

import com.yimao.cloud.product.po.ProductDistributor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductDistributorMapper extends Mapper<ProductDistributor> {

    Integer batchInsert(@Param("list") List<ProductDistributor> list);
}
