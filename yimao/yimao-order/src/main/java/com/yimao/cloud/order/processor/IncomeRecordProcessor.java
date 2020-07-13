package com.yimao.cloud.order.processor;

import com.yimao.cloud.base.constant.RabbitConstant;
import com.yimao.cloud.base.enums.OrderCompleteStatusEnum;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.order.mapper.ProductIncomeRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 描述：TODO
 *
 * @Author Zhang Bo
 * @Date 2019/11/7
 */
@Component
@Slf4j
public class IncomeRecordProcessor {

    @Resource
    private ProductIncomeRecordMapper productIncomeRecordMapper;

    /**
     * 体验版经销商升级之后需要把收益结算数据的状态修改为“可结算”，和结算月份
     *
     * @param distributorId 经销商ID
     */
    @RabbitListener(queues = RabbitConstant.DISTRIBUTOR_UPGRADE_INCOME_STATUS)
    @RabbitHandler
    public void process(Integer distributorId) {
        try {
            log.info("开始处理体验版经销商（ID={}）的收益结算数据，将其变更为可结算状态并将结算月份设值为当前日期的下个月份。", distributorId);
            if (distributorId != null) {
                String settlementMonth = DateUtil.transferDateToString(DateUtil.monthAfter(new Date(), 1), "yyyy-MM");
                productIncomeRecordMapper.updateStatusAndSettlementMonthByDistributorId(distributorId, OrderCompleteStatusEnum.UNCOMPLETED.value, OrderCompleteStatusEnum.CAN_BE_SETTLED.value, settlementMonth);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
