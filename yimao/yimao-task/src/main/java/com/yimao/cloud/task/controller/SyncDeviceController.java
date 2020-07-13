// package com.yimao.cloud.task.controller;
//
// import com.yimao.cloud.base.constant.RabbitConstant;
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.out.DeviceDTO;
// import com.yimao.cloud.task.feign.OutFeign;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.amqp.core.AmqpTemplate;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.annotation.Resource;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
//
// @RestController
// @Api(tags = "SyncDeviceController")
// @Slf4j
// public class SyncDeviceController {
//
//     @Resource
//     private OutFeign outFeign;
//     @Resource
//     private AmqpTemplate rabbitTemplate;
//
//     // private static ExecutorService executor = Executors.newFixedThreadPool(3);
//
//     @GetMapping("/sync/device")
//     @ApiOperation(value = "同步设备数据")
//     public void syncDevice(@RequestParam(required = false) String time, @RequestParam(defaultValue = "50") Integer pageSize) {
//         long l1 = System.currentTimeMillis();
//         //返回结果
//         Set<String> result = new HashSet<>();
//
//         Date startTime = outFeign.getDeviceMinTime();
//         if (startTime == null) {
//             startTime = DateUtil.transferStringToDate("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//         }
//         if (StringUtil.isNotBlank(time)) {
//             startTime = DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss.SSS");
//         }
//
//         log.info("startTime=" + DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//
//         long total = outFeign.getDeviceTotalCount(startTime);
//
//         log.info("total=" + total);
//
//         List<DeviceDTO> devices;
//         int pages = 0;
//         while (startTime.getTime() < new Date().getTime() && result.size() < total) {
//             StringBuilder sb = new StringBuilder();
//             sb.append("第").append(++pages).append("次查询，当前结果集大小=").append(result.size());
//             long l11 = System.currentTimeMillis();
//             sb.append("，startTime=").append(DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//             devices = outFeign.getDevicePage(pageSize, startTime, new Date());
//             if (CollectionUtil.isEmpty(devices)) {
//                 break;
//             }
//             startTime = devices.get(devices.size() - 1).getActiveTime();
//             long l111 = System.currentTimeMillis();
//             for (DeviceDTO dto : devices) {
//                 result.add(dto.getId());
//             }
//             long l222 = System.currentTimeMillis();
//             log.info("循环耗时===" + (l222 - l111));
//             long l22 = System.currentTimeMillis();
//             sb.append("单次查询耗时：").append(l22 - l11).append("毫秒。查询到的结果数=").append(devices.size());
//             if (l22 - l11 >= 15000) {
//                 log.info("========单次查询耗时过长：" + (l22 - l11) + "毫秒。");
//             }
//             log.info(sb.toString());
//             rabbitTemplate.convertAndSend(RabbitConstant.SYNC_DEVICE, devices);
//         }
//         log.info("pages=" + pages);
//         log.info("result.size()=" + result.size());
//
//         long l2 = System.currentTimeMillis();
//         log.info("总查询耗时：" + (l2 - l1) + "毫秒。");
//     }
//
//     // @GetMapping("/sync/device2")
//     // @ApiOperation(value = "同步设备数据")
//     // public void syncDevice2(@RequestParam(required = false) String time, @RequestParam(defaultValue = "100") Integer pageSize) {
//     //     long total = 0L;
//     //     long l1 = System.currentTimeMillis();
//     //
//     //     Date startDate = outFeign.getDeviceMinTime();
//     //     if (startDate == null) {
//     //         startDate = DateUtil.transferStringToDate("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//     //     }
//     //     if (StringUtil.isNotBlank(time)) {
//     //         startDate = DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss.SSS");
//     //     }
//     //
//     //     Map<Date, Date> dayMap = new HashMap<>();
//     //     this.buildDayMap(dayMap, startDate);
//     //     // List<DeviceTask> taskList = new ArrayList<>();
//     //     Set<Date> keySet = dayMap.keySet();
//     //     for (Date startTime : keySet) {
//     //         Date endTime = dayMap.get(startTime);
//     //         DeviceTask task = new DeviceTask(pageSize, startTime, endTime);
//     //         executor.submit(task);
//     //         // taskList.add(task);
//     //     }
//     //     // try {
//     //     //     List<Future<Long>> futures = executor.invokeAll(taskList);
//     //     // } catch (InterruptedException e) {
//     //     //     log.info("执行遇到错误1。");
//     //     //     e.printStackTrace();
//     //     // }
//     // }
//     //
//     // private void buildDayMap(Map<Date, Date> dayMap, Date startDate) {
//     //     Date dayStartTime = DateUtil.getDayStartTime(startDate);
//     //     Date dayEndTime = DateUtil.dayAfter(dayStartTime, 1);
//     //     dayMap.put(dayStartTime, dayEndTime);
//     //     int days = DateUtil.betweenDays(startDate, new Date());
//     //     if (days > 0) {
//     //         for (int i = 0; i < days; i++) {
//     //             dayStartTime = DateUtil.dayAfter(dayStartTime, 1);
//     //             dayEndTime = DateUtil.dayAfter(dayEndTime, 1);
//     //             dayMap.put(dayStartTime, dayEndTime);
//     //         }
//     //     }
//     // }
//     //
//     // class DeviceTask implements Runnable {
//     //     private Integer pageSize;
//     //     private Date startTime;
//     //     private Date endTime;
//     //
//     //     DeviceTask(int pageSize, Date startTime, Date endTime) {
//     //         this.pageSize = pageSize;
//     //         this.startTime = startTime;
//     //         this.endTime = endTime;
//     //     }
//     //
//     //     @Override
//     //     public void run() {
//     //         String dateStr = DateUtil.transferDateToString(startTime, "yyyy-MM-dd");
//     //         long l1 = System.currentTimeMillis();
//     //         long total = outFeign.getDeviceTotalCount2(startTime, endTime);
//     //         List<DeviceDTO> devices;
//     //         Set<String> result = new HashSet<>();
//     //         int pages = 0;
//     //         while (startTime.getTime() < endTime.getTime() && result.size() < total) {
//     //             long l11 = System.currentTimeMillis();
//     //             StringBuilder sb = new StringBuilder();
//     //             sb.append("线程-").append(Thread.currentThread().getName()).append("，第").append(++pages).append("次查询。");
//     //             devices = outFeign.getDevicePage(pageSize, startTime, endTime);
//     //             if (CollectionUtil.isNotEmpty(devices)) {
//     //                 for (DeviceDTO dto : devices) {
//     //                     result.add(dto.getId());
//     //                 }
//     //                 startTime = devices.get(devices.size() - 1).getActiveTime();
//     //                 long l22 = System.currentTimeMillis();
//     //                 sb.append("单次查询耗时：").append(l22 - l11);
//     //                 log.info(sb.toString());
//     //                 rabbitTemplate.convertAndSend(RabbitConstant.SYNC_DEVICE, devices);
//     //             }
//     //         }
//     //         long l2 = System.currentTimeMillis();
//     //         log.info("查询" + dateStr + "这天的设备数据，执行耗时：" + (l2 - l1) + "毫秒。共查询出" + result.size() + "条数据。");
//     //     }
//     // }
//
// }
