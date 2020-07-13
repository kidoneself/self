package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.product.po.ProductRenew;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 商品续费
 * @author Liu Yi
 * @date 2018/11/26.
 */
public interface ProductRenewMapper extends Mapper<ProductRenew> {
    /**
     * 新增商品续费
     *
     * @param productRenew
     * @return
     */
    Integer saveProductRenew(ProductRenew productRenew);

    /**
     * 批量新增商品续费
     * @param list
     * @return
     */
    Integer saveProductRenews(@Param("productRenewList")List<ProductRenew> list);

    /**
     * 更新商品续费
     *
     * @param
     * @return page
     */
    Integer updateProductRenew(ProductRenew productRenew);

    /**
     * 获取商品续费
     *
     * @param
     * @return page
     */
    ProductRenewDTO getProductRenewById(@Param("id") Integer id);

    /**
     * 分页查询商品续费
     *
     * @param categoryIdList
     * @return
     */
    Page<ProductRenewDTO> listProductRenews(@Param("categoryIdList") List categoryIdList);

}
