package com.yimao.cloud.system.controller;

import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "ProductCostController")
public class ProductCostController {

    @Resource
    private ProductFeign productFeign;

    /**
     * 新增计费方式
     *
     * @param dto 计费方式
     */
    @PostMapping(value = "/product/cost")
    @ApiOperation(value = "新增计费方式")
    @ApiImplicitParam(name = "dto", value = "计费方式", required = true, dataType = "ProductCostDTO", paramType = "body")
    public Object save(@RequestBody ProductCostDTO dto) {
        productFeign.saveProductCost(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除计费方式
     *
     * @param id 计费方式ID
     */
    @DeleteMapping(value = "/product/cost/{id}")
    @ApiOperation(value = "删除计费方式")
    @ApiImplicitParam(name = "id", value = "计费方式ID", required = true, dataType = "Long", paramType = "path")
    public Object delete(@PathVariable("id") Integer id) {
        productFeign.deleteProductCost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改计费方式
     *
     * @param dto 计费方式
     */
    @PutMapping(value = "/product/cost")
    @ApiOperation(value = "修改计费方式")
    @ApiImplicitParam(name = "dto", value = "计费方式", required = true, dataType = "ProductCostDTO", paramType = "body")
    public Object update(@RequestBody ProductCostDTO dto) {
        productFeign.updateProductCost(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 启用/禁用计费方式
     *
     * @param ids     计费方式ID集合
     * @param deleted 删除标识：0-未删除；1-已删除；
     */
    @PatchMapping(value = "/product/cost")
    @ApiOperation(value = "启用/禁用计费方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "计费方式ID集合", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "deleted", value = "启用/禁用：0-禁用；1-启用；", dataType = "Long", paramType = "query")
    })
    public Object forbidden(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "deleted") Boolean deleted) {
        productFeign.forbiddenProductCost(ids, deleted);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据ID获取计费方式
     *
     * @param id 产品价格体系模版
     */
    @GetMapping(value = "/product/cost/{id}")
    @ApiOperation(value = "根据ID获取计费方式")
    @ApiImplicitParam(name = "id", value = "计费方式ID", required = true, dataType = "Long", paramType = "path")
    public Object getById(@PathVariable("id") Integer id) {
        ProductCostDTO dto = productFeign.getProductCostById(id);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * 分页获取计费方式
     *
     * @param pageNum          第几页
     * @param pageSize         每页大小
     * @param name             计费方式名称
     * @param type             计费方式：1-流量计费；2-时长计费；
     * @param firstCategoryId  后台一级产品类目ID
     * @param secondCategoryId 后台二级产品类目ID
     */
    @GetMapping(value = "/product/cost/{pageNum}/{pageSize}")
    @ApiOperation(value = "分页获取计费方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "计费方式名称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "计费方式：1-流量计费；2-时长计费；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "modelType", value = "模板类型：1-首年 2-续费", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "firstCategoryId", value = "后台一级分类", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "secondCategoryId", value = "后台二级分类", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "deleted", value = "是否启用0-未启用 1-启用", dataType = "Integer", paramType = "query")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestParam(value = "name", required = false) String name,
                       @RequestParam(value = "type", required = false) Integer type,
                       @RequestParam(value = "modelType", required = false) Integer modelType,
                       @RequestParam(value = "firstCategoryId", required = false) Integer firstCategoryId,
                       @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                       @RequestParam(value = "deleted", required = false) Integer deleted
    ) {
        PageVO<ProductCostVO> page = productFeign.pageProductCost(pageNum, pageSize, name, type, modelType, firstCategoryId, secondCategoryId, deleted);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取续费计费方式列表
     */
    @GetMapping(value = "/product/renewcost")
    @ApiOperation(value = "获取续费计费方式列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldCostId", value = "老计费方式ID", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "type", value = "计费方式类型：1-流量计费；2-时长计费；", dataType = "Long", paramType = "query")
    })
    public List<ProductCostDTO> listRenewCost(@RequestParam Integer oldCostId, @RequestParam(required = false) Integer type) {
        //获取续费时可选择的计费方式列表
        return productFeign.listProductRenewCostByOldCostId(oldCostId, type);
    }

}
