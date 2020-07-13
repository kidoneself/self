package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.ProductFeign;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 产品活动
 *
 * @author zhangbaobao
 * @date 2020/3/12
 */
@RestController
@Api(tags = "ProductActivityController")
public class ProductActivityController {

    @Resource
    private ProductFeign productFeign;

    /****
     * 查询已开启活动并且产品已上架的活动产品列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", required = true, dataType = "ProductActivityQuery", paramType = "body")
    })
    public Object pageProductActivity(@PathVariable Integer pageNum,
                                      @PathVariable Integer pageSize,
                                      @RequestBody ProductActivityQuery query) {
        return ResponseEntity.ok(productFeign.pageProductActivity(pageNum, pageSize, query));
    }

    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询单个活动商品信息", notes = "查询单个活动商品信息")
    @ApiImplicitParam(name = "query", value = "app经销商收益统计请求参数", required = true, dataType = "ProductActivityQuery", paramType = "body")
    public Object productActivityInfo(@RequestBody ProductActivityQuery query) {
        query.setProductStatus(2);
        query.setOpening(true);
        return ResponseEntity.ok(productFeign.productActivityInfo(query));
    }

}
