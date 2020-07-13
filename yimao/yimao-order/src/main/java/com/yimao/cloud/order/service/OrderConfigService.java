package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.OrderConfig;
import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;

public interface OrderConfigService {

    void addOrderConfig(OrderConfig orderConfig);

    void updateOrderConfig(OrderConfig orderConfig);

    OrderConfigDTO getOrderConfig();
}
