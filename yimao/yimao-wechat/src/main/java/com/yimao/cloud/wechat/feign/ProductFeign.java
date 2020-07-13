package com.yimao.cloud.wechat.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019/4/1
 */
@FeignClient(name = Constant.MICROSERVICE_PRODUCT)
public interface ProductFeign {

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
                            @RequestParam(value = "statusList", required = false) List<Integer> statusList,
                            @PathVariable("pageNum") Integer pageNum,
                            @PathVariable("pageSize") Integer pageSize);

    /**
     * 查询单个商品信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    ProductDTO getProductById(@PathVariable("id") Integer id);

    /**
     * 分页查询商品分类
     *
     * @param type
     * @param terminal
     * @param pid
     * @param level
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/product/category/{pageNum}/{pageSize}", method = RequestMethod.GET)
    PageVO<ProductCategoryDTO> pageProductCategory(@RequestParam("type") Integer type,
                                                   @RequestParam("terminal") Integer terminal,
                                                   @RequestParam("pid") Integer pid,
                                                   @RequestParam("level") Integer level,
                                                   @PathVariable("pageNum") Integer pageNum,
                                                   @PathVariable("pageSize") Integer pageSize);


    /**
     * 查询单个商品信息（拓展）
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/product/{id}/expansion", method = RequestMethod.GET)
    ProductDTO getFullProductById(@PathVariable("id") Integer id,@RequestParam(name="terminal",required=false)Integer terminal);

    /**
     * wechat查询产品
     *
     * @param frontCategoryId 产品前台一级类目ID
     * @param pageNum         页码
     * @param pageSize        每页大小
     */
    @GetMapping(value = "/product/weChat/{pageNum}/{pageSize}")
    PageVO<ProductDTO> listProductForClient(
            @RequestParam(name = "frontCategoryId", required = false) Integer frontCategoryId,
            @PathVariable(name = "pageNum") Integer pageNum, @PathVariable(name = "pageSize") Integer pageSize);


    @RequestMapping(value = "/product/waters", method = RequestMethod.GET)
    List<ProductDTO> getWaterProduct();


    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductActivityVO productActivityInfo(@RequestBody ProductActivityQuery query);
}
