package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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


    /***
     *更新产品活动信息
     * @param query
     * @return
     */
    @PostMapping(value = "product/activity", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "更新活动信息")
    @ApiImplicitParam(name = "update", value = "更新条件", required = true, dataType = "ProductActivityDTO", paramType = "body")
    public Object updateProductActivity(@RequestBody ProductActivityDTO update) {
        productFeign.updateProductActivity(update);
        return ResponseEntity.noContent().build();
    }


    /****
     * 查询活动列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "product/activity/list/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "ProductActivityDTO", paramType = "body")
    })
    public Object productActivityList(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                      @RequestBody ProductActivityDTO query) {
        return ResponseEntity.ok(productFeign.productActivityList(pageNum, pageSize, query));
    }

    /**
     * 删除活动
     *
     * @param id 活动对象
     */
    @DeleteMapping(value = "product/activity/{id}")
    @ApiOperation(value = "删除活动", notes = "删除活动")
    @ApiImplicitParam(name = "id", value = "活动ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        productFeign.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查看活动详情
     *
     * @param id 活动对象
     */
    @GetMapping(value = "product/activity/{id}")
    @ApiOperation(value = "查看活动", notes = "查看活动")
    @ApiImplicitParam(name = "id", value = "活动ID", required = true, dataType = "Long", paramType = "path")
    public Object detail(@PathVariable("id") Integer id) {
        ProductActivityQuery query = new ProductActivityQuery();
        query.setId(id);
        return ResponseEntity.ok(productFeign.productActivityInfo(query));
    }

    /****
     * 激活活动,需要将商品设置为活动商品
     * @param id
     * @return
     */
    @GetMapping(value = "product/activity/open/{id}")
    @ApiOperation(value = "激活活动", notes = "激活活动")
    @ApiImplicitParam(name = "id", value = "活动ID", required = true, dataType = "Long", paramType = "path")
    public Object openProductActivity(@PathVariable("id") Integer id) {
        productFeign.openProductActivity(id);
        return ResponseEntity.noContent().build();
    }


}
