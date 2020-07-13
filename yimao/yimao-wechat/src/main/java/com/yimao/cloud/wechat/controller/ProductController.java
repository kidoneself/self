package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.base.enums.ProductStatus;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.query.product.ProductQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.ProductFeign;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 公众号产品
 *
 * @author Lizhqiang
 * @date 2019/4/9
 */


@Slf4j
@RestController
public class ProductController {

    @Resource
    private ProductFeign productFeign;


    @GetMapping(value = "/product/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hot", value = "是否热销：1热销  0不热销", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：传2则是已上架  不传则为全部", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> productPage(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "hot", required = false) Integer hot,
                                          @PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(ProductStatus.ONSHELF.value);
        return productFeign.page(categoryId, hot, 1, statusList, pageNum, pageSize);
    }


    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}")
    @ApiOperation(value = "查询产品（单个，基本信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getById(@PathVariable("id") Integer id) {
        ProductDTO productDTO = productFeign.getProductById(id);
        return productDTO;
    }


    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    @ApiOperation(value = "查询产品（单个，扩展信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getFullById(@PathVariable("id") Integer id) {
    	//1表示公众号
        return productFeign.getFullProductById(id,1);
    }


    /**
     * weChat查询产品
     *
     * @param frontCategoryId 产品前台一级类目ID
     * @param pageNum         页码
     * @param pageSize        每页大小
     */
    @GetMapping(value = "/product/weChat/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询前台产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "frontCategoryId", value = "前台分类的ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> listProductForClient(@RequestParam(required = false) Integer frontCategoryId,
                                                   @PathVariable Integer pageNum,
                                                   @PathVariable Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setFrontCategoryId(frontCategoryId);
        return productFeign.listProductForClient(frontCategoryId, pageNum, pageSize);
    }

    @GetMapping(value = "/product/waters")
    @ApiOperation(notes = "直升会员列表", value = "直升会员列表")
    public List<ProductDTO> getWaterProduct() {
        return productFeign.getWaterProduct();
    }


    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询单个活动商品信息", notes = "查询单个活动商品信息")
    @ApiImplicitParam(name = "query", value = "查询单个活动商品信息", required = true, dataType = "ProductActivityQuery", paramType = "body")
    public Object productActivityInfo(@RequestBody ProductActivityQuery query) {
        query.setProductStatus(2);
        query.setOpening(true);
        return ResponseEntity.ok(productFeign.productActivityInfo(query));
    }
}
