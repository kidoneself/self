package com.yimao.cloud.out.controller.api;

import com.yimao.cloud.out.feign.OrderFeign;
import com.yimao.cloud.out.feign.UserFeign;
import com.yimao.cloud.out.service.WorkOrderApi;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 描述：原云平台提供给安装工APP调用的接口
 *
 * @Author Zhang Bo
 * @Date 2019/3/13
 */
@RestController
@Slf4j
@Api(tags = "WorkOrderController")
public class WorkOrderController {

    @Resource
    private WorkOrderApi workOrderApi;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private UserFeign userFeign;





}
