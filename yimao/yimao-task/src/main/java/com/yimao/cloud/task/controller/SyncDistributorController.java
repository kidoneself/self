// package com.yimao.cloud.task.controller;
//
// import com.yimao.cloud.base.constant.RabbitConstant;
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.out.DistributorDTO;
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
// /**
//  * 描述：同步经销商信息。
//  *
//  * @Author Zhang Bo
//  * @Date 2019/5/16
//  */
// @RestController
// @Api(tags = "SyncDistributorController")
// @Slf4j
// public class SyncDistributorController {
//
//     @Resource
//     private OutFeign outFeign;
//     @Resource
//     private AmqpTemplate rabbitTemplate;
//
//     @GetMapping("/sync/distributor")
//     @ApiOperation(value = "同步经销商数据")
//     public void SyncDistributor(@RequestParam(required = false) String time, @RequestParam(defaultValue = "50") Integer pageSize) {
//         long l1 = System.currentTimeMillis();
//         //返回结果
//         Set<DistributorDTO> result = new HashSet<>();
//
//         Date startTime = outFeign.getDistributorMinTime();
//         if (startTime == null) {
//             startTime = DateUtil.transferStringToDate("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//         }
//         if (StringUtil.isNotBlank(time)) {
//             startTime = DateUtil.transferStringToDate(time, "yyyy-MM-dd HH:mm:ss.SSS");
//         }
//
//         log.info("startTime=" + DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//
//         long total = outFeign.getDistributorTotalCount(startTime);
//
//         List<DistributorDTO> list;
//         int pages = 0;
//         while (startTime.getTime() < new Date().getTime() && result.size() < total) {
//             StringBuilder sb = new StringBuilder();
//             sb.append("第").append(++pages).append("次查询，当前结果集大小=").append(result.size()).append("，total=").append(total);
//             long l11 = System.currentTimeMillis();
//             sb.append("，startTime=").append(DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//             list = outFeign.getDistributorPage(pageSize, startTime, new Date());
//             if (CollectionUtil.isEmpty(list)) {
//                 break;
//             }
//             startTime = list.get(list.size() - 1).getCreateTime();
//             result.addAll(list);
//             long l22 = System.currentTimeMillis();
//             sb.append("单次查询耗时：").append(l22 - l11).append("毫秒。查询到的结果数=").append(list.size());
//             if (l22 - l11 >= 15000) {
//                 log.info("========单次查询耗时过长：" + (l22 - l11) + "毫秒。");
//             }
//             log.info(sb.toString());
//             rabbitTemplate.convertAndSend(RabbitConstant.SYNC_DISTRIBUTOR, list);
//         }
//         log.info("pages=" + pages);
//         log.info("result.size()=" + result.size());
//
//         long l2 = System.currentTimeMillis();
//         log.info("总查询耗时：" + (l2 - l1) + "毫秒。");
//     }
//
// }
