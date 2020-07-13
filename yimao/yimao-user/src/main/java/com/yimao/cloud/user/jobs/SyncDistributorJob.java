// package com.yimao.cloud.user.jobs;
//
// import com.yimao.cloud.base.utils.CollectionUtil;
// import com.yimao.cloud.base.utils.DateUtil;
// import com.yimao.cloud.base.utils.StringUtil;
// import com.yimao.cloud.pojo.dto.out.DistributorDTO;
// import com.yimao.cloud.user.feign.OutFeign;
// import com.yimao.cloud.user.mapper.DistributorMapper;
// import com.yimao.cloud.user.mapper.DistributorRoleMapper;
// import com.yimao.cloud.user.po.Distributor;
// import com.yimao.cloud.user.po.DistributorRole;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import tk.mybatis.mapper.entity.Example;
//
// import javax.annotation.Resource;
// import java.math.BigDecimal;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
//
// @Component
// @EnableScheduling
// @Slf4j
// public class SyncDistributorJob {
//
//     @Resource
//     private OutFeign outFeign;
//
//     @Resource
//     private DistributorMapper distributorMapper;
//     @Resource
//     private DistributorRoleMapper distributorRoleMapper;
//
//     /**
//      * 每1个小时从云平台mongo数据库同步一次经销商信息到微商城MySQL数据库库
//      */
//     @Scheduled(cron = "0 0 0/1 * * ?")
//     public void execute() {
//         try {
//             log.info("同步经销商信息定时任务开始执行===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
//             long l1 = System.currentTimeMillis();
//             //返回结果
//             Set<DistributorDTO> result = new HashSet<>();
//
//             Date startTime = outFeign.getDistributorMinTime();
//             if (startTime == null) {
//                 startTime = DateUtil.transferStringToDate("1970-01-01 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
//             }
//             log.info("startTime=" + DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//
//             long total = outFeign.getDistributorTotalCount(startTime);
//
//             List<DistributorDTO> list;
//             int pages = 0;
//             int pageSize = 50;
//             while (startTime.getTime() < new Date().getTime() && result.size() < total) {
//                 StringBuilder sb = new StringBuilder();
//                 sb.append("第").append(++pages).append("次查询，当前结果集大小=").append(result.size()).append("，total=").append(total);
//                 long l11 = System.currentTimeMillis();
//                 sb.append("，startTime=").append(DateUtil.transferDateToString(startTime, "yyyy-MM-dd HH:mm:ss.SSS"));
//                 list = outFeign.getDistributorPage(pageSize, startTime, new Date());
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
//             this.doSetDistributorInfo();
//
//             long l2 = System.currentTimeMillis();
//             log.info("同步经销商信息定时任务执行完毕===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
//             log.info("同步经销商信息定时任务执行耗时：" + (l2 - l1) + "毫秒。");
//         } catch (Exception e) {
//             log.error(e.getMessage(), e);
//         }
//     }
//
//     /**
//      * 当有新经销商信息同步到MySQL数据库时，新数据的推荐人信息、企业版主账号信息、角色信息等要设置
//      */
//     private void doSetDistributorInfo() {
//         //1-设置推荐人信息
//         Example example = new Example(Distributor.class);
//         Example.Criteria criteria = example.createCriteria();
//         criteria.andIsNull("recommendId");
//         criteria.andIsNotNull("oldRecommendId");
//         List<Distributor> list = distributorMapper.selectByExample(example);
//         if (CollectionUtil.isNotEmpty(list)) {
//             for (Distributor d : list) {
//                 String oldRecommendId = d.getOldRecommendId();
//                 Distributor query = new Distributor();
//                 query.setOldId(oldRecommendId);
//                 Distributor recommend = distributorMapper.selectOne(query);
//                 if (recommend != null) {
//                     Distributor update = new Distributor();
//                     update.setId(d.getId());
//                     update.setRecommendId(recommend.getId());
//                     update.setRecommendName(recommend.getRealName());
//                     distributorMapper.updateByPrimaryKeySelective(update);
//                 }
//
//             }
//         }
//         //2-设置企业版主账号信息
//         example = new Example(Distributor.class);
//         criteria = example.createCriteria();
//         criteria.andIsNull("pid");
//         criteria.andIsNotNull("oldPid");
//         list = distributorMapper.selectByExample(example);
//         if (CollectionUtil.isNotEmpty(list)) {
//             for (Distributor d : list) {
//                 String oldPid = d.getOldPid();
//                 Distributor query = new Distributor();
//                 query.setOldId(oldPid);
//                 Distributor p = distributorMapper.selectOne(query);
//                 if (p != null) {
//                     Distributor update = new Distributor();
//                     update.setId(d.getId());
//                     update.setPid(p.getId());
//                     distributorMapper.updateByPrimaryKeySelective(update);
//                 }
//
//             }
//         }
//         //3-设置角色信息
//         example = new Example(Distributor.class);
//         criteria = example.createCriteria();
//         criteria.andIsNull("roleId");
//         criteria.andIsNull("roleName");
//         criteria.andIsNotNull("roleLevel");
//         list = distributorMapper.selectByExample(example);
//         if (CollectionUtil.isNotEmpty(list)) {
//             for (Distributor d : list) {
//                 Integer roleLevel = d.getRoleLevel();
//                 DistributorRole roleQuery = new DistributorRole();
//                 roleQuery.setLevel(roleLevel);
//                 DistributorRole role = distributorRoleMapper.selectOne(roleQuery);
//                 if (role != null) {
//                     Distributor update = new Distributor();
//                     update.setId(d.getId());
//                     update.setRoleId(role.getId());
//                     update.setRoleName(role.getName());
//                     distributorMapper.updateByPrimaryKeySelective(update);
//                 }
//
//             }
//         }
//
//         //4-经销商的代理级别设置为空
//         distributorMapper.updateAgentLevelToNull();
//     }
//
//     /**
//      * 添加或更新经销商信息
//      *
//      * @param list
//      */
//     private void processor(List<DistributorDTO> list) {
//         if (CollectionUtil.isNotEmpty(list)) {
//             for (DistributorDTO dto : list) {
//                 Distributor record = new Distributor();
//                 record.setOldId(dto.getId());
//                 record.setUserName(dto.getName());
//                 record.setPassword(dto.getPassword());
//                 record.setNickName(dto.getRealName());
//                 record.setRealName(dto.getRealName());
//                 record.setSex(StringUtil.isEmpty(dto.getSex()) ? 1 : Integer.parseInt(dto.getSex()));
//                 record.setPhone(dto.getPhone());
//                 record.setProvince(dto.getProvince());
//                 record.setCity(dto.getCity());
//                 record.setRegion(dto.getRegion());
//                 record.setAddress(dto.getAddress());
//                 record.setIdCard(dto.getWorkId());
//                 record.setEmail(dto.getMail());
//                 if (dto.getPayMoney() != null) {
//                     record.setMoney(new BigDecimal(dto.getPayMoney()));
//                 }
//                 if (dto.getType() != null) {
//                     record.setType(dto.getType() + 1);
//                 }
//                 record.setFounder(dto.getSpecial() != null && dto.getSpecial() == 1);
//                 record.setStationMaster(dto.getStationMaster() != null && dto.getStationMaster());
//                 record.setOldRecommendId(dto.getReferee());
//                 record.setSponsor(dto.getIsSponsors() != null && dto.getIsSponsors() == 1);
//                 // record.setSponsorLevel();
//                 if (dto.getLevel() != null) {
//                     int agentLevel;
//                     if (dto.getLevel() == 1) {
//                         agentLevel = 1;
//                     } else if (dto.getLevel() == 2) {
//                         agentLevel = 2;
//                     } else if (dto.getLevel() == 3) {
//                         agentLevel = 4;
//                     } else if (dto.getLevel() == 4) {
//                         agentLevel = 3;
//                     } else if (dto.getLevel() == 5) {
//                         agentLevel = 7;
//                     } else if (dto.getLevel() == 6) {
//                         agentLevel = 6;
//                     } else {
//                         agentLevel = 5;
//                     }
//                     record.setAgentLevel(agentLevel);
//                 }
//                 record.setRoleLevel(dto.getRoleLevel());
//                 record.setCompanyName(dto.getCompany());
//                 record.setOldPid(dto.getPid());
//                 record.setQuota(dto.getCount());
//                 record.setRemainingQuota(dto.getCount());
//                 record.setDeleted(false);
//                 record.setForbidden(dto.getForbidden() == null ? false : dto.getForbidden());
//                 record.setForbiddenOrder(dto.getForbiddenOrder() == null ? false : dto.getForbiddenOrder());
//                 record.setFuhuishun(dto.getFuhuishun() != null && dto.getFuhuishun() == 1);
//                 record.setCreateTime(dto.getCreateTime());
//                 record.setUpdateTime(dto.getUpdateTime());
//                 record.setCompleteTime(dto.getDisTime());//成为经销商的时间
//
//                 record.setAppType(dto.getAppType());//终端设备类型：1-Android；2-ios
//                 record.setVersion(dto.getVersion());
//
//                 try {
//                     boolean exists = distributorMapper.existsWithOldId(dto.getId());
//                     if (!exists) {
//                         distributorMapper.insert(record);
//                     } else {
//                         Example example = new Example(Distributor.class);
//                         Example.Criteria criteria = example.createCriteria();
//                         criteria.andEqualTo("oldId", dto.getId());
//                         distributorMapper.updateByExampleSelective(record, example);
//                     }
//                 } catch (Exception e) {
//                     log.error(e.getMessage(), e);
//                 }
//             }
//         }
//     }
// }
