package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;

import java.util.Map;

/**
 * 订单概况
 *
 * @author hhf
 * @date 2019/3/26
 */
public interface OrderOverviewService {
    /**
     *  待办事项统计
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    Map<String, Long> orderOverview();

    /**
     * 经营概况
     *
     * @return com.yimao.cloud.pojo.dto.system.BusinessProfileDTO
     * @author hhf
     * @date 2019/3/26
     */
    BusinessProfileDTO orderOverviewBusiness();
}
