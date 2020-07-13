package com.yimao.cloud.hra.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {

    /**
     * 后去分销用户的分销数量
     *
     * @param userId 用户Id
     * @return int
     */
    @RequestMapping(value = "/sale/order/count/{userId}", method = RequestMethod.GET)
    Integer countSaleOrder(@PathVariable(value = "userId") Integer userId);

    /**
     * 获取订单信息
     *
     * @param id 订单ID
     * @return orderSubDto
     */
    @RequestMapping(value = {"/order/sub/{id}/basic"}, method = RequestMethod.GET)
    OrderSubDTO findBasicOrderInfoById(@PathVariable(value = "id") Long id);

    /**
     * 根据累计的水机、健康产品已完成的工单数，发放HRA优惠卡
     *
     * @param distributorId 经销商e家号
     * @param phone         手机号
     * @param userType      用户类型
     */
    @RequestMapping(value = "/order/countcompletedorderfromdate", method = RequestMethod.GET)
    int countCompletedOrderFromDate(@RequestParam(value = "distributorId", required = false) Integer distributorId,
                                    @RequestParam("phone") String phone,
                                    @RequestParam("userType") Integer userType);
}
