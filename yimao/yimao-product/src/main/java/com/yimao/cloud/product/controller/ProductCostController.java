package com.yimao.cloud.product.controller;

import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.query.product.ProductCostQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductCostVO;
import com.yimao.cloud.product.mapper.ProductCostMapper;
import com.yimao.cloud.product.po.ProductCost;
import com.yimao.cloud.product.service.ProductCostService;
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

import java.math.BigDecimal;
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
    private ProductCostService productCostService;
    @Resource
    private ProductCostMapper productCostMapper;

    /**
     * 新增计费方式
     *
     * @param dto 计费方式
     */
    @PostMapping(value = "/product/cost")
    @ApiOperation(value = "新增计费方式")
    @ApiImplicitParam(name = "dto", value = "计费方式", required = true, dataType = "ProductCostDTO", paramType = "body")
    public void save(@RequestBody ProductCostDTO dto) {
        ProductCost productCost = new ProductCost(dto);
        productCostService.saveProductCost(productCost);
    }

    /**
     * 删除计费方式
     *
     * @param id 计费方式ID
     */
    @DeleteMapping(value = "/product/cost/{id}")
    @ApiOperation(value = "删除计费方式")
    @ApiImplicitParam(name = "id", value = "计费方式ID", required = true, dataType = "Long", paramType = "path")
    public void delete(@PathVariable("id") Integer id) {
        productCostService.deleteProductCostById(id);
    }

    /**
     * 修改计费方式
     *
     * @param dto 计费方式
     */
    @PutMapping(value = "/product/cost")
    @ApiOperation(value = "修改计费方式")
    @ApiImplicitParam(name = "dto", value = "计费方式", required = true, dataType = "ProductCostDTO", paramType = "body")
    public void update(@RequestBody ProductCostDTO dto) {
        ProductCost productCost = new ProductCost(dto);
        productCostService.updateProductCost(productCost);
    }

    /**
     * 启用/禁用计费方式
     *
     * @param ids 计费方式ID集合
     */
    @PatchMapping(value = "/product/cost")
    @ApiOperation(value = "启用/禁用计费方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "计费方式ID集合", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "deleted", value = "启用/禁用：0-禁用；1-启用；", dataType = "Long", paramType = "query")
    })
    public void forbidden(@RequestParam(value = "ids") Integer[] ids, @RequestParam(value = "deleted") Boolean deleted) {
        productCostService.forbiddenProduct(ids, deleted);
    }

    /**
     * 根据ID获取计费方式
     *
     * @param id 产品价格体系模版
     */
    @GetMapping(value = "/product/cost/{id}")
    @ApiOperation(value = "根据ID获取计费方式")
    @ApiImplicitParam(name = "id", value = "计费方式ID", required = true, dataType = "Long", paramType = "path")
    public ProductCostDTO getById(@PathVariable("id") Integer id) {
        ProductCost productCost = productCostMapper.selectByPrimaryKey(id);
        if (productCost == null) {
            return null;
        }
        ProductCostDTO dto = new ProductCostDTO();
        productCost.convert(dto);
        return dto;
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
            @ApiImplicitParam(name = "type", value = "计费方式：1-流量计费；2-时长计费；", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "modelType", value = "模板类型：1-首年 2-续费", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "firstCategoryId", value = "后台一级产品类目ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "secondCategoryId", value = "后台二级产品类目ID", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "deleted", value = "是否启用0-未启用 1-启用", dataType = "Integer", paramType = "query")
    })
    public Object page(@PathVariable(value = "pageNum") Integer pageNum,
                       @PathVariable(value = "pageSize") Integer pageSize,
                       @RequestParam(value = "name", required = false) String name,
                       @RequestParam(value = "type", required = false) Integer type,
                       @RequestParam(value = "modelType", required = false) Integer modelType,
                       @RequestParam(value = "firstCategoryId", required = false) Integer firstCategoryId,
                       @RequestParam(value = "secondCategoryId", required = false) Integer secondCategoryId,
                       @RequestParam(value = "deleted", required = false) Integer deleted) {
        ProductCostQuery query = new ProductCostQuery();
        query.setName(name);
        query.setType(type);
        query.setModelType(modelType);
        query.setFirstCategoryId(firstCategoryId);
        query.setSecondCategoryId(secondCategoryId);
        query.setDeleted(deleted);

        PageVO<ProductCostVO> page = productCostService.page(pageNum, pageSize, query);
        return ResponseEntity.ok(page);
    }

    /**
     * 获取计费方式列表
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/product/cost")
    @ApiOperation(value = "获取计费方式列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "oldCostId", value = "原计费方式ID", dataType = "Long", paramType = "query")
    })
    public List<ProductCostDTO> list(@RequestParam(value="productId",required = false) Integer productId, @RequestParam(value="oldCostId",required = false) Integer oldCostId) {
        if (oldCostId != null) {
            //获取变更计费方式时可选择的计费方式列表
            return productCostMapper.listByOldCostId(oldCostId);
        } else {
            return productCostMapper.listProductCostByProductId(productId);
        }
    }

    /**
     * 获取续费计费方式列表
     */
    @GetMapping(value = "/product/renewcost")
    public List<ProductCostDTO> listRenewCost(@RequestParam Integer oldCostId, @RequestParam(required = false) Integer type) {
        //获取续费时可选择的计费方式列表
        return productCostMapper.listRenewCostByOldCostId(oldCostId, type);
    }
    
    /***
     * 获取已上架的产品型号的计费方式
     * @param price
     * @return
     */
    @GetMapping(value = "/product/online/cost")
    public List<ProductCostDTO> getOnlinePoruductCost(@RequestParam(value = "price",required = false)BigDecimal price){
    	return productCostMapper.getOnlinePoruductCost(price);
    }

}
