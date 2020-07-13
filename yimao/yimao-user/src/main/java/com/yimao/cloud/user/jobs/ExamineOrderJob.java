package com.yimao.cloud.user.jobs;

import com.yimao.cloud.base.enums.DistributorOrderStateEnum;
import com.yimao.cloud.base.enums.DistributorOrderType;
import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.enums.FinancialStateEnum;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.dto.user.DistributorRoleDTO;
import com.yimao.cloud.user.mapper.DistributorOrderMapper;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import com.yimao.cloud.user.service.DistributorOrderService;
import com.yimao.cloud.user.service.DistributorRoleService;
import com.yimao.cloud.user.service.DistributorService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 每15分钟检查财务审核不通过的微创经销商升级为个人版的经销商订单，如果超过该经销商的升级剩余有效期则使订单失效
 *
 * @author Liu long jie
 * @date 2019/10/15
 */
@Slf4j
@DisallowConcurrentExecution//该注解表示前一个任务执行结束再执行下一次任务
public class ExamineOrderJob extends QuartzJobBean {
    @Resource
    private DistributorOrderMapper distributorOrderMapper;

    @Resource
    private DistributorRoleService distributorRoleService;

    @Resource
    private DistributorService distributorService;

    @Resource
    private DistributorOrderService distributorOrderService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("检查财务审核不通过的微创经销商升级为个人版的经销商订单定时任务开始执行===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            Example example = new Example(DistributorOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderType", DistributorOrderType.UPGRADE.value); //升级订单
            criteria.andEqualTo("roleLevel", DistributorRoleLevel.D_350.value); //原经销商类型为微创版
            criteria.andEqualTo("destRoleLevel", DistributorRoleLevel.D_650.value); //升级的经销商类型为个人版
            criteria.andEqualTo("financialState", FinancialStateEnum.UN_PASS_AUDIT.value); //财务审核不通过
            criteria.andNotEqualTo("orderState", DistributorOrderStateEnum.CLOSE.value); //订单状态不为已关闭
            List<DistributorOrder> distributorOrders = distributorOrderMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(distributorOrders)) {
                for (DistributorOrder distributorOrder : distributorOrders) {
                    //获取经销商信息
                    DistributorDTO distributorDTO = distributorService.getBasicInfoById(distributorOrder.getDistributorId());
                    Distributor distributor = new Distributor(distributorDTO);
                    DistributorRoleDTO origDistributorRole = distributorRoleService.getByLevel(distributorOrder.getRoleLevel());
                    boolean flag = distributorOrderService.upgradeValidityTime(origDistributorRole.getUpgradeLimitDays(), distributor);
                    if (!flag) {
                        //已过期，将订单关闭
                        distributorOrder.setOrderState(DistributorOrderStateEnum.CLOSE.value);
                        int count = distributorOrderMapper.updateByPrimaryKeySelective(distributorOrder);
                        if (count != 1) {
                            throw new YimaoException("经销商订单状态修改失败！");
                        }
                    }
                }
            }
            log.info("检查财务审核不通过的微创经销商升级为个人版的经销商订单定时任务执行完毕===" + DateUtil.transferDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (YimaoException e) {
            log.error(e.getMessage(), e);
        }

    }
}
