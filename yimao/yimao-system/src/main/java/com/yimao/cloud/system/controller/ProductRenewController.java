package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.product.ProductRenewDTO;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 续费模板
 *
 * @auther: liu.lin
 * @date: 2019/1/21
 */
@RestController
@Slf4j
@Api(tags = "ProductRenewController")
public class ProductRenewController {

    @Resource
    private ProductFeign productFeign;

    /**
     * 创建商品续费
     * @param dto
     * @return
     */
    @PostMapping(value = "/product/renew")
    @ApiOperation(value = "创建商品续费", notes = "创建商品续费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dto", required = true, value = "", dataType = "ProductRenewDTO", paramType = "body")
    })
    public Object save(@RequestBody ProductRenewDTO dto) {
        return productFeign.productRenewSave(dto);
    }

    /**
     * 删除商品续费
     *
     * @param id 管理员ID
     * @return
     */
    @DeleteMapping(value = "/product/renew/{id}")
    @ApiOperation(value = "删除商品续费", notes = "删除商品续费")
    @ApiImplicitParam(name = "id", value = "价格体系ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        return productFeign.productRenewDelete(id);
    }


    /**
     * 更新商品续费
     *
     * @param dto 商品续费信息
     * @return
     */
    @PutMapping(value = "/product/renew")
    @ApiOperation(value = "更新商品续费", notes = "更新商品续费")
    @ApiImplicitParam(name = "dto", value = "商品续费", required = true, dataType = "ProductRenewDTO", paramType = "body")
    public Object update(@RequestBody ProductRenewDTO dto) {
        return  productFeign.productRenewUpdate(dto);
    }

    /**
     * 查询特定商品续费
     *
     * @param id 产品商品续费
     * @return
     */
    @GetMapping(value = "/product/renew/{id}")
    @ApiOperation(value = "根据ID查询商品续费", notes = "根据ID查询商品续费")
    @ApiImplicitParam(name = "id", value = "续费id", required = true, dataType = "Long", paramType = "path")
    public Object getById(@PathVariable("id") Integer id) {
        return productFeign.productRenewGetById(id);
    }

    /**
     * 查询商品续费列表
     *
     * @param pageNum          第几页
     * @param pageSize         每页大小
     * @param categoryId       一级分类id
     * @param secondCategoryId 二级分类id
     * @return
     */
    @GetMapping(value = "/product/renew/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页查询所有商品续费", notes = "分页查询所有商品续费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "一级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "secondCategoryId", value = "二级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "thirdCategoryId", value = "三级级分类id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestParam(value = "categoryId", required = false) Integer categoryId,
                       @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                       @RequestParam(value = "thirdCategoryId", required = false) Integer thirdCategoryId) {
        return  productFeign.productRenewPage(pageNum,pageSize,categoryId,secondCategoryId,thirdCategoryId);
    }

}
