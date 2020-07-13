package com.yimao.cloud.product.service;

import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.query.product.ProductCategoryQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.product.po.ProductCategory;

import java.util.List;
import java.util.Set;

/**
 * 产品类目
 *
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface ProductCategoryService {

    /**
     * 保存产品类目
     *
     * @param productCategory 产品类目
     */
    void saveProductCategory(ProductCategory productCategory);

    /**
     * 删除产品类目
     *
     * @param id 产品类目ID
     */
    void deleteProductCategoryById(Integer id);

    /**
     * 修改产品类目
     *
     * @param productCategory 产品类目
     */
    void updateProductCategory(ProductCategory productCategory);

    /**
     * 查询产品类目
     *
     * @param id 产品类目ID
     */
    ProductCategory getProductCategoryById(Integer id);

    /**
     * 查询产品类目（分页）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param query    查询条件
     */
    PageVO<ProductCategoryDTO> page(Integer pageNum, Integer pageSize, ProductCategoryQuery query);

    /**
     * 获取级联的产品类目名称，分隔符为“ > ”
     *
     * @param categoryId 产品类目ID
     */
    String buildCascadeCategoryName(Integer categoryId);

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    List<ProductCategoryDTO> listCategoryBySupplyCode(String supplyCode);

    /**
     * 更新产品分类排序
     *
     * @param id    产品类目ID
     * @param sorts 排序值
     */
    void updateCategorySorts(Integer id, Integer sorts);

    /**
     * 获取自身以及上级类目的ID集合
     *
     * @param id 产品类目ID
     */
    Set<Integer> getSelfAndParentId(Integer id);

    List<ProductCategoryDTO> getBottomCatgory(Integer id);

    ProductCategoryDTO getOneCategory(Integer categoryId);

    ProductCategoryDTO getTwoCategory(Integer categoryId);

    /**
     * 获取产品所有的一级类目
     *
     * @author hhf
     * @date 2019/5/21
     */
    List<ProductCategoryDTO> getFirstProductCategory();

    /**
     * 根据产品三级类目ID查询类目级联信息
     */
    ProductCategoryCascadeDTO getProductCategoryCascadeById(Integer id);

    /**
     * 获取产品前台一级类目
     *
     * @author hhf
     * @date 2019/7/11
     */
    ProductCategoryDTO getFrontCategoryByProductId(Integer productId, Integer terminal);

    /**
     * 查询所有产品一级分类
     *
     * @return
     */
    List<ProductCategoryDTO> getAllFirstCategory();

    /**
     * 查询经销商产品一级分类
     *
     * @return
     */
    List<ProductCategoryDTO> getFirstCategoryForAppProductIncome();

    Integer getGoodsIdByProductId(Integer productId);
}
