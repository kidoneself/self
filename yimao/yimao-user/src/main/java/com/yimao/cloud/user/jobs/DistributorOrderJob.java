package com.yimao.cloud.user.jobs;

import com.yimao.cloud.base.enums.DistributorOrderStateEnum;
import com.yimao.cloud.base.enums.DistributorOrderType;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.service.DistributorRoleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 每15分钟更新升级为微创版的经销商订单的升级剩余有效时间
 *
 * @author Liu long jie
 * @date 2019/10/15
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class DistributorOrderJob extends QuartzJobBean {

    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private DistributorRoleService distributorRoleService;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("更新经销商订单的升级剩余有效时间定时任务开始执行===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));

            //获取升级为微创版的经销商订单
            Example example = new Example(DistributorOrder.class);
            Example.Criteria criteria = example.createCriteria();
            List<Integer> query = new ArrayList<>();
            query.add(DistributorOrderType.REGISTER.value);//订单类型为注册订单
            query.add(DistributorOrderType.UPGRADE.value);//订单类型为升级订单
            criteria.andIn("orderType", query);
            criteria.andEqualTo("orderState", DistributorOrderStateEnum.COMPLETED.value);//状态为已完成的订单
            List<DistributorOrder> list = distributorOrderMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                for (DistributorOrder distributorOrder : list) {
                    if ((distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value && distributorOrder.getRoleLevel() == DistributorRoleLevel.D_350.value) ||
                            (distributorOrder.getOrderType() == DistributorOrderType.UPGRADE.value && distributorOrder.getDestRoleLevel() == DistributorRoleLevel.D_350.value)
                            &&(distributorOrder.getPeriodValidity() == null || (distributorOrder.getPeriodValidity() != null && distributorOrder.getPeriodValidity() > 0 ))
                            ) {
                        //获取注册为微创版或者升级为微创版的订单 (过滤升级剩余有效期天数为0的订单)
                        //订单完成时间
                        Date completionTime = distributorOrder.getCompletionTime();
                        long differTime = 0;
                        if (completionTime != null) {
                            differTime = DateUtil.getSeconds(completionTime, new Date()); //获取当前时间距离订单完成时间的时间差，单位秒
                        }
                        if (differTime > 0) {
                            int differDay = (int) differTime / 60 / 60 / 24; //转换单位为天 --- 当前时间距离订单完成时间的天数
                            Integer roleLevel = null;
                            if (distributorOrder.getOrderType() == DistributorOrderType.REGISTER.value) {
                                //注册订单取原经销商类型
                                roleLevel = distributorOrder.getRoleLevel();
                            } else {
                                //升级订单取升级经销商类型
                                roleLevel = distributorOrder.getDestRoleLevel();
                            }
                            if (roleLevel == null) {
                                //经销商类型不能不为空
                                throw new YimaoException("经销商类型不能为空！");
                            }
                            DistributorRoleDTO role = distributorRoleService.getByLevel(roleLevel);
                            if (role == null) {
                                throw new YimaoException("经销商类型不存在!");
                            }
                            Integer upgradeLimitDays = role.getUpgradeLimitDays(); //补差价的有效天数
                            Integer periodValidity = upgradeLimitDays - differDay > 0 ? upgradeLimitDays - differDay : 0;
                            distributorOrder.setPeriodValidity(periodValidity);
                            int count = distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
                            if (count != 1) {
                                throw new YimaoException("订单信息更新失败");
                            }
                        }
                    }
                }
            }
            log.info("更新经销商订单的升级剩余有效时间定时任务执行完毕===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
