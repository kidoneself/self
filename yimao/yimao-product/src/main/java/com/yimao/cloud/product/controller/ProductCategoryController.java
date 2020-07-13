package com.yimao.cloud.product.controller;

import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.pojo.dto.product.ProductCategoryCascadeDTO;
import com.yimao.cloud.pojo.dto.product.ProductCategoryDTO;
import com.yimao.cloud.pojo.query.product.ProductCategoryQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.product.mapper.ProductCategoryMapper;
import com.yimao.cloud.product.po.ProductCategory;
import com.yimao.cloud.product.service.ProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Api(tags = "ProductCategoryController")
public class ProductCategoryController {

    @Resource
    private ProductCategoryService productCategoryService;
    @Resource
    private ProductCategoryMapper productCategoryMapper;

    /**
     * 创建产品类目
     *
     * @param dto 产品类目
     */
    @PostMapping(value = "/product/category")
    @ApiOperation(value = "创建产品类目")
    @ApiImplicitParam(name = "dto", value = "产品类目", required = true, dataType = "ProductCategoryDTO", paramType = "body")
    public void save(@RequestBody ProductCategoryDTO dto) {
        ProductCategory productCategory = new ProductCategory(dto);
        productCategoryService.saveProductCategory(productCategory);
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
        productCategoryService.deleteProductCategoryById(id);
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
        if (dto.getId() == null) {
            throw new BadRequestException("操作失败，修改对象不存在。");
        } else {
            ProductCategory productCategory = new ProductCategory(dto);
            productCategoryService.updateProductCategory(productCategory);
        }
    }

    /**
     * 查询产品类目（单个）
     *
     * @param id 产品类目ID
     */
    @GetMapping(value = "/product/category/{id}")
    @ApiOperation(value = "查询产品类目")
    @ApiImplicitParam(name = "id", value = "产品类目ID", required = true, dataType = "Long", paramType = "path")
    public ProductCategoryDTO getById(@PathVariable Integer id) {
        ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
        if (productCategory == null) {
            return null;
        }
        ProductCategoryDTO dto = new ProductCategoryDTO();
        productCategory.convert(dto);
        return dto;
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
        ProductCategoryQuery query = new ProductCategoryQuery();
        query.setName(name);
        query.setType(type);
        query.setTerminal(terminal);
        query.setPid(pid);
        query.setLevel(level);
        query.setCompanyId(companyId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        return productCategoryService.page(pageNum, pageSize, query);
    }

    /**
     * APP查询产品供应栏目下的产品前端类目
     *
     * @param supplyCode 产品供应类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping(value = "/product/category/supply")
    @ApiOperation(value = "APP查询产品销售栏目下的前端产品分类")
    @ApiImplicitParam(name = "supplyCode", value = "栏目code", required = true, dataType = "String", paramType = "query")
    public List<ProductCategoryDTO> listCategoryBySupplyCode(@RequestParam String supplyCode) {
        return productCategoryService.listCategoryBySupplyCode(supplyCode);
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
            productCategoryService.updateCategorySorts(id, sorts);
        }
    }

    /**
     * @param id
     * @Description: 查询三级分类
     * @author ycl
     * @Return: org.springframework.http.ResponseEntity
     * @Create: 2019/5/6 16:02
     */
    @GetMapping(value = "/productcategory/{id}")
    @ApiOperation(value = "查询三级分类")
    public ResponseEntity getBottomCatgory(@PathVariable Integer id) {
        List<ProductCategoryDTO> categoryDTOList = productCategoryService.getBottomCatgory(id);
        return ResponseEntity.ok(categoryDTOList);
    }


    @GetMapping(value = "/product/oneCategory/{categoryId}")
    @ApiOperation(value = "根据三级类目ID查询一级类目")
    public ProductCategoryDTO getOneCategory(@PathVariable Integer categoryId) {
        return productCategoryService.getOneCategory(categoryId);
    }


    /**
     * @param categoryId 三级类目ID
     * @Description: 根据三级类目ID查询二级类目
     * @author ycl
     * @Return: com.yimao.cloud.pojo.dto.product.ProductCategoryDTO
     * @Create: 2019/5/17 18:56
     */
    @GetMapping(value = "/product/twoCategory/{categoryId}")
    @ApiOperation(value = "根据三级类目ID查询二级类目")
    public ProductCategoryDTO getTwoCategory(@PathVariable Integer categoryId) {
        return productCategoryService.getTwoCategory(categoryId);
    }

    /**
     * 获取产品所有的一级类目
     *
     * @return com.yimao.cloud.pojo.dto.product.ProductCategoryDTO
     * @author hhf
     * @date 2019/5/21
     */
    @GetMapping(value = "/product/category/first")
    @ApiOperation(value = "获取产品所有的一级类目")
    public List<ProductCategoryDTO> getFirstProductCategory() {
        return productCategoryService.getFirstProductCategory();
    }

    /**
     * @param id
     * @description 根据产品三级类目ID查询类目级联信息
     * @author zhilin.he
     * @date 2019/5/24 14:21
     */
    @GetMapping(value = "/product/category/cascade/{id}")
    @ApiOperation(value = "根据产品三级类目ID查询类目级联信息")
    @ApiImplicitParam(name = "id", value = "产品三级类目ID", required = true, dataType = "Long", paramType = "path")
    public ProductCategoryCascadeDTO getProductCategoryCascadeById(@PathVariable("id") Integer id) {
        return productCategoryService.getProductCategoryCascadeById(id);
    }

    /**
     * 获取产品前台一级类目
     *
     * @return com.yimao.cloud.pojo.dto.product.ProductCategoryDTO
     * @author hhf
     * @date 2019/7/11
     */
    @GetMapping(value = "/product/category/front")
    @ApiOperation(value = "获取产品前台一级类目")
    public ProductCategoryDTO getFrontCategoryByProductId(@RequestParam("productId") Integer productId,
                                                          @RequestParam(value = "terminal", required = false) Integer terminal) {
        return productCategoryService.getFrontCategoryByProductId(productId, terminal);
    }

    /**
     * 查询所有产品一级分类
     *
     * @return
     */
    @GetMapping("/category/first/all")
    @ApiOperation(notes = "查询所有产品一级分类", value = "查询所有产品一级分类")
    public Object getAllFirstCategory() {
        List<ProductCategoryDTO> list = productCategoryService.getAllFirstCategory();
        return ResponseEntity.ok(list);
    }

    /**
     * 查询所有产品一级分类（经销商app收益模块一级分类）
     *
     * @return
     */
    @GetMapping("/category/first/productIncome")
    @ApiOperation(notes = "查询所有产品一级分类", value = "查询所有产品一级分类")
    public Object getFirstCategoryForAppProductIncome() {
        List<ProductCategoryDTO> list = productCategoryService.getFirstCategoryForAppProductIncome();
        return ResponseEntity.ok(list);
    }

    /**
     * 查询所有产品一级分类
     *
     * @return
     */
    @GetMapping("/product/category/getByOldId")
    public ProductCategoryDTO getByOldId(@RequestParam String oldId) {
        return productCategoryMapper.selectByOldId(oldId);
    }

    /**
     * 根据产品id获取到其三级分类对应库存物资id
     *
     * @param productId 产品ID
     */
    @GetMapping(value = "/product/category/goodsId/{productId}")
    @ApiOperation(value = "根据产品id获取到其三级分类对应库存物资id")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public Object getGoodsIdByProductId(@PathVariable("productId") Integer productId) {

        return productCategoryService.getGoodsIdByProductId(productId);
    }

}
