package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/7/4.
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

    /**
     * APP端获取当前登录用户有权销售的产品销售类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping("/product/app/supply")
    List<DictionaryDTO> listProductSupplyCode(@RequestParam(value = "userName", required = false) String userName);

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping("/product/category/supply")
    List<ProductCategoryDTO> listCategoryBySupplyCode(@RequestParam(value = "supplyCode") String supplyCode);

    /**
     * 经销商APP客户端查询产品
     *
     * @param supplyCode      APP产品供应类型CODE：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     * @param frontCategoryId 产品前台一级类目ID
     * @param pageNum         页码
     * @param pageSize        每页大小
     */
    @GetMapping(value = "/product/app/{pageNum}/{pageSize}")
    PageVO<ProductDTO> listProductForClient(@RequestParam(name = "supplyCode") String supplyCode,
                                            @RequestParam(name = "frontCategoryId", required = false) Integer frontCategoryId,
                                            @PathVariable(name = "pageNum") Integer pageNum, @PathVariable(name = "pageSize") Integer pageSize);


    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    ProductDTO getProductById(@PathVariable("id") Integer id);

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    ProductDTO getFullProductById(@PathVariable("id") Integer id, @RequestParam(name = "terminal", required = false) Integer terminal);

    /**
     * 分页查询商品信息
     *
     * @param categoryId
     * @param hot
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/product/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ProductDTO> page(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                            @RequestParam(value = "hot", required = false) Integer hot,
                            @RequestParam(value = "terminal", required = false) Integer terminal,
                            @RequestParam(value = "need", required = false) Integer need,
                            @RequestParam(value = "statusList", required = false) List<Integer> statusList,
                            @PathVariable("pageNum") Integer pageNum,
                            @PathVariable("pageSize") Integer pageSize);


    @RequestMapping(value = "/category/first/all", method = RequestMethod.GET)
    List<ProductCategoryDTO> getAllFirstCategory();

    /**
     * 查询所有产品一级分类（经销商app收益模块一级分类）
     */
    @GetMapping("/category/first/productIncome")
    List<ProductCategoryDTO> getFirstCategoryForAppProductIncome();


    @RequestMapping(value = "/product/front/{id}", method = RequestMethod.GET)
    List<ProductDTO> getProductByFrontId(@PathVariable("id") Integer id);

    @RequestMapping(value = "/product/waters", method = RequestMethod.GET)
    List<ProductDTO> getWaterProduct();

    /***
     * 查询活动产品列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageVO<ProductActivityVO> pageProductActivity(@PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize,
                                                  @RequestBody ProductActivityQuery query);

    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductActivityVO productActivityInfo(@RequestBody ProductActivityQuery query);
}
