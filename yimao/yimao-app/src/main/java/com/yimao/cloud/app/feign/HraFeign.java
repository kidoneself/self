package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.hra.HraTicketDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author liuhao
 * @description Hra服务调用
 * @date 2019/09/25 15:11
 **/
@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {


    /**
     * 根据订单号，获取体检卡列表
     *
     * @param orderId 订单号
     * @return obj
     */
    @GetMapping(value = "/order/card/detail")
    List<HraTicketDTO> cardDetailByOrderId(@RequestParam("orderId") Long orderId);

}
