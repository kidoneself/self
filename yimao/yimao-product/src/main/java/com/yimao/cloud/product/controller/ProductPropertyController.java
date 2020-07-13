package com.yimao.cloud.product.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.pojo.dto.product.ProductPropertyDTO;
import com.yimao.cloud.pojo.query.product.ProductPropertyQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductPropertyVO;
import com.yimao.cloud.product.po.ProductProperty;
import com.yimao.cloud.product.service.ProductPropertyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author liulin
 * @date 2018/12/1
 */
@RestController
@Slf4j
@Api(tags = "ProductPropertyController")
public class ProductPropertyController {

    @Resource
    private ProductPropertyService productPropertyService;

    /**
     * 添加产品属性
     *
     * @param dto 产品属性
     */
    @PostMapping("/product/property")
    @ApiOperation(value = "添加产品属性")
    @ApiImplicitParam(name = "dto", dataType = "ProductPropertyDTO", paramType = "body", value = "产品属性")
    public Object save(@RequestBody ProductPropertyDTO dto) {
        ProductProperty property = new ProductProperty(dto);
        productPropertyService.savePropertyAndValue(property, dto.getPropertyValueStr());
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改产品属性
     *
     * @param dto 产品属性
     */
    @PutMapping("/product/property")
    @ApiOperation(value = "修改产品属性")
    @ApiImplicitParam(name = "dto", dataType = "ProductPropertyDTO", paramType = "body", value = "产品属性")
    public Object update(@RequestBody ProductPropertyDTO dto) {
        ProductProperty property = new ProductProperty(dto);
        productPropertyService.updatePropertyAndValue(property, dto.getPropertyValueStr());
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除产品属性
     *
     * @param propertyIds 产品属性ID集合
     */
    @DeleteMapping("/product/property/batch")
    @ApiOperation(value = "批量删除产品属性")
    @ApiImplicitParam(value = "产品属性ID集合", name = "propertyIds", required = true, allowMultiple = true, dataType = "Long", paramType = "query")
    public Object batchDelete(Integer[] propertyIds) {
        if (propertyIds == null || propertyIds.length <= 0) {
            throw new BadRequestException("操作失败，请选择要删除的对象。");
        }
        if (propertyIds.length >= 1000) {
            throw new BadRequestException("操作失败，删除对象过多。");
        }
        List<Integer> ids = Arrays.asList(propertyIds);
        productPropertyService.batchUpdate(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据产品大类查询所有产品属性
     *
     * @param typeId 产品大类：1-实物商品；2-电子卡券；3-租赁商品；
     */
    @GetMapping("/product/property")
    @ApiOperation(value = "根据产品大类查询所有产品属性")
    @ApiImplicitParam(name = "typeId", value = "类型id", paramType = "query", required = true, dataType = "Long")
    public Object listPropertyForProduct(@RequestParam("typeId") Integer typeId) {
        List<ProductPropertyVO> list = productPropertyService.listProductPropertyByType(typeId);
        if (list == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 查询产品属性的分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param query
     */
    @GetMapping("/product/property/{pageNum}/{pageSize}")
    @ApiOperation(value = "产品属性查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "产品属性名字", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startUpdateTime", value = "开始时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
            @ApiImplicitParam(name = "endUpdateTime", value = "结束时间", dataType = "Date", format = "yyyy-MM-dd HH:mm:ss", paramType = "query"),
    })
    public Object page(@PathVariable(name = "pageNum") Integer pageNum,
                       @PathVariable(name = "pageSize") Integer pageSize,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Date startUpdateTime,
                       @RequestParam(required = false) Date endUpdateTime) {
        ProductPropertyQuery query = new ProductPropertyQuery();
        if (StringUtil.isNotBlank(name)) {
            query.setName(name);
        }
        query.setStartUpdateTime(startUpdateTime);
        query.setEndUpdateTime(endUpdateTime);
        PageVO<ProductPropertyVO> page = productPropertyService.page(pageNum, pageSize, query);
        return ResponseEntity.ok(page);
    }

}