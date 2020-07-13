package com.yimao.cloud.system.controller;

import com.yimao.cloud.base.enums.ProductStatus;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.vo.product.ProductBuyPermissionVO;
import com.yimao.cloud.system.feign.ProductFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Slf4j
@Api(tags = "ProductController")
public class ProductController {

    @Resource
    private ProductFeign productFeign;

    /**
     * 新增产品
     *
     * @param dto 产品信息
     * @return
     */
    @PostMapping(value = "/product")
    @ApiOperation(value = "新增产品", notes = "新增产品")
    @ApiImplicitParam(name = "dto", value = "产品信息", required = true, dataType = "ProductDTO", paramType = "body")
    public Object save(@RequestBody ProductDTO dto) {
        return productFeign.productSave(dto);
    }

    /**
     * 更新某个产品信息
     *
     * @param dto 产品信息体
     * @return
     */
    @PutMapping(value = "/product")
    @ApiOperation(value = "更新某个产品信息", notes = "更新某个产品信息")
    @ApiImplicitParam(name = "dto", value = "产品信息", required = true, dataType = "ProductDTO", paramType = "body")
    public Object update(@RequestBody ProductDTO dto) {
        return productFeign.productUpdate(dto);
    }

    /**
     * 修改产品状态、排序等
     *
     * @param id     产品
     * @param status 产品状态：1-未上架；2-已上架；3-已下架；4-已删除；
     * @param sorts  产品排序
     */
    @PatchMapping(value = "/product/{id}")
    @ApiOperation(value = "修改产品状态、排序等")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "产品ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "status", value = "产品状态：1-未上架；2-已上架；3-已下架；4-已删除；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hot", value = "是否推荐 1 是  0否", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sorts", value = "排序", dataType = "Long", paramType = "query")
    })
    public void updateProductStatusById(@PathVariable("id") Integer id, @RequestParam(required = false) Integer status,
                                        @RequestParam(required = false) Integer sorts, @RequestParam(required = false) Integer hot) {
        productFeign.updateProductById(id, status, sorts, hot);
    }

    /**
     * 查询某个产品信息
     *
     * @param id 产品ID
     * @return
     */
    @GetMapping(value = "/product/{id}")
    @ApiOperation(value = "查询某个产品信息", notes = "查询某个产品信息")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public Object findById(@PathVariable("id") Integer id) {
        return productFeign.getProductById(id);
    }

    /**
     * 查询单个产品信息（带扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    @ApiOperation(value = "查询单个产品信息（带扩展信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getFullById(@PathVariable("id") Integer id) {
        return productFeign.findFullProductById(id);
    }

    /**
     * @param name
     * @param categoryId
     * @param status
     * @param hot
     * @param companyId
     * @param startTime
     * @param endTime
     * @param onShelfStartTime
     * @param onShelfEndTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表", notes = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "商品名称/商品编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "产品后台分类ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：传2则是已上架  不传则为全部", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hot", value = "是否热销：1热销  0不热销", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "companyId", value = "产品公司ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "onShelfStartTime", value = "上架时间开始", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "onShelfEndTime", value = "上架时间结束", dataType = "Date", paramType = "query", format = "yyyy-MM-dd HH:mm:ss"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object findPageForBackSys(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "hot", required = false) Integer hot,
            @RequestParam(value = "companyId", required = false) Integer companyId,
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam(value = "onShelfStartTime", required = false) Date onShelfStartTime,
            @RequestParam(value = "onShelfEndTime", required = false) Date onShelfEndTime,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize) {

        List<Integer> statusList = new ArrayList<>();

        if (status == null) {
            statusList.add(ProductStatus.OFFSHELF.value);
            statusList.add(ProductStatus.NOTONSHELF.value);
            statusList.add(ProductStatus.ONSHELF.value);
        } else if (status.equals(ProductStatus.OFFSHELF.value)) {
            statusList.add(ProductStatus.OFFSHELF.value);
            statusList.add(ProductStatus.NOTONSHELF.value);

        } else if (status.equals(ProductStatus.ONSHELF.value)) {
            statusList.add(ProductStatus.ONSHELF.value);
        }


        return productFeign.findProductPage(name, categoryId, statusList, hot, companyId, startTime, endTime, onShelfStartTime, onShelfEndTime, pageNum, pageSize);
    }

    @GetMapping("/product/statistic")
    @ApiOperation(value = "查询产品统计数据")
    @ApiImplicitParams({})
    public Object findProductStatistics() {
        return productFeign.findProductStatistics();
    }


    @PatchMapping("/product")
    @ApiOperation("产品批量更新状态 逻辑删除/上下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", required = true, value = "状态码1、未上架、3已下架、2已上架、4已删除", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ids", required = true, value = "产品id ,隔开", dataType = "Long", allowMultiple = true, paramType = "query")})
    public Object updateProductStatus(@RequestParam("ids") List<Integer> ids, @RequestParam(name = "status") Integer status) {
        return productFeign.updateProductStatus(ids, status);
    }

    /**
     * 获取产品可购买人群列表
     */
    @GetMapping("/product/buy/permissions")
    @ApiOperation("获取产品可购买人群列表")
    public List<ProductBuyPermissionVO> findProductRoleTag() {
        return productFeign.getProductBuyPermissions();
    }

    /**
     * 发布产品时获取所有产品销售类型：1-经销产品；2-站长专供产品；3-特供产品；4-特批水机；
     */
    @GetMapping("/product/branch/sys")
    @ApiOperation("发布产品时获取所有产品销售类型：1-经销产品；2-站长专供产品；3-特供产品；4-特批水机；")
    @ApiImplicitParams({})
    public ResponseEntity<Object> findAppProductBranchs() {
        List<DictionaryDTO> dictionaryList = productFeign.listProductBranchSys();
        if (CollectionUtil.isEmpty(dictionaryList)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dictionaryList);
    }

    /**
     * 获取产品列表
     */
    @GetMapping(value = "/product")
    @ApiOperation(value = "查询产品列表")
    public Object findProductList() {
        return ResponseEntity.ok(productFeign.findProductList());
    }


    /**
     * 后台修改订单
     * 注：如果更换产品-》只能修改同类型同价位
     *
     * @param productId 产品ID
     * @param costId    计费方式
     * @return
     */
    @GetMapping("/product/modifyorder")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", required = true, value = "产品ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orderId", required = true, value = "订单ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "costId", required = true, value = "计费方式", dataType = "Long", allowMultiple = true, paramType = "query")})
    public ResponseEntity listProductForModifyOrder(@RequestParam(value = "orderId", required = false) Long orderId,
                                                    @RequestParam(value = "productId", required = false) Integer productId,
                                                    @RequestParam(value = "costId", required = false) Integer costId) {
        return ResponseEntity.ok(productFeign.listProductForModifyOrder(orderId, productId, costId));
    }
    
    
    /**
     * 添加活动时需要获取未上架的活动产品
     */
    @GetMapping(value = "/product/activitys")
    @ApiOperation("查询未上架的商品列表")
    public Object findActivityProduct(){
    	List<Integer> statusList=new ArrayList<Integer>();
    	statusList.add(ProductStatus.NOTONSHELF.value);//未上架
    	statusList.add(ProductStatus.OFFSHELF.value);//已下架
    	return ResponseEntity.ok(productFeign.findActivityProduct(statusList));
    }
    
}
