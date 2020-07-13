package com.yimao.cloud.wechat.controller;

import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.wechat.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 产品类目
 *
 * @auther: liu.lin
 * @date: 2019/1/21
 */
@RestController
@Slf4j
@Api(tags = "ProductCategoryController")
public class ProductCategoryController {

    @Resource
    private ProductFeign productFeign;

    /*
     *//**
     * 查询产品类目（单个）
     *
     * @param id 产品类目ID
     *//*
    @GetMapping(value = "/product/category/{id}")
    @ApiOperation(value = "查询产品类目")
    @ApiImplicitParam(name = "id", value = "产品类目ID", required = true, dataType = "Long", paramType = "path")
    public ProductCategoryDTO getById(@PathVariable(value = "id") Integer id) {
        return productFeign.getProductCategoryById(id);
    }*/


    /**
     * 查询产品类目（分页）
     *
     * @param type     前台类目还是后台类目：1-后台类目；2-前台类目；
     * @param terminal 终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；
     * @param pid      父类目ID
     * @param level    产品类目等级：1-一级；2-二级；3-三级；
     * @param pageNum  页码
     * @param pageSize 每页大小
     */
    @GetMapping(value = "/product/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询分类列表", notes = "查询分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "前台类目还是后台类目：1-后台类目；2-前台类目；", defaultValue = "2", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；", defaultValue = "1", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "父类目ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "产品类目等级：1-一级；2-二级；3-三级；", defaultValue = "1", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductCategoryDTO> page(@RequestParam(required = false) Integer type,
                                           @RequestParam(required = false) Integer terminal,
                                           @RequestParam(required = false) Integer pid,
                                           @RequestParam(required = false) Integer level,
                                           @PathVariable Integer pageNum,
                                           @PathVariable Integer pageSize) {
        return productFeign.pageProductCategory(type, terminal, pid, level, pageNum, pageSize);
    }


}
