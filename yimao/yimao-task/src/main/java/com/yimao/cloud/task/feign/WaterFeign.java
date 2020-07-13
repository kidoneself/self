package com.yimao.cloud.task.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import com.yimao.feign.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 描述：水机微服务的接口列表
 *
 * @author liu yi
 * @date 2019/5/5.
 */
@FeignClient(name = Constant.MICROSERVICE_WATER, configuration = MultipartSupportConfig.class)
public interface WaterFeign {

    /**
     * 根据水机设备SN获取设备信息
     *
     * @param sn 设备sn编码
     */
    @GetMapping(value = "/waterdevice")
    WaterDeviceDTO getBySnCode(@RequestParam("sn") String sn);

    @PatchMapping(value = "/waterdevice", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateWaterDevice(@RequestBody WaterDeviceDTO device);

}
