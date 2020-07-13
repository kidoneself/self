package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.query.product.ProductCostQuery;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import com.yimao.cloud.product.po.ProductCost;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface ProductCostMapper extends Mapper<ProductCost> {

    /**
     * 分页获取计费方式
     *
     * @param query 查询条件
     */
    Page<ProductCostVO> listProductCost(ProductCostQuery query);


    /**
     * 查询水机产品的计费方式
     *
     * @param productId 产品ID
     */
    List<ProductCostDTO> listProductCostByProductId(@Param("productId") Integer productId);

    /**
     * 获取变更计费方式时可选择的计费方式列表
     *
     * @param oldCostId 原计费方式ID
     */
    List<ProductCostDTO> listByOldCostId(@Param("oldCostId") Integer oldCostId);

    /**
     * 获取续费计费方式列表
     *
     * @param oldCostId 原计费方式ID
     */
    List<ProductCostDTO> listRenewCostByOldCostId(@Param("oldCostId") Integer oldCostId, @Param("type") Integer type);

    /****
     * 获取已上架产品的计费方式
     * @param price
     */
    List<ProductCostDTO> getOnlinePoruductCost(@Param("price") BigDecimal price);
}
