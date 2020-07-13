package com.yimao.cloud.order.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import feign.Param;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * 产品Feign
 *
 * @author liuhao@yimaokeji.com
 * @date 2018-12-28
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}")
    ProductDTO getProductById(@PathVariable(value = "id") Integer id);

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    ProductDTO getFullProductById(@PathVariable(value = "id") Integer id);

    /**
     * @param id
     * @return java.lang.Object
     * @description 根据ID查询产品公司信息
     * @author zhilin.he
     * @date 2019/1/2 15:57
     */
    @GetMapping(value = "/product/company/{id}")
    ProductCompanyDTO getProductCompanyById(@PathVariable("id") Integer id);

    /**
     * 根据产品ID更新产品信息
     *
     * @param productDTO
     * @return
     */
    @PutMapping(value = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    Object update(@Param("productDTO") ProductDTO productDTO);

    /**
     * @param pageNum
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param name
     * @param code
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.product.ProductCompanyDTO>
     * @description 查询产品公司列表
     * @author zhilin.he
     * @date 2019/1/14 10:16
     */
    @GetMapping(value = "/product/company/{pageNum}/{pageSize}")
    PageVO<ProductCompanyDTO> listProductCompany(@PathVariable(value = "pageNum") Integer pageNum,
                                                 @PathVariable(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "startTime", required = false) Date startTime,
                                                 @RequestParam(value = "endTime", required = false) Date endTime,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "code", required = false) String code);

    /**
     * @param id
     * @return
     * @description 单个查询产品类目
     * @author zhilin.he
     * @date 2019/1/14 10:16
     */
    @GetMapping(value = "/product/category/{id}")
    ProductCategoryDTO getProductCategory(@PathVariable(value = "id") Integer id);

    /**
     * 根据产品ID查询水机计费方式列表
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/product/cost")
    List<ProductCostDTO> listProductCostByProductId(@RequestParam("productId") Integer productId);


    /**
     * 根据ID获取计费方式
     *
     * @param id 产品价格体系模版
     */
    @GetMapping(value = "/product/cost/{id}")
    ProductCostDTO getById(@PathVariable("id") Integer id);

    /**
     * @param id 一级分类ID
     * @Description: 查询三级分类
     * @author ycl
     * @Return: java.util.List<com.yimao.cloud.pojo.dto.product.ProductCategoryDTO>
     * @Create: 2019/5/6 16:00
     */
    @GetMapping(value = "/productcategory/{id}")
    List<ProductCategoryDTO> getBottomCatgory(@PathVariable(value = "id") Integer id);

    /**
     * @param categoryId
     * @Description: 查询一级类目
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.product.ProductCategoryDTO
     * @Create: 2019/5/6 16:56
     */
    @GetMapping(value = "/product/oneCategory/{categoryId}")
    ProductCategoryDTO getOneProductCategory(@PathVariable(value = "categoryId") Integer categoryId);

    /**
     * 根据前端分类id展示商品列表
     *
     * @return
     * @Author lizhiqiang
     * @Date 2019-07-24
     * @Param
     */
    @GetMapping(value = "/product/front/{id}")
    @ApiOperation(notes = "根据前端分类id展示商品列表", value = "根据前端分类id展示商品列表")
    @ApiImplicitParam(name = "id", value = "前端一级分类id", dataType = "Long", paramType = "path")
    List<ProductDTO> getProductByFrontId(@PathVariable("id") Integer id);

    /**
     * 更新 产品的销售量
     *
     * @param id 产品
     * @author liuhao@yimaokeji.com
     */
    @RequestMapping(value = "/product/buys/count", method = RequestMethod.PATCH)
    void updateBuyCount(@RequestParam("id") Integer id, @RequestParam("count") Integer count);

    //获取二级类目
    @GetMapping(value = "/product/twoCategory/{categoryId}")
    ProductCategoryDTO getTwoProductCategory(@PathVariable(value = "categoryId") Integer categoryId);

    /**
     * 获取所有产品的一级类目
     *
     * @author hhf
     * @date 2019/5/21
     */
    @RequestMapping(value = "/product/category/first", method = RequestMethod.GET)
    List<ProductCategoryDTO> getFirstProductCategory();

    /**
     * 获取产品的前台类目
     *
     * @author hhf
     * @date 2019/7/11
     */
    @GetMapping(value = "/product/category/front")
    ProductCategoryDTO getFrontCategoryByProductId(@RequestParam("productId") Integer productId,
                                                   @RequestParam(value = "terminal", required = false) Integer terminal);

    @GetMapping(value = "/product/cost/{id}")
    ProductCostDTO getProductCostById(@PathVariable("id") Integer id);

    /**
     * 查询特定价格体系模版
     *
     * @param id 产品价格体系模版
     * @return
     */
    @GetMapping(value = "/product/cost/{id}")
    ProductCostDTO productCostGetById(@PathVariable("id") Integer id);

    /**
     * @param id
     * @description 根据产品三级类目ID查询类目级联信息
     * @author zhilin.he
     * @date 2019/5/24 14:21
     */
    @GetMapping(value = "/product/category/cascade/{id}")
    ProductCategoryCascadeDTO getProductCategoryCascadeById(@PathVariable("id") Integer id);

    @GetMapping(value = "/product/getByOldId")
    ProductDTO getProductByOldId(@RequestParam(value = "oldId") String oldId);

    /**
     * 根据商品活动ID查询单个商品活动
     */
    @GetMapping(value = "/product/activity/{id}")
    ProductActivityDTO getProductActivityById(@PathVariable(value = "id") Integer id);

    @GetMapping(value = "/product/activity/{id}/subtractStock")
    int subtractProductActivityStock(@PathVariable(value = "id") Integer id, @RequestParam(value = "count") Integer count);

    @GetMapping(value = "/product/activity/{id}/addStock")
    int addProductActivityStock(@PathVariable(value = "id") Integer id, @RequestParam(value = "count") Integer count);

    /**
     * 发布产品时获取所有产品供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping(value = "/product/supplyCode")
    List<Integer> getProductIdsBySupplyCode(@RequestParam("supplyCode") String supplyCode);
}
