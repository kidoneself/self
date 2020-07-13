package com.yimao.cloud.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.Terminal;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.DialogException;
import com.yimao.cloud.base.msg.CommResult;
import com.yimao.cloud.order.po.ShopCart;
import com.yimao.cloud.order.service.ShopCartService;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.ShopCartDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
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
 * @author zhilin.he
 * @description 购物车控制层
 * @date 2018/12/25 11:24
 **/
@RestController
@Api(tags = "ShopCartController")
public class ShopCartController {

    @Resource
    private ShopCartService shopCartService;
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
        ShopCart shopCart = new ShopCart(dto);
        shopCart.setUserId(userCache.getUserId());
        shopCartService.save(shopCart);
    }

    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车
     */
    @DeleteMapping(value = "/cart")
    @ApiOperation(value = "删除购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "所选购物车ID", required = true, dataType = "Long", paramType = "query", allowMultiple = true),
    })
    public void delete(@RequestParam List<Integer> cartIds) {
        shopCartService.delete(cartIds);
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
        dto.setTerminal(Terminal.YIMAO_APP.value);
        ShopCart shopCart = new ShopCart(dto);
        if (shopCart.getId() == null) {
            throw new BadRequestException("修改失败。");
        }
        shopCartService.update(shopCart);
    }

    /**
     * 查询购物车列表
     *
     * @param terminal          1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    @GetMapping(value = "/cart")
    @ApiOperation(value = "购物车列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminal", value = "1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productCategoryId", value = "产品前台一级类目ID", dataType = "Long", paramType = "query")
    })
    public JSONObject listShopCart(@RequestParam Integer terminal, @RequestParam(required = false) Integer productCategoryId) {
        return shopCartService.listShopCart(userCache.getUserId(), terminal, productCategoryId);
    }

    /**
     * 查询购物车产品数量
     *
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @GetMapping(value = "/cart/count")
    @ApiOperation(value = "查询购物车产品数量")
    @ApiImplicitParam(name = "terminal", value = "1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；", required = true, dataType = "Long", paramType = "query")
    public Integer sumCount(@RequestParam(value = "terminal") Integer terminal) {
        return shopCartService.sumCount(userCache.getUserId(), terminal);
    }

    /**
     * 查询加购的产品前台一级类目列表
     *
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    @GetMapping(value = "/cart/productCategory")
    @ApiOperation(value = "查询加购的产品前台一级类目列表")
    @ApiImplicitParam(name = "terminal", value = "1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；", required = true, dataType = "Long", paramType = "query")
    public JSONObject listShopCartProductCategory(@RequestParam Integer terminal) {
        return shopCartService.listShopCartProductCategory(userCache.getUserId(), terminal);
    }

    /**
     * 购物车商品结算
     *
     * @param dto 前端传递的购物车订单信息
     */
    @PostMapping(value = "/order/settlement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object orderSettlement(@RequestBody OrderDTO dto) {
        try {
            shopCartService.orderSettlement(dto);
            return CommResult.ok();
        } catch (Exception e) {
            if (e instanceof DialogException) {
                //对话框消息提示
                return CommResult.dialogError(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 填写订单--发票须知
     */
    @ApiOperation(value = "填写订单--发票须知")
    @GetMapping(value = "/order/invoiceNotes")
    public JSONObject orderInvoiceNotes() {
        return shopCartService.orderInvoiceNotes();
    }
}
