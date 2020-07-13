package com.yimao.cloud.user.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.out.CustomerDTO;
import com.yimao.cloud.pojo.dto.out.DistributorDTO;
import com.yimao.cloud.pojo.dto.out.WaterUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * 描述：OUT微服务远程调用类。
 *
 * @Author Zhang Bo
 * @Date 2019/2/18 13:44
 * @Version 1.0
 */
@FeignClient(name = Constant.MICROSERVICE_OUT)
public interface OutFeign {

    //同步经销商数据到MYSQL

    @GetMapping(value = "/sync/distributor/min/time")
    Date getDistributorMinTime();

    @GetMapping(value = "/sync/distributor/total/count")
    long getDistributorTotalCount(@RequestParam("startTime") Date startTime);

    @GetMapping(value = "/sync/distributor/{pageSize}")
    List<DistributorDTO> getDistributorPage(@PathVariable("pageSize") Integer pageSize, @RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime);

    //同步安装工数据到MYSQL

    @GetMapping(value = "/sync/engineer/min/time")
    Date getEngineerMinTime();

    @GetMapping(value = "/sync/engineer/total/count")
    long getEngineerTotalCount(@RequestParam("startTime") Date startTime);

    @GetMapping(value = "/sync/engineer/{pageSize}")
    List<CustomerDTO> getEngineerPage(@PathVariable("pageSize") Integer pageSize, @RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime);


    //同步设备用户数据到MYSQL

    @GetMapping(value = "/sync/device/user/min/time")
    Date getDeviceUserMinTime();

    @GetMapping(value = "/sync/device/user/total/count")
    long getDeviceUserTotalCount(@RequestParam("startTime") Date startTime);

    @GetMapping(value = "/sync/device/user/{pageSize}")
    List<WaterUserDTO> getDeviceUserPage(@PathVariable("pageSize") Integer pageSize, @RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime);
}
