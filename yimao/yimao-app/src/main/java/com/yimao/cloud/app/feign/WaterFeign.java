package com.yimao.cloud.app.feign;

import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhilin.he
 * @description 订单服务调用
 * @date 2019/1/9 14:16
 **/
@FeignClient(name = Constant.MICROSERVICE_WATER)
public interface WaterFeign {


    /**
     * 翼猫APP-我的-水机续费-查询列表（栏目：10-新安装；20-待续费；30-已续费；）
     */
    @GetMapping(value = "/my/waterdevice/{pageNum}/{pageSize}")
    Object listDeviceWithRenewInfo(@RequestParam(value = "column") Integer column,
                                   @RequestParam(value = "distributorId") Integer distributorId,
                                   @PathVariable(value = "pageNum") Integer pageNum,
                                   @PathVariable(value = "pageSize") Integer pageSize);

    /***
     * 公司水机设备续费率统计
     * @param query
     * @return
     */
    @GetMapping(value = "/company/waterdevice/renew/statsInfo")
    List<SalesStatsDTO> getDeviceRenewPropList(@RequestParam(value = "ids") List<Integer> ids);

}
