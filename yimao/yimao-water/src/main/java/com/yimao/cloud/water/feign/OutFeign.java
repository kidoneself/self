package com.yimao.cloud.water.feign;

import com.yimao.cloud.base.constant.Constant;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 描述：OUT微服务远程调用类。
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:44
 * @Version 1.0
 */
@FeignClient(name = Constant.MICROSERVICE_OUT)
public interface OutFeign {

    // /**
    //  * 查询所有的服务站数据
    //  *
    //  * @return
    //  */
    // @RequestMapping(value = "/service/devices", method = RequestMethod.GET)
    // List<ServiceDeviceDTO> serviceDevicesList();

}
