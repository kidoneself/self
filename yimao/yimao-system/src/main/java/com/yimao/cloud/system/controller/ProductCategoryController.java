package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 创建产品类目
     *
     * @param dto 产品类目
     */
    @PostMapping(value = "/product/category")
    @ApiOperation(value = "创建产品类目")
    @ApiImplicitParam(name = "dto", value = "产品类目", required = true, dataType = "ProductCategoryDTO", paramType = "body")
    public void save(@RequestBody ProductCategoryDTO dto) {
        productFeign.saveProductCategory(dto);
    }

    /**
     * 删除产品类目
     *
     * @param id 产品类目ID
     */
    @DeleteMapping(value = "/product/category/{id}")
    @ApiOperation(value = "删除产品类目")
    @ApiImplicitParam(name = "id", value = "产品类目ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable Integer id) {
        productFeign.deleteProductCategoryById(id);
    }

    /**
     * 修改产品类目
     *
     * @param dto 产品类目
     */
    @PutMapping(value = "/product/category")
    @ApiOperation(value = "修改产品类目")
    @ApiImplicitParam(name = "dto", value = "产品类目", required = true, dataType = "ProductCategoryDTO", paramType = "body")
    public void update(@RequestBody ProductCategoryDTO dto) {
        productFeign.updateProductCategory(dto);
    }

    /**
     * 查询产品类目（单个）
     *
     * @param id 产品类目ID
     */
    @GetMapping(value = "/product/category/{id}")
    @ApiOperation(value = "查询产品类目")
    @ApiImplicitParam(name = "id", value = "产品类目ID", required = true, dataType = "Long", paramType = "path")
    public ProductCategoryDTO getById(@PathVariable(value = "id") Integer id) {
        return productFeign.getProductCategoryById(id);
    }


    /**
     * 查询产品类目（分页）
     *
     * @param name      类目名称
     * @param type      前台类目还是后台类目：1-后台类目；2-前台类目；
     * @param terminal  终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；
     * @param pid       父类目ID
     * @param level     产品类目等级：1-一级；2-二级；3-三级；
     * @param companyId 产品公司ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageNum   页码
     * @param pageSize  每页大小
     */
    @GetMapping(value = "/product/category/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询分类列表", notes = "查询分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "类目名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "前台类目还是后台类目：1-后台类目；2-前台类目；", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "终端：1-健康e家公众号；2-小猫店小程序；3-经销商APP；", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "父类目ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "level", value = "产品类目等级：1-一级；2-二级；3-三级；", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "companyId", value = "产品公司ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductCategoryDTO> page(@RequestParam(required = false) String name, @RequestParam(required = false) Integer type,
                                           @RequestParam(required = false) Integer terminal, @RequestParam(required = false) Integer pid,
                                           @RequestParam(required = false) Integer level, @RequestParam(required = false) Integer companyId,
                                           @RequestParam(required = false) Date startTime, @RequestParam(required = false) Date endTime,
                                           @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return productFeign.pageProductCategory(name, type, terminal, pid, level, companyId, startTime, endTime, pageNum, pageSize);
    }

    /**
     * 修改产品类目排序
     *
     * @param id    产品类目ID
     * @param sorts 排序值
     */
    @PatchMapping("/product/category/{id}")
    @ApiOperation(value = "修改产品类目排序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Long"),
            @ApiImplicitParam(name = "sorts", value = "排序", required = true, paramType = "query", dataType = "Long")
    })
    public void updateCategorySorts(@PathVariable Integer id, @RequestParam(required = false) Integer sorts) {
        if (sorts != null) {
            productFeign.updateCategorySorts(id, sorts);
        }
    }

}
