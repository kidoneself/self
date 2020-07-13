package com.yimao.cloud.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;

@FeignClient(name = Constant.MICROSERVICE_WATER)
public interface WaterFeign {

    /**
     * 根据经销商id查询经销商所售水机数量
     *
     * @param distributorId
     * @return
     */
    @GetMapping(value = "/waterdevice/count/{distributorId}")
    Integer getWaterDeviceCountByDistributorId(@PathVariable("distributorId") Integer distributorId);
    
    /****
     * 更新设备上的安装工手机号
     * @param update
     */
    @PostMapping(value = "/waterdevice/engineerphone",consumes = MediaType.APPLICATION_JSON_VALUE)
	void updateDeviceForEngineerPhone(@RequestBody WaterDeviceDTO update);

}
