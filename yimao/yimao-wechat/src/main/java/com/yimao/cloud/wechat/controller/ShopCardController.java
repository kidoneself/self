package com.yimao.cloud.wechat.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import com.yimao.cloud.wechat.feign.OrderFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车
 *
 * @author hhf
 * @date 2019/4/3
 */
@RestController
@Api(tags = "ShopCartController")
public class ShopCardController {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserCache userCache;

    /**
     * 添加购物车
     *
     * @param dto 购物车信息
     */
    @PostMapping(value = "/cart")
    @ApiOperation(value = "添加购物车信息")
    @ApiImplicitParam(name = "dto", value = "购物车信息", required = true, dataType = "ShopCartDTO", paramType = "body")
    public void save(@RequestBody ShopCartDTO dto) {
        orderFeign.savaShopCart(dto);
    }

    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车ID
     */
    @DeleteMapping(value = "/cart")
    @ApiOperation(value = "删除购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "所选购物车", required = true, dataType = "Long", paramType = "query", allowMultiple = true)
    })
    public void delete(@RequestParam List<Integer> cartIds) {
        orderFeign.deleteShopCart(cartIds);
    }

    /**
     * 修改购物车计费方式或数量
     *
     * @param dto 购物车信息
     */
    @PatchMapping(value = "/cart")
    @ApiOperation(value = "修改购物车计费方式或数量")
    @ApiImplicitParam(name = "dto", value = "购物车信息", required = true, dataType = "ShopCartDTO", paramType = "body")
    public void update(@RequestBody ShopCartDTO dto) {
        orderFeign.updateShopCart(dto);
    }

    /**
     * 查询购物车列表
     *
     * @param type              购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
     * @param terminal          1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    @GetMapping(value = "/cart")
    @ApiOperation(value = "查询购物车列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminal", value = "1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productCategoryId", value = "产品前台一级类目ID", dataType = "Long", paramType = "query")
    })
    public JSONObject listShopCart(@RequestParam(required = false) Integer productCategoryId) {
        return orderFeign.listShopCart(Terminal.WECHAT.value, productCategoryId);
    }

    /**
     * 查询购物车产品数量
     *
     * @param type     购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；
     * @param terminal 1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
     */
    @GetMapping(value = "/cart/count")
    @ApiOperation(value = "查询购物车产品数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "购物车类型：1-普通购物车；2-经销购物车；3-站长购物车；4-特批水机购物车；5-特供产品购物车；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "terminal", value = "1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；", required = true, dataType = "Long", paramType = "query")
    })
    public Integer sumCount(@RequestParam(value = "type", defaultValue = "1") Integer type, @RequestParam(value = "terminal") Integer terminal) {
        try {
            userCache.getUserId();
        } catch (Exception e) {
            //如果没有登录，默认为0
            return 0;
        }
        return orderFeign.sumCount(type, terminal);
    }

    /**
     * 填写订单--发票须知
     */
    @ApiOperation(value = "填写订单--发票须知")
    @GetMapping(value = "/order/invoiceNotes")
    public JSONObject orderInvoiceNotes() {
        return orderFeign.orderInvoiceNotes();
    }
}
