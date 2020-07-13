package com.yimao.cloud.product.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.query.product.ProductCategoryQuery;
import com.yimao.cloud.product.po.ProductCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface ProductCategoryMapper extends Mapper<ProductCategory> {

    /**
     * 查询产品类目（分页）
     *
     * @param query 查询条件
     */
    Page<ProductCategoryDTO> listProductCategory(ProductCategoryQuery query);

    /**
     * 查询产品前台类目
     *
     * @param productId 产品ID
     * @param terminal 平台
     */
    List<ProductCategoryDTO> listFrontCategoryByProductId(@Param("productId") Integer productId,@Param("terminal") Integer terminal);

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    List<ProductCategoryDTO> listCategoryBySupplyCode(@Param("supplyCode") String supplyCode);

    List<ProductCategoryDTO> getBottomCatgory(@Param("id") Integer id);

    ProductCategoryDTO getOneCategory(@Param("categoryId") Integer categoryId);

    List<Integer> getNextCategoryT(Integer categoryId);

    ProductCategory selectT(Integer id);

    ProductCategoryDTO getTwoCategory(@Param("categoryId") Integer categoryId);

    /**
     * 校验后台产品类目关联关系是否存在，存在则不能删除
     *
     * @param id
     */
    boolean checkProductUsing(@Param("id") Integer id);

    /**
     * 校验前台产品类目关联关系是否存在，存在则不能删除
     *
     * @param id
     */
    boolean checkProductFrontUsing(@Param("id") Integer id);

    /**
     * 获取产品前台一级类目
     *
     * @author hhf
     * @date 2019/7/11
     */
    ProductCategoryDTO getFrontCategoryByProductId(@Param("productId") Integer productId, @Param("terminal") Integer terminal);

    ProductCategoryDTO selectByOldId(@Param("oldId") String oldId);

    /**
     * @description   根据编码等参数查询一级分类
     * @author Liu Yi
     * @date 2019/11/7 20:12
     * @param
     * @return java.util.List<com.yimao.cloud.pojo.dto.product.ProductCategoryDTO>
     */
    List<ProductCategoryDTO> getFirstCategoryListByParam(@Param("type") Integer type,@Param("terminal") Integer terminal,@Param("supplyCode") String supplyCode);
}
