package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface ProductRenewService {

    /**
     * 新增商品续费
     *
     * @param categoryIdList
     * @param costIdList
     * @return
     */
    Integer saveProductRenew(List<Integer> categoryIdList, List<Integer> costIdList);

    /**
     * 更新商品续费
     *
     * @param
     * @return page
     */
    Integer updateProductRenew(ProductRenewDTO productCosDTO);

    /**
     * 获取商品续费
     *
     * @param
     * @return page
     */
    ProductRenewDTO getProductRenewById(Integer id);

    /**
     * 删除商品续费
     *
     * @param id
     */
    void deleteProductRenewById(Integer id);

    /**
     * 分页查询商品续费
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param secondCategoryId
     * @return
     */
    PageVO<ProductRenewDTO> listProductRenews(Integer pageNum, Integer pageSize, Integer categoryId, Integer secondCategoryId, Integer thirdCategoryId);
}
