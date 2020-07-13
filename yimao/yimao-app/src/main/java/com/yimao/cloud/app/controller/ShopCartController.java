package com.yimao.cloud.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.app.feign.OrderFeign;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车前端控制器
 *
 * @author zhilin.he
 * @date 2019/1/9 15:56
 **/
@RestController
@Api(tags = "ShopCartController")
public class ShopCartController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 添加购物车
     *
     * @param dto 购物车信息
     */
    @PostMapping(value = "/cart")
    @ApiOperation(value = "添加购物车信息")
    @ApiImplicitParam(name = "dto", value = "购物车信息", required = true, dataType = "ShopCartDTO", paramType = "body")
    public void save(@RequestBody ShopCartDTO dto) {
        orderFeign.saveShopCart(dto);
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
     * @param productCategoryId 产品前台一级类目ID
     */
    @GetMapping(value = "/cart")
    @ApiOperation(value = "查询购物车列表")
    @ApiImplicitParam(name = "productCategoryId", value = "产品前台一级类目ID", dataType = "Long", paramType = "query")
    public JSONObject listShopCart(@RequestParam(required = false) Integer productCategoryId) {
        return orderFeign.listShopCart(Terminal.YIMAO_APP.value, productCategoryId);
    }

    /**
     * 查询购物车产品数量
     */
    @GetMapping(value = "/cart/count")
    @ApiOperation(value = "查询购物车产品数量")
    public JSONObject sumCount() {
        JSONObject object = new JSONObject();
        object.put("count", orderFeign.sumCount(Terminal.YIMAO_APP.value));
        return object;
    }

    /**
     * 查询加购的产品前台一级类目列表
     */
    @GetMapping(value = "/cart/productCategory")
    @ApiOperation(value = "查询加购的产品前台一级类目列表")
    public JSONObject listShopCartProductCategory() {
        return orderFeign.listShopCartProductCategory(Terminal.YIMAO_APP.value);
    }

    /**
     * 1、翼猫APP购物车结算按钮
     * 2、翼猫APP活动抢购立即抢购按钮
     *
     * @param dto 前端传递的购物车订单信息
     */
    @PostMapping(value = "/order/settlement")
    @ApiOperation(value = "购物车商品结算")
    @ApiImplicitParam(name = "dto", value = "前端传递的购物车订单信息", required = true, dataType = "OrderDTO", paramType = "body")
    public JSONObject orderSettlement(@RequestBody OrderDTO dto) {
        dto.setTerminal(Terminal.YIMAO_APP.value);
        return orderFeign.orderSettlement(dto);
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
