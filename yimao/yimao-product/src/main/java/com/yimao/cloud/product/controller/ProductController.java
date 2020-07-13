package com.yimao.cloud.product.controller;

import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.ProductBuyPermission;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.product.VirtualProductConfigDTO;
import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import com.yimao.cloud.pojo.query.product.ProductQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductBuyPermissionVO;
import com.yimao.cloud.pojo.vo.product.ProductStatusStatisticVO;
import com.yimao.cloud.product.mapper.ProductMapper;
import com.yimao.cloud.product.po.Product;
import com.yimao.cloud.product.po.VirtualProductConfig;
import com.yimao.cloud.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 产品控制器
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@RestController
@Api(tags = "ProductController")
public class ProductController {

    @Resource
    private ProductService productService;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private UserCache userCache;

    /**
     * 创建产品
     *
     * @param dto 产品
     */
    @PostMapping(value = "/product")
    @ApiOperation(value = "创建产品")
    @ApiImplicitParam(name = "dto", value = "产品", required = true, dataType = "ProductDTO", paramType = "body")
    public ResponseEntity<Object> save(@RequestBody ProductDTO dto) {
        Set<Integer> frontCategoryIds = dto.getFrontCategoryIds();
        Set<Integer> incomeRuleIds = dto.getIncomeRuleIds();
        Set<Integer> costIds = dto.getCostIds();
        Set<Integer> distributorIds = dto.getDistributorIds();
        VirtualProductConfigDTO virtualProductConfig = dto.getVirtualProductConfig();
        Product product = new Product(dto);
        productService.save(product, frontCategoryIds, incomeRuleIds, costIds, distributorIds, virtualProductConfig);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改产品
     *
     * @param dto 产品
     */
    @PutMapping(value = "/product")
    @ApiOperation(value = "修改产品")
    @ApiImplicitParam(name = "dto", value = "产品", required = true, dataType = "ProductDTO", paramType = "body")
    public ResponseEntity<Object> update(@RequestBody ProductDTO dto) {
        if (null == dto || null == dto.getId()) {
            throw new BadRequestException("产品ID不能为空。");
        }
        Set<Integer> frontCategoryIds = dto.getFrontCategoryIds();
        Set<Integer> incomeRuleIds = dto.getIncomeRuleIds();
        Set<Integer> costIds = dto.getCostIds();
        Set<Integer> distributorIds = dto.getDistributorIds();
        VirtualProductConfigDTO virtualProductConfigDTO = dto.getVirtualProductConfig();

        Product product = new Product(dto);
        productService.update(product, frontCategoryIds, incomeRuleIds, costIds, distributorIds, virtualProductConfigDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改产品状态、排序、库存
     *
     * @param id     产品ID
     * @param status 产品状态：1-未上架；2-已上架；3-已下架；4-已删除；
     * @param sorts  排序
     * @param stock  减掉的库存
     */
    @PatchMapping(value = "/product/{id}")
    @ApiOperation(value = "修改产品状态、排序、库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "产品ID", dataType = "Long", required = true, paramType = "path"),
            @ApiImplicitParam(name = "status", value = "产品状态：1-未上架；2-已上架；3-已下架；4-已删除；", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "sorts", value = "排序", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hot", value = "是否热销", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "stock", value = "减掉的库存", dataType = "Long", paramType = "query")
    })
    public void updateById(@PathVariable Integer id,
                           @RequestParam(required = false) Integer status,
                           @RequestParam(required = false) Integer sorts,
                           @RequestParam(required = false) Integer stock,
                           @RequestParam(required = false) Integer hot) {
        Product record = new Product();
        record.setId(id);
        record.setStatus(status);
        record.setSorts(sorts);
        record.setStock(stock);
        record.setHot(hot);
        productService.update(record);
    }


    /**
     * 批量修改产品状态
     *
     * @param ids    产品ID
     * @param status 产品状态：1-未上架；2-已上架；3-已下架；4-已删除；
     */
    @PatchMapping(value = "/product")
    @ApiOperation(value = "批量修改产品状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "产品ID", dataType = "Long", required = true, allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：1-未上架；2-已上架；3-已下架；4-已删除；", dataType = "Long", paramType = "query"),
    })
    public void updateByIds(@RequestParam List<Integer> ids,
                            @RequestParam Integer status) {
        String updater = userCache.getCurrentAdminRealName();
        productService.updateBatch(updater, ids, status);
    }


    /**
     * 修改产品售出量
     *
     * @param id 产品ID
     */
    @PatchMapping(value = "/product/buys/count")
    @ApiOperation(value = "修改产品售出量")
    public void updateBuyCount(@RequestParam Integer id, @RequestParam Integer count) {
        productService.updateBuyCount(id, count);
    }

    /**
     * 查询产品（单个，基本信息）
     *
     * @param id 产品ID
     */
    @GetMapping(value = "/product/{id}")
    @ApiOperation(value = "查询产品（单个，基本信息）")
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path")
    public ProductDTO getById(@PathVariable("id") Integer id) {
        ProductDTO productDTO = productService.getProductById(id);
        if (productDTO != null) {
            //虚拟产品获取虚拟产品配置信息
            if (productDTO.getMode() == ProductModeEnum.VIRTUAL.value) {
                VirtualProductConfig virtualProductConfig = productService.getVirtualProductConfigByProductId(id);
                if (virtualProductConfig != null) {
                    VirtualProductConfigDTO virtualDTO = new VirtualProductConfigDTO();
                    virtualProductConfig.convert(virtualDTO);
                    productDTO.setVirtualProductConfig(virtualDTO);
                } else {
                    //如果产品时虚拟产品，但是没找到虚拟配置，则报错
                    throw new NotFoundException("未找到相关虚拟产品配置信息");
                }
            }
        }
        return productDTO;
    }

    /**
     * 查询产品（单个，扩展信息）
     *
     * @param id 产品ID
     * @param terminal 终端：1-健康e家公众号；3-翼猫APP
     * 查询前台类目需要区别终端，会出现类别重复问题
     */
    @GetMapping(value = "/product/{id}/expansion")
    @ApiOperation(value = "查询产品（单个，扩展信息）")
    @ApiImplicitParams({
    @ApiImplicitParam(name = "id", value = "产品ID", required = true, dataType = "Long", paramType = "path"),
    @ApiImplicitParam(name = "terminal", value = "终端：1-健康e家公众号；3-翼猫APP", required = false, dataType = "Long", paramType = "query")
    })
    public ProductDTO getFullById(@PathVariable("id") Integer id,@RequestParam(name="terminal",required=false) Integer terminal) {
        return productService.getFullProductById(id,terminal);
    }

    /**
     * 后台修改订单产品型号时查询同类产品列表（二级类目一致，价格一致，计费方式一致）
     *
     * @param productId 产品ID
     * @param costId    计费方式ID
     */
    @GetMapping(value = "/product/modifyorder")
    @ApiOperation(value = "后台修改订单产品型号时查询同类产品列表（二级类目一致，价格一致，计费方式一致）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "产品ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "costId", value = "计费方式ID", required = true, dataType = "Long", paramType = "query")
    })
    public Object listProductForModifyOrder(@RequestParam Long orderId, @RequestParam Integer productId, @RequestParam(required = false) Integer costId) {
        return productService.listProductForModifyOrder(orderId, productId, costId);
    }

    /**
     * 查询产品（分页）
     *
     * @param name             商品名称/商品编号
     * @param categoryId       产品后台分类ID
     * @param companyId        产品公司ID
     * @param startTime        开始时间
     * @param endTime          结束时间
     * @param onShelfStartTime 上架时间开始
     * @param onShelfEndTime   上架时间结束
     * @param pageNum          页码
     * @param pageSize         每页大小
     */
    @GetMapping(value = "/product/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "商品名称/商品编号", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "产品后台分类ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "产品状态：传2则是已上架  不传则为全部", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "hot", value = "是否热销：1热销  0不热销", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "companyId", value = "产品公司ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "need", value = "是否需要展示会员价格", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "展示端", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
            @ApiImplicitParam(name = "onShelfStartTime", value = "上架时间开始", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
            @ApiImplicitParam(name = "onShelfEndTime", value = "上架时间结束", dataType = "Date", paramType = "query", format = "yyyy-MM-dd"),
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> productPage(@RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "statusList", required = false) List<Integer> statusList,
                                          @RequestParam(value = "hot", required = false) Integer hot,
                                          @RequestParam(value = "companyId", required = false) Integer companyId,
                                          @RequestParam(value = "need", required = false) Integer need,
                                          @RequestParam(value = "terminal", required = false) Integer terminal,
                                          @RequestParam(value = "startTime", required = false) Date startTime,
                                          @RequestParam(value = "endTime", required = false) Date endTime,
                                          @RequestParam(value = "onShelfStartTime", required = false) Date onShelfStartTime,
                                          @RequestParam(value = "onShelfEndTime", required = false) Date onShelfEndTime,
                                          @PathVariable("pageNum") Integer pageNum,
                                          @PathVariable("pageSize") Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setName(name);
        query.setCategoryId(categoryId);
        query.setHot(hot);
        query.setCompanyId(companyId);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setOnShelfStartTime(onShelfStartTime);
        query.setOnShelfEndTime(onShelfEndTime);
        query.setStatus(statusList);
        query.setNeed(need);
        query.setTerminal(terminal);
        return productService.page(pageNum, pageSize, query);
    }


    /**
     * weChat查询产品
     *
     * @param frontCategoryId 产品前台一级 类目ID
     * @param pageNum         页码
     * @param pageSize        每页大小
     */
    @GetMapping(value = "/product/weChat/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询前台产品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "frontCategoryId", value = "前台分类的ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> listProductForClient(@RequestParam(required = false) Integer frontCategoryId,
                                                   @PathVariable Integer pageNum,
                                                   @PathVariable Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setFrontCategoryId(frontCategoryId);
        query.setTerminal(1);
        return productService.listProductForClient(pageNum, pageSize, query);
    }


    /**
     * 经销商APP客户端查询产品
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
            @ApiImplicitParam(name = "frontCategoryId", value = "前台分类的ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "第几页", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path")
    })
    public PageVO<ProductDTO> listProductForClient(@RequestParam String supplyCode,
                                                   @RequestParam(required = false) Integer frontCategoryId,
                                                   @PathVariable Integer pageNum,
                                                   @PathVariable Integer pageSize) {
        //产品供应类型校验：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
        boolean check;
        List<DictionaryDTO> dicList = productService.listProductSupplyCode(null);
        if (CollectionUtil.isNotEmpty(dicList)) {
            check = dicList.stream().anyMatch(dicDto -> dicDto.getCode().equalsIgnoreCase(supplyCode));
            if (!check) {
                throw new BadRequestException("产品供应类型错误。");
            }
            ProductQuery query = new ProductQuery();
            query.setFrontCategoryId(frontCategoryId);
            query.setSupplyCode(supplyCode);
            query.setTerminal(3);
            return productService.listProductForClient(pageNum, pageSize, query);
        }
        throw new YimaoException("未找到相关产品");
    }

    /**
     * APP端获取当前登录用户有权供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping(value = "/product/app/supply")
    @ApiOperation(value = "APP端获取当前登录用户有权供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    public List<DictionaryDTO> listProductSupplyCode(@RequestParam(value = "userName", required = false) String userName) {
        //UserDTO currentUser = userCache.getCurrentUser();
        //DistributorDTO distributor = userCache.getCurrentDistributor();
        return productService.listProductSupplyCode(userName);
    }

    /**
     * 发布产品时获取所有产品供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping(value = "/product/branch/sys")
    @ApiOperation(value = "发布产品时获取所有产品供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    @ApiImplicitParams({})
    public ResponseEntity<Object> listProductBranchSys() {
        // 直接获取所有供应类型

        List<DictionaryDTO> dictionaryList = productService.getAllProductSupplyCode();
        if (CollectionUtil.isEmpty(dictionaryList)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dictionaryList);
    }
    /**
     * 产品统计
     */
    @GetMapping(value = "/product/statistic")
    @ApiOperation(value = "查询产品统计数据")
    public ResponseEntity<Object> findProductStatistics() {
        ProductStatusStatisticVO statistics = productService.findProductStatistics();
        if (statistics == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取产品可购买人群列表
     */
    @GetMapping(value = "/product/buy/permissions")
    @ApiOperation(value = "获取产品可购买人群列表")
    public List<ProductBuyPermissionVO> getProductBuyPermissions() {
        List<ProductBuyPermissionVO> permissions = new ArrayList<>();
        //1-代理商组
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.A_1.name, ProductBuyPermission.A_1.code, ProductBuyPermission.A_1.group, 1));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.A_2.name, ProductBuyPermission.A_2.code, ProductBuyPermission.A_2.group, 2));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.A_3.name, ProductBuyPermission.A_3.code, ProductBuyPermission.A_3.group, 3));
        //2-经销商组
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.D_50.name, ProductBuyPermission.D_50.code, ProductBuyPermission.D_50.group, 1));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.D_350.name, ProductBuyPermission.D_350.code, ProductBuyPermission.D_350.group, 2));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.D_650.name, ProductBuyPermission.D_650.code, ProductBuyPermission.D_650.group, 3));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.D_950.name, ProductBuyPermission.D_950.code, ProductBuyPermission.D_950.group, 4));
        //3-服务站站长组
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.M_1.name, ProductBuyPermission.M_1.code, ProductBuyPermission.M_1.group, 1));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.M_2.name, ProductBuyPermission.M_2.code, ProductBuyPermission.M_2.group, 2));
        //4-用户组
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.U_7.name, ProductBuyPermission.U_7.code, ProductBuyPermission.U_7.group, 1));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.U_3.name, ProductBuyPermission.U_3.code, ProductBuyPermission.U_3.group, 2));
        permissions.add(new ProductBuyPermissionVO(ProductBuyPermission.U_4.name, ProductBuyPermission.U_4.code, ProductBuyPermission.U_4.group, 3));
        return permissions;
    }

    @GetMapping(value = "/product/MYFCard")
    @ApiOperation(value = "查询M和Y卡的电子卡券产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "分类名称 MYF卡", dataType = "String", paramType = "query")
    })
    public Object findMYCardProductList(@RequestParam("categoryName") String categoryName) {
        List<ProductDTO> result = productService.findMYCardProductList(categoryName);
        if (CollectionUtil.isNotEmpty(result)) {
            return ResponseEntity.ok(result);
        }
        throw new NotFoundException("没有找到产品数据");
    }

    @GetMapping(value = "/product/FCard")
    @ApiOperation(value = "查询F卡的电子卡券产品")
    public Object findFCardProductList() {
        List<ProductDTO> result = productService.findFCardProductList();
        if (CollectionUtil.isNotEmpty(result)) {
            return ResponseEntity.ok(result);
        }
        throw new NotFoundException("没有找到产品数据");
    }

    /**
     * 描述：根据产品分类名称获取产品ID集合
     *
     * @param categoryName  产品分类名称
     * @param categoryLevel 几级产品分类
     */
    @GetMapping(value = "/product/ids/category_name")
    @ApiOperation(value = "根据产品分类名称获取产品ID集合")
    public ResponseEntity<Object> listProductIdsByCategoryName(@RequestParam(value = "categoryName") String categoryName,
                                                               @RequestParam(value = "categoryLevel") Integer categoryLevel) {
        List<Integer> idList = productService.listProductIdsByCategoryName(categoryName, categoryLevel);
        if (CollectionUtil.isEmpty(idList)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(idList);
    }

    @GetMapping(value = "/product/MCard")
    @ApiOperation(value = "查询M和Y卡的电子卡券产品")
    public ResponseEntity findMCardProductList() {
        List<ProductDTO> result = productService.findMCardProductList();
        if (CollectionUtil.isNotEmpty(result)) {
            ProductDTO productDTO = result.get(0);
            if (productDTO != null) {
                //虚拟产品获取虚拟产品配置信息
                if (productDTO.getMode() == ProductModeEnum.VIRTUAL.value) {
                    VirtualProductConfig virtualProductConfig = productService.getVirtualProductConfigByProductId(productDTO.getId());
                    if (virtualProductConfig != null) {
                        VirtualProductConfigDTO virtualDTO = new VirtualProductConfigDTO();
                        virtualProductConfig.convert(virtualDTO);
                        productDTO.setVirtualProductConfig(virtualDTO);
                        return ResponseEntity.ok(result.get(0));
                    } else {
                        //如果产品时虚拟产品，但是没找到虚拟配置，则报错
                        throw new NotFoundException("未找到相关虚拟产品配置信息");
                    }
                }
            }
        }
        throw new NotFoundException("没有找到产品数据");
    }

    /**
     * 获取产品列表
     */
    @GetMapping(value = "/product")
    @ApiOperation(value = "查询产品列表")
    public Object findProductList() {
        List<ProductDTO> result = productService.findProductList();
        if (CollectionUtil.isNotEmpty(result)) {
            return ResponseEntity.ok(result);
        }
        throw new NotFoundException("没有找到产品数据");
    }


    /**
     * 根据前端分类id展示商品列表
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
        List<ProductDTO> list = productService.getProductByFrontId(id);
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/product/getIdByCategoryId")
    public Object getProductIdByCategoryId(@RequestParam Integer categoryId) {
        return productMapper.selectIdByCategoryId(categoryId);
    }

    @GetMapping(value = "/product/getIdByCategoryName")
    public Object getProductIdByCategoryName(@RequestParam String categoryName) {
        return productMapper.selectIdByCategoryName(categoryName);
    }

    @GetMapping(value = "/product/waters")
    public List<ProductDTO> getWaterProduct() {
        return productMapper.getWaterProduct();
    }


    /**
     * 数据迁移用（业务不准调用）
     */
    @GetMapping(value = "/product/getByOldId")
    public ProductDTO getProductByOldId(@RequestParam("oldId") String oldId) {
        Integer id = productMapper.selectIdByOldId(oldId);
        return productService.getFullProductById(id,null);
    }
    
    
    /**
     * 添加活动获取未上架的产品列表
     */
    @GetMapping(value = "/product/activitys")
    @ApiOperation(value = "添加活动获取未上架的产品列表")
    public List<ProductDTO> findActivityProduct(@RequestParam("statusList") List<Integer> statusList) {
         return productMapper.findNotListedProduct(statusList);
        
    }
    
    /**
     *折机经销商关联关系因经销商转让转移修改
     */
    @PutMapping(value = "/product/changeProductDistributor")
    @ApiOperation(value = "折机经销商关联关系因经销商转让转移修改")
    public void changeProductDistributor(@RequestParam("origDistributorId")Integer origDistributorId, @RequestParam("newDistributorId")Integer newDistributorId){
          productService.changeProductDistributor(origDistributorId,newDistributorId);
        
    }

    /**
     * 发布产品时获取所有产品供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；
     */
    @GetMapping(value = "/product/supplyCode")
    @ApiOperation(value = "产品供应的产品类型：PJXCP-经销产品；PZZZG-站长专供；PTPSJ-特批水机；PTGCP-特供产品；")
    @ApiImplicitParam(name = "supplyCode", value = "产品供应的产品类型", dataType = "String", paramType = "query")
    public List<Integer> getProductIdsBySupplyCode(@RequestParam("supplyCode") String supplyCode) {
        // 直接获取所有供应类型
        return productService.getProductBySupplyCode(supplyCode);
    }
    
    /***
     * 获取已上架水机产品的所有型号
     * @return
     */
    @GetMapping(value = "/product/online/category")
    public List<ProductDTO> getOnlinePoruductCategory(@RequestParam(value = "price",required = false)BigDecimal price){
		return productMapper.getOnlineProductThreeCategory(price);
    	
    }
    
}
