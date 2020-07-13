package com.yimao.cloud.user.service.impl;

import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.utils.ThreadUtil;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.user.UserOverviewDTO;
import com.yimao.cloud.user.mapper.DistributorMapper;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.mapper.DistributorProtocolMapper;
import com.yimao.cloud.user.mapper.UserMapper;
import com.yimao.cloud.user.service.UserOverviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class UserOverviewServiceImpl implements UserOverviewService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DistributorMapper distributorMapper;
    @Resource
    private DistributorOrderMapper distributorOrderMapper;
    @Resource
    private DistributorProtocolMapper distributorProtocolMapper;

    /**
     * 用户概况统计
     *
     * @return UserOverviewDTO
     * @author hhf
     * @date 2019/3/19
     */
    @Override
    public UserOverviewDTO overview() {
        UserOverviewDTO userOverviewDTO = new UserOverviewDTO();
        try {
            // 经销商总数
            Callable<Integer> callableDistributorTotal = () -> distributorMapper.countDistributorTotal(null);
            FutureTask<Integer> distributorTotalFutureTask = new FutureTask<>(callableDistributorTotal);
            ThreadUtil.executor.submit(distributorTotalFutureTask);


            // 代理商总数
            Callable<Integer> callableAgentTotal = () -> distributorMapper.countAgentTotal(null);
            FutureTask<Integer> agentTotalFutureTask = new FutureTask<>(callableAgentTotal);
            ThreadUtil.executor.submit(agentTotalFutureTask);

            // 用户总数
            Callable<Integer> callableUserTotal = () -> userMapper.countUserTotal(null);
            FutureTask<Integer> userTotalFutureTask = new FutureTask<>(callableUserTotal);
            ThreadUtil.executor.submit(userTotalFutureTask);


            // 经销商总数,代理商总数,用户总数 近7天 近30天 近180天变化趋势
            Callable<Integer> callableDistributorTotalWeek = () -> distributorMapper.countDistributorTotal(1);
            FutureTask<Integer> distributorTotalWeekFutureTask = new FutureTask<>(callableDistributorTotalWeek);
            ThreadUtil.executor.submit(distributorTotalWeekFutureTask);

            Callable<Integer> callableAgentTotalWeek = () -> distributorMapper.countAgentTotal(1);
            FutureTask<Integer> agentTotalFutureTaskWeek = new FutureTask<>(callableAgentTotalWeek);
            ThreadUtil.executor.submit(agentTotalFutureTaskWeek);

            Callable<Integer> callableUserTotalWeek = () -> userMapper.countUserTotal(1);
            FutureTask<Integer> userTotalFutureTaskWeek = new FutureTask<>(callableUserTotalWeek);
            ThreadUtil.executor.submit(userTotalFutureTaskWeek);

            Callable<Integer> callableDistributorTotalMonth = () -> distributorMapper.countDistributorTotal(2);
            FutureTask<Integer> distributorTotalMonthFutureTask = new FutureTask<>(callableDistributorTotalMonth);
            ThreadUtil.executor.submit(distributorTotalMonthFutureTask);

            Callable<Integer> callableAgentTotalMonth = () -> distributorMapper.countAgentTotal(2);
            FutureTask<Integer> agentTotalMonthFutureTask = new FutureTask<>(callableAgentTotalMonth);
            ThreadUtil.executor.submit(agentTotalMonthFutureTask);

            Callable<Integer> callableUserTotalMonth = () -> userMapper.countUserTotal(2);
            FutureTask<Integer> userTotalMonthFutureTask = new FutureTask<>(callableUserTotalMonth);
            ThreadUtil.executor.submit(userTotalMonthFutureTask);


            Callable<Integer> callableDistributorTotal3Month = () -> distributorMapper.countDistributorTotal(3);
            FutureTask<Integer> distributorTotal3MonthFutureTask = new FutureTask<>(callableDistributorTotal3Month);
            ThreadUtil.executor.submit(distributorTotal3MonthFutureTask);

            Callable<Integer> callableAgentTotal3Month = () -> distributorMapper.countAgentTotal(3);
            FutureTask<Integer> agentTotal3MonthFutureTask = new FutureTask<>(callableAgentTotal3Month);
            ThreadUtil.executor.submit(agentTotal3MonthFutureTask);

            Callable<Integer> callableUserTotal3Month = () -> userMapper.countUserTotal(3);
            FutureTask<Integer> userTotal3MonthFutureTask = new FutureTask<>(callableUserTotal3Month);
            ThreadUtil.executor.submit(userTotal3MonthFutureTask);


            // 待财务审核订单
            Callable<Integer> callableFinancialCount = () -> distributorOrderMapper.selectCount4Status(1);
            FutureTask<Integer> financialCountFutureTask = new FutureTask<>(callableFinancialCount);
            ThreadUtil.executor.submit(financialCountFutureTask);
            // 待留商审核订单
            Callable<Integer> callableEnterpriseCount = () -> distributorOrderMapper.selectCount4Status(2);
            FutureTask<Integer> enterpriseCountFutureTask = new FutureTask<>(callableEnterpriseCount);
            ThreadUtil.executor.submit(enterpriseCountFutureTask);

            // 待法务审核订单
            Callable<Integer> callableProtocolCount = () -> distributorProtocolMapper.selectCount4Status();
            FutureTask<Integer> protocolFutureTask = new FutureTask<>(callableProtocolCount);
            ThreadUtil.executor.submit(protocolFutureTask);

            userOverviewDTO.setDistributorTotal(distributorTotalFutureTask.get());
            userOverviewDTO.setAgentTotal(agentTotalFutureTask.get());
            userOverviewDTO.setUserTotal(userTotalFutureTask.get());
            userOverviewDTO.setDistributorTotalOfWeek(distributorTotalWeekFutureTask.get());
            userOverviewDTO.setAgentTotalOfWeek(agentTotalFutureTaskWeek.get());
            userOverviewDTO.setUserTotalOfWeek(userTotalFutureTaskWeek.get());
            userOverviewDTO.setDistributorTotalOfMonth(distributorTotalMonthFutureTask.get());
            userOverviewDTO.setAgentTotalOfMonth(agentTotalMonthFutureTask.get());
            userOverviewDTO.setUserTotalOfMonth(userTotalMonthFutureTask.get());
            userOverviewDTO.setDistributorTotalOf3Month(distributorTotal3MonthFutureTask.get());
            userOverviewDTO.setAgentTotalOf3Month(agentTotal3MonthFutureTask.get());
            userOverviewDTO.setUserTotalOf3Month(userTotal3MonthFutureTask.get());
            userOverviewDTO.setFinancialCount(financialCountFutureTask.get());
            userOverviewDTO.setEnterpriseCount(enterpriseCountFutureTask.get());
            userOverviewDTO.setProtocolCount(protocolFutureTask.get());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotFoundException("查询出错，请稍后重试。");
        }
        return userOverviewDTO;
    }

    @Override
    public BusinessProfileDTO overviewBusiness() {
        BusinessProfileDTO businessProfileDTO = new BusinessProfileDTO();
        // 经销商总数 用户总数 昨日新增经销商数 昨日新增用户数
        businessProfileDTO.setUserTotal(userMapper.countUserTotal(null));
        businessProfileDTO.setYestUserTotal(userMapper.countUserTotal(4));
        businessProfileDTO.setDistributorTotal(distributorMapper.countDistributorTotal(null));
        businessProfileDTO.setYestDistributorTotal(distributorMapper.countDistributorTotal(4));
        return businessProfileDTO;
    }
}
