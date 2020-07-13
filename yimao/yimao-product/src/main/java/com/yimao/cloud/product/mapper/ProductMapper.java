package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.query.product.ProductQuery;
import com.yimao.cloud.pojo.vo.product.ProductStatusStatisticVO;
import com.yimao.cloud.product.po.Product;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface ProductMapper extends Mapper<Product> {

    /**
     * 查询产品（分页）
     *
     * @param query 查询条件
     */
    Page<ProductDTO> listProduct(ProductQuery query);

    /**
     * 客户端查询产品（经销商APP、公众号等）
     *
     * @param query 查询条件
     */
    Page<ProductDTO> listProductForClient(ProductQuery query);

    /**
     * 产品统计
     */
    ProductStatusStatisticVO findProductStatistics();

    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @description 查询M Y 卡的产品数据
     * @author liulin
     * @date 2019/2/23 9:26
     */
    List<ProductDTO> findMYCardProductList(@Param("categoryName") String categoryName);


    /**
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductDTO>
     * @description 查询F 卡的产品数据
     * @author liulin
     * @date 2019/2/27 17:51
     */
    List<ProductDTO> findFCardProductList();

    /**
     * 描述：根据产品分类名称获取产品ID集合
     *
     * @param categoryName  产品分类名称
     * @param categoryLevel 几级产品分类
     */
    List<Integer> listProductIdsByCategoryName(@Param("categoryName") String categoryName, @Param("categoryLevel") Integer categoryLevel);

    List<ProductDTO> findMCardProductList();

    /**
     * 后台修改订单产品型号时查询同类产品列表（二级类目一致，价格一致）
     *
     * @param productId 产品ID
     */
    List<ProductDTO> listProductForModifyOrder(@Param("costId") Integer costId, @Param("price") BigDecimal price,
                                               @Param("secondCategoryId") Integer secondCategoryId, @Param("fcidList") List<Integer> fcidList);

    List<ProductDTO> getProductByFrontId(@Param("id") Integer id);

    Product updateSaleCountById(@Param("id") Integer id, @Param("count") Integer count);

    Integer selectIdByCategoryId(@Param("categoryId") Integer categoryId);

    Integer selectIdByCategoryName(@Param("categoryName") String categoryName);

    List<ProductDTO> getWaterProduct();

    List<ProductDTO> listProductByActivityTypeAndDistributor(@Param("activityType") Integer activityType,
                                                             @Param("distributorId") Integer distributorId,
                                                             @Param("price") BigDecimal price);

    Integer selectIdByOldId(@Param("oldId") String oldId);

	List<ProductDTO> findNotListedProduct(@Param(value="statusList")List<Integer> statusList);


    List<Integer> getProductBySupplyCode(@Param("supplyCode") String supplyCode);
    
    /***
     * 获取已绑定上线产品的水机三级分类集合
     * @param price 
     * @return
     */
    List<ProductDTO> getOnlineProductThreeCategory(@Param("price") BigDecimal price);
}
