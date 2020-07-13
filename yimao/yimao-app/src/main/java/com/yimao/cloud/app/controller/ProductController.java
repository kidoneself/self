package com.yimao.cloud.app.controller;

import com.yimao.cloud.app.feign.ProductFeign;
import com.yimao.cloud.app.feign.UserFeign;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.ProductStatus;
import com.yimao.cloud.base.enums.UserType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Api(tags = "ProductController")
public class ProductController {

    @Resource
    private UserCache userCache;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private UserFeign userFeign;

    /**
     * APP端获取当前登录用户有权销售的产品销售类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping("/product/app/supply")
    @ApiOperation("APP端获取当前登录用户有权供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", dataType = "String", paramType = "query")
    })
    public List<DictionaryDTO> listProductSupplyCode(@RequestParam(value = "userName", required = false) String userName) {
        return productFeign.listProductSupplyCode(userName);
    }

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping("/product/category/supply")
    @ApiOperation(value = "APP查询产品供应栏目下的产品前端类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplyCode", value = "产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；", required = true, dataType = "String", paramType = "query")
    })
    public List<ProductCategoryDTO> listCategoryBySupplyCode(@RequestParam String supplyCode) {
        return productFeign.listCategoryBySupplyCode(supplyCode);
    }

    /**
     * 首页产品列表
     *
     * @param supplyCode      APP产品供应类型CODE：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     * @param frontCategoryId 产品前台一级类目ID
     * @param pageNum         页码
     * @param pageSize        每页大小
     */
    @GetMapping(value = "/product/app/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询前台产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "supplyCode", value = "APP产品供应类型CODE：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "frontCategoryId", value = "产品前台一级类目ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public Object listProductForClient(@RequestParam String supplyCode,
                                       @RequestParam(required = false) Integer frontCategoryId,
                                       @PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        return productFeign.listProductForClient(supplyCode, frontCategoryId, pageNum, pageSize);
    }

    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}")
    @ApiOperation(value = "查询产品（单个，基本信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getProductById(@PathVariable Integer id) {
        return productFeign.getProductById(id);
    }

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}/expansion")
    @ApiOperation(value = "查询产品（单个，扩展信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getFullProductById(@PathVariable Integer id) {
    	//3表示app端
        return productFeign.getFullProductById(id,3);
    }

    /**
     * 畅销榜单
     *
     * @param categoryId
     * @param hot
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/hot/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hot", value = "是否热销：1热销  0不热销", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：传2则是已上架  不传则为全部", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "need", value = "是否需要展示会员价格", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> productPage(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "hot", required = false) Integer hot,
                                          @RequestParam(value = "need", required = false) Integer need,
                                          @PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(ProductStatus.ONSHELF.value);
        return productFeign.page(categoryId, hot, 3, need, statusList, pageNum, pageSize);
    }


    /**
     * 直升会员
     *
     * @param categoryId
     * @param hot
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/product/water/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hot", value = "是否热销：1热销  0不热销", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：传2则是已上架  不传则为全部", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "need", value = "是否需要展示会员价格", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> waterPage(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                        @RequestParam(value = "hot", required = false) Integer hot,
                                        @RequestParam(value = "need", required = false) Integer need,
                                        @PathVariable("pageNum") Integer pageNum,
                                        @PathVariable("pageSize") Integer pageSize) {
        List<Integer> statusList = new ArrayList<>();
        statusList.add(ProductStatus.ONSHELF.value);
        return productFeign.page(categoryId, hot, 3, need, statusList, pageNum, pageSize);
    }

    //查询所有产品一级分类

    @GetMapping("/category/first/all")
    @ApiOperation(notes = "查询所有产品一级分类", value = "查询所有产品一级分类")
    public Object getAllFirstCategory() {
        List<ProductCategoryDTO> list = productFeign.getAllFirstCategory();
        return ResponseEntity.ok(list);
    }

    //查询所有产品一级分类（经销商app收益模块一级分类）
    @GetMapping("/category/first/productIncome")
    @ApiOperation(notes = "查询所有产品一级分类（经销商app收益模块一级分类）", value = "查询所有产品一级分类（经销商app收益模块一级分类）")
    public Object getFirstCategoryProductIncome() {
        UserDTO user = userFeign.getBasicUserById(userCache.getUserId());
        //去除折机经销商
        if (UserType.DISTRIBUTOR_DISCOUNT_50.value == user.getUserType()) {
            throw new BadRequestException("该经销商类型无权限查看。");
        }
        List<ProductCategoryDTO> list = productFeign.getFirstCategoryForAppProductIncome();
        return ResponseEntity.ok(list);
    }


    /**
     * 全部分类
     *
     * @return
     * @Author lizhiqiang
     * @Date 2019-07-24
     * @Param
     */
    @GetMapping(value = "/product/front/{id}")
    @ApiOperation(notes = "根据前端分类id展示商品列表", value = "根据前端分类id展示商品列表")
    @ApiImplicitParam(name = "id", value = "前端一级分类id", dataType = "Long", paramType = "path")
    public Object getProductByFrontId(@PathVariable("id") Integer id) {
        List<ProductDTO> list = productFeign.getProductByFrontId(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/product/waters")
    @ApiOperation(notes = "直升会员列表", value = "直升会员列表")
    public List<ProductDTO> getWaterProduct() {
        return productFeign.getWaterProduct();
    }


}
