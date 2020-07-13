// package com.yimao.cloud.user.jobs;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.out.CustomerDTO;
// import com.yimao.cloud.user.feign.OutFeign;
// import com.yimao.cloud.user.mapper.EngineerMapper;
// import com.yimao.cloud.user.po.Engineer;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import tk.mybatis.mapper.entity.Example;
//
// import javax.annotation.Resource;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
//
// @Component
// @EnableScheduling
// @Slf4j
// public class SyncEngineerJob {
//
//     @Resource
//     private OutFeign outFeign;
//
//     @Resource
//     private EngineerMapper engineerMapper;
//
//     /**
//      * 每5个小时从云平台mongo数据库同步一次安装工信息到微商城MySQL数据库
//      */
//     @Scheduled(cron = "0 10 0/5 * * ?")
//     public void execute() {
//         try {
//             log.info("同步安装工信息定时任务开始执行===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
//             long l1 = System.currentTimeMillis();
//             //返回结果
//             Set<CustomerDTO> result = new HashSet<>();
//
//             Date startTime = outFeign.getEngineerMinTime();
//             if (startTime == null) {
//                 startTime = DateUtil.transferStringToDate("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//             }
//
//             log.info("startTime=" + DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//
//             long total = outFeign.getEngineerTotalCount(startTime);
//
//             List<CustomerDTO> list;
//             int pages = 0;
//             int pageSize = 50;
//             while (startTime.getTime() < new Date().getTime() && result.size() < total) {
//                 StringBuilder sb = new StringBuilder();
//                 sb.append("第").append(++pages).append("次查询，当前结果集大小=").append(result.size()).append("，total=").append(total);
//                 long l11 = System.currentTimeMillis();
//                 sb.append("，startTime=").append(DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//                 list = outFeign.getEngineerPage(pageSize, startTime, new Date());
//                 if (CollectionUtil.isEmpty(list)) {
//                     break;
//                 }
//                 startTime = list.get(list.size() - 1).getCreateTime();
//                 result.addAll(list);
//                 long l22 = System.currentTimeMillis();
//                 sb.append("单次查询耗时：").append(l22 - l11).append("毫秒。查询到的结果数=").append(list.size());
//                 if (l22 - l11 >= 15000) {
//                     log.info("========单次查询耗时过长：" + (l22 - l11) + "毫秒。");
//                 }
//                 log.info(sb.toString());
//                 //添加或更新经销商数据
//                 this.processor(list);
//             }
//             log.info("pages=" + pages);
//             log.info("result.size()=" + result.size());
//
//             long l2 = System.currentTimeMillis();
//             log.info("同步安装工信息定时任务执行完毕===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
//             log.info("同步安装工信息定时任务执行耗时：" + (l2 - l1) + "毫秒。");
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//         }
//     }
//
//     /**
//      * 添加或更新安装工信息
//      *
//      * @param list
//      */
//     private void processor(List<CustomerDTO> list) {
//         if (CollectionUtil.isNotEmpty(list)) {
//             for (CustomerDTO dto : list) {
//                 Engineer record = new Engineer();
//                 record.setUserName(dto.getName());
//                 record.setPassword(dto.getPassword());
//                 record.setRealName(dto.getRealName());
//                 record.setPhone(dto.getPhone());
//                 record.setProvince(dto.getProvince());
//                 record.setCity(dto.getCity());
//                 record.setRegion(dto.getRegion());
//                 record.setSex(StringUtil.isEmpty(dto.getSex()) ? 1 : Integer.parseInt(dto.getSex()));
//                 record.setEmail(dto.getMail());
//                 record.setAddress(dto.getAddress());
//                 record.setIdCard(dto.getWorkId());
//                 record.setState(dto.getState());
//                 record.setAppType(dto.getAppType());//终端设备类型：1-Android；2-ios
//                 record.setCount(dto.getCount());
//                 record.setForbidden(dto.getUsed() != null && dto.getUsed() == 1);
//                 record.setLoginCount(dto.getLoginCount());
//                 record.setLastLoginTime(dto.getUpdateTime());
//                 record.setVersion(dto.getVersion());
//                 record.setStationName(dto.getServicesiteName());
//                 record.setIccid(dto.getIccid());
//                 record.setCreateTime(dto.getCreateTime());
//                 record.setUpdateTime(dto.getUpdateTime());
//                 record.setOldId(dto.getId());
//                 record.setOldSiteId(dto.getOldSiteId());
//                 try {
//                     boolean exists = engineerMapper.existsWithOldId(dto.getId());
//                     if (!exists) {
//                         engineerMapper.insert(record);
//                     } else {
//                         Example example = new Example(Engineer.class);
//                         Example.Criteria criteria = example.createCriteria();
//                         criteria.andEqualTo("oldId", dto.getId());
//                         engineerMapper.updateByExampleSelective(record, example);
//                     }
//                 } catch (Exception e) {
//                     log.error(e.getMessage(), e);
//                 }
//             }
//         }
//     }
// }
