package com.yimao.cloud.openapi.feign;

import com.yimao.cloud.base.constant.Constant;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = Constant.MICROSERVICE_ORDER)
public interface OrderFeign {
}
