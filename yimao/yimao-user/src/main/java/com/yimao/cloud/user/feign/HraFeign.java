package com.yimao.cloud.user.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = Constant.MICROSERVICE_HRA)
public interface HraFeign {

    @RequestMapping(value = "/discount/card/grant", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    void grantDiscountCard(@RequestBody UserDTO userDTO);
}
