package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.BaseOrder;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;

public interface OrderCheckService {

    /**
     * 描述：活动商品下单时相关校验
     *
     * @param product
     * @param baseOrder
     * @param user
     */
    void checkProductActivity(ProductDTO product, BaseOrder baseOrder, UserDTO user);
}
