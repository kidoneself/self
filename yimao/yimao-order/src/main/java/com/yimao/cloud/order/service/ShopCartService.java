package com.yimao.cloud.order.service;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.order.po.ShopCart;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;

import java.util.List;

/**
 * @author zhilin.he
 * @description 购物车服务层
 * @date 2018/12/25 11:29
 */
public interface ShopCartService {

    /**
     * 添加购物车
     *
     * @param shopCart 购物车信息
     */
    void save(ShopCart shopCart);

    /**
     * 修改购物车
     *
     * @param shopCart 购物车信息
     */
    void update(ShopCart shopCart);

    /**
     * 删除购物车
     *
     * @param cartIds 所选购物车ID
     */
    void delete(List<Integer> cartIds);

    /**
     * 查询购物车列表
     *
     * @param userId            用户id
     * @param terminal          1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     * @param productCategoryId 产品前台一级类目ID
     */
    JSONObject listShopCart(Integer userId, Integer terminal, Integer productCategoryId);

    /**
     * 查询购物车产品数量
     *
     * @param userId   用户id
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    Integer sumCount(Integer userId, Integer terminal);

    /**
     * 查询加购的产品前台一级类目列表
     *
     * @param userId   用户id
     * @param terminal 1-用户终端APP；2-微信公众号；3-翼猫APP；4-小猫店小程序；
     */
    JSONObject listShopCartProductCategory(Integer userId, Integer terminal);

    /**
     * 购物车商品结算
     *
     * @param orderInfo 前端传递的购物车订单信息
     */
    void orderSettlement(OrderDTO orderInfo);

    /**
     * 填写订单--发票须知
     */
    JSONObject orderInvoiceNotes();

    /**
     * 站长权限校验
     */
    void stationMasterPermission(UserDTO user);
}
