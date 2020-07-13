package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.product.ProductPropertyDTO;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 产品属性
 *
 * @author liulin
 * @date 2018/12/1
 */
@RestController
@Slf4j
@Api(tags = "ProductPropertyController")
public class ProductPropertyController {

    @Resource
    private ProductFeign productFeign;

    /**
     * 添加产品属性
     *
     * @param dto 产品属性
     */
    @PostMapping("/product/property")
    @ApiOperation(value = "添加产品属性")
    @ApiImplicitParam(name = "dto", dataType = "ProductPropertyDTO", paramType = "body", value = "产品属性信息")
    public Object save(@RequestBody ProductPropertyDTO dto) {
        return productFeign.productPropertySave(dto);
    }

    /**
     * 更新产品属性
     *
     * @param dto 产品属性
     */
    @PutMapping("/product/property")
    @ApiOperation(value = "更新产品属性")
    @ApiImplicitParams({@ApiImplicitParam(name = "dto", dataType = "ProductPropertyDTO", paramType = "body", value = "产品属性信息")})
    public Object update(@RequestBody ProductPropertyDTO dto) {
        return productFeign.productPropertyUpdate(dto);
    }

    /**
     * 批量删除产品属性
     *
     * @param propertyIds 产品属性ID集合
     */
    @DeleteMapping("/product/property/batch")
    @ApiOperation("批量删除产品属性")
    @ApiImplicitParam(value = "产品属性ID集合", name = "propertyIds", required = true, allowMultiple = true, dataType = "Long", paramType = "query")
    public Object batchDelete(Integer[] propertyIds) {
        return productFeign.productPropertyBatchDelete(propertyIds);
    }

    /**
     * 根据产品大类查询所有产品属性
     *
     * @param typeId 产品大类：1-实物商品；2-电子卡券；3-租赁商品；
     */
    @GetMapping("/product/property")
    @ApiOperation(value = "查询所有产品属性用于产品发布页面属性选择", notes = "查询所有产品属性用于产品发布页面属性选择")
    @ApiImplicitParams({@ApiImplicitParam(name = "typeId", value = "类型id", paramType = "query", dataType = "Long")})
    public Object productPropertyListForType(@RequestParam("typeId") Integer typeId) {
        return productFeign.productPropertyListForType(typeId);
    }

    /**
     * 查询产品属性的分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     */
    @GetMapping("/product/property/{pageNum}/{pageSize}")
    @ApiOperation(value = "产品属性查询", notes = "产品属性查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "产品属性名字", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startUpdateTime", value = "开始时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endUpdateTime", value = "结束时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
    })
    public Object findPage(@PathVariable(name = "pageNum") Integer pageNum,
                           @PathVariable(name = "pageSize") Integer pageSize,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) Date startUpdateTime,
                           @RequestParam(required = false) Date endUpdateTime) {
        return productFeign.productPropertyPage(pageNum, pageSize, name, startUpdateTime, endUpdateTime);
    }

}