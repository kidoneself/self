package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.ShopCart;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhilin.he
 * @description 购物车数据调用层
 * @date 2018/12/25 11:33
 **/
public interface ShopCartMapper extends Mapper<ShopCart> {

    /**
     * 删除购物车
     *
     * @param ids    购物车ID
     * @param userId 用户ID
     */
    Integer deleteShopCart(@Param(value = "ids") List<Integer> ids, @Param(value = "userId") Integer userId);

    /**
     * 查询购物车产品累计数量
     *
     * @param record 查询条件
     */
    Integer sumCount(ShopCart record);
}
