package com.yimao.cloud.out.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 描述：产品微服务的接口列表
 *
 * @author Zhang Bo
 * @date 2019/3/9.
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

    /**
     * 根据产品分类名称获取产品ID集合
     *
     * @param categoryName  产品分类名称
     * @param categoryLevel 几级产品分类
     */
    @RequestMapping(value = "/product/ids/category_name", method = RequestMethod.GET)
    List<Integer> listProductIdsByCategoryName(@RequestParam(value = "categoryName") String categoryName,
                                               @RequestParam(value = "categoryLevel") Integer categoryLevel);

    /**
     * 查询特定价格体系模版
     *
     * @param id 产品价格体系模版
     * @return
     */
    @GetMapping(value = "/product/cost/{id}")
    ProductCostDTO productCostGetById(@PathVariable("id") Integer id);

    /**
     * 获取计费方式列表
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/product/cost")
    List<ProductCostDTO> productCostList(@RequestParam(required = false, value = "productId") Integer productId);

    /**
     * @param id
     * @description 根据产品三级类目ID查询类目级联信息
     * @author zhilin.he
     * @date 2019/5/24 14:21
     */
    @GetMapping(value = "/product/category/cascade/{id}")
    ProductCategoryCascadeDTO getProductCategoryCascadeById(@PathVariable("id") Integer id);

    @GetMapping(value = "/product/{id}")
    ProductDTO getById(@PathVariable("id") Integer id);

    @GetMapping(value = "/product/getIdByCategoryId")
    Integer getProductIdByCategoryId(@RequestParam(value = "categoryId") Integer categoryId);

    @GetMapping(value = "/product/getIdByCategoryName")
    Integer getProductIdByCategoryName(@RequestParam(value = "categoryName") String categoryName);

    @GetMapping(value = "/product/category/getByOldId")
    ProductCategoryDTO getProductCategoryByOldId(@RequestParam(value = "oldId") String oldId);

    @GetMapping(value = "/product/category/{id}")
    ProductCategoryDTO getProductCategoryById(@PathVariable(value = "id") Integer id);

    @GetMapping(value = "/product/oneCategory{categoryId}")
    ProductCategoryDTO getOneProductCategory(@PathVariable(value = "categoryId") Integer categoryId);

    @GetMapping(value = "/product/twoCategory/{categoryId}")
    ProductCategoryDTO getTwoProductCategory(@PathVariable(value = "categoryId") Integer categoryId);


}
