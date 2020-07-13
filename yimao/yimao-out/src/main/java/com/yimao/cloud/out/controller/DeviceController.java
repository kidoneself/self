// package com.yimao.cloud.out.controller;
//
// import com.yimao.cloud.out.service.DeviceApi;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.annotation.Resource;
//
// /**
//  * 查询云平台mongo数据库设备相关信息。
//  *
//  * @author Zhang Bo
//  * @date 2017/12/15.
//  */
// @RestController
// @Slf4j
// @Api(tags = "DeviceController")
// public class DeviceController {
//
//     @Resource
//     private DeviceApi deviceApi;
//
//     @GetMapping(value = "/service/devices")
//     @ApiOperation(value = "查询全部服务站水机数据", notes = "查询服务站水机数据")
//     public Object serviceDevicesList() {
//         return ResponseEntity.ok(deviceApi.serviceDevicesList(deviceApi.getFieldsList()));
//     }
//
// }
