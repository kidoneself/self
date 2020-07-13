package com.yimao.cloud.product.controller;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.framework.cache.RedisCache;
import com.yimao.cloud.pojo.dto.product.ProductActivityDTO;
import com.yimao.cloud.pojo.query.product.ProductActivityQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.product.ProductActivityVO;
import com.yimao.cloud.product.mapper.ProductActivityMapper;
import com.yimao.cloud.product.po.ProductActivity;
import com.yimao.cloud.product.service.ProductActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 产品活动
 *
 * @author zhangbaobao
 * @date 2020/3/12
 */
@RestController
@Api(tags = "ProductActivityController")
public class ProductActivityController {

    @Resource
    private ProductActivityService productActivityService;

    @Resource
    private ProductActivityMapper productActivityMapper;

    @Resource
    private RedisCache redisCache;

    /**
     * 根据商品活动ID查询单个商品活动
     */
    @GetMapping(value = "/product/activity/{id}")
    @ApiOperation(value = "根据商品活动ID查询单个商品活动")
    public ProductActivityDTO getProductActivityById(@PathVariable(value = "id") Integer id) {
        ProductActivity productActivity = productActivityMapper.selectByPrimaryKey(id);
        if (productActivity == null) {
            return null;
        }
        ProductActivityDTO dto = new ProductActivityDTO();
        productActivity.convert(dto);
        return dto;
    }

    /**
     * 更新商品活动剩余库存（减）
     */
    @GetMapping(value = "/product/activity/{id}/subtractStock")
    @ApiOperation(value = "更新商品活动剩余库存")
    public Integer subtractProductActivityStock(@PathVariable(value = "id") Integer id, @RequestParam(value = "count") Integer count) {
        int num = productActivityMapper.subtractStock(id, count);
        if (num <= 0) {
            return num;
        }
        ProductActivity productActivity = productActivityMapper.selectByPrimaryKey(id);
        if (productActivity != null) {
            ProductActivityDTO dto = new ProductActivityDTO();
            productActivity.convert(dto);
            redisCache.set(Constant.PRODUCT_ACTIVITY_CACHE + id, dto, 3600L);
        }
        return num;
    }

    /**
     * 更新商品活动剩余库存（加）
     */
    @GetMapping(value = "/product/activity/{id}/addStock")
    @ApiOperation(value = "更新商品活动剩余库存")
    public Integer addProductActivityStock(@PathVariable(value = "id") Integer id, @RequestParam(value = "count") Integer count) {
        int num = productActivityMapper.addStock(id, count);
        if (num <= 0) {
            return num;
        }
        ProductActivity productActivity = productActivityMapper.selectByPrimaryKey(id);
        if (productActivity != null) {
            ProductActivityDTO dto = new ProductActivityDTO();
            productActivity.convert(dto);
            redisCache.set(Constant.PRODUCT_ACTIVITY_CACHE + id, dto, 3600L);
        }
        return num;
    }

    /****
     * 查询已开启活动并且产品已上架的活动产品列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/{pageNum}/{pageSize}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询商品列表")
    public PageVO<ProductActivityVO> pageProductActivity(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                         @RequestBody ProductActivityQuery query) {
        return productActivityService.pageProductActivity(pageNum, pageSize, query);
    }

    /****
     * 查询单个活动商品信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/detail", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询商品列表")
    public ProductActivityVO productActivityInfo(@RequestBody ProductActivityQuery query) {
        return productActivityService.getProductActivity(query);
    }

    /*********************以下服务是业务系统调用***********************/

    /***
     *业务系统- 更新产品活动信息
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "update", value = "更新条件", dataType = "ProductActivity", paramType = "body")
    })
    public int updateProductActivity(@RequestBody ProductActivityDTO update) {
        return productActivityService.updateProductActivity(update);
    }


    /****
     * 业务系统-查询产品列表
     * @param pageNum
     * @param pageSize
     * @param query
     * @return
     */
    @PostMapping(value = "/product/activity/list/{pageNum}/{pageSize}")
    @ApiOperation(value = "查询商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "query", value = "查询条件", dataType = "ProductActivityDTO", paramType = "body")
    })
    public PageVO<ProductActivityVO> productActivityList(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                                                         @RequestBody ProductActivityDTO query) {
        return productActivityService.productActivityList(pageNum, pageSize, query);
    }

    /***
     * 删除活动
     * @param id
     */
    @DeleteMapping(value = "/product/activity/{id}")
    public void deleteActivity(@PathVariable("id") Integer id) {
        productActivityMapper.deleteByPrimaryKey(id);
    }

    /****
     * 激活活动
     * @param id
     */
    @GetMapping(value = "product/activity/open/{id}")
    void openProductActivity(@PathVariable("id") Integer id) {
        productActivityService.openProductActivity(id);
    }

}
