package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.enums.DistributorRoleLevel;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.feign.UserFeign;
import com.yimao.cloud.order.mapper.QuotaChangeRecordMapper;
import com.yimao.cloud.order.po.QuotaChangeRecord;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.pojo.dto.order.QuotaChangeRecordDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class QuotaChangeRecordServiceImpl implements QuotaChangeRecordService {

    @Resource
    private UserFeign userFeign;
    @Resource
    private QuotaChangeRecordMapper quotaChangeRecordMapper;

    @Override
    public void quotaChange(Long orderId, Integer distributorId, String reason, Integer type, Integer count, BigDecimal amount) {
        QuotaChangeRecord quotaChangeRecord = new QuotaChangeRecord();
        if (distributorId == null) {
            log.error("新增配额记录失败,订单信息有误");
            throw new YimaoException("新增配额记录失败,订单信息有误");
        }
        DistributorDTO distributorDTO = userFeign.getDistributorBasicInfoById(distributorId);
        if (Objects.isNull(distributorDTO)) {
            log.error("新增配额记录失败,订单信息有误");
            throw new YimaoException("新增配额记录失败,订单信息有误");
        }

        //记录配额变化
        quotaChangeRecord.setOrderId(orderId);
        quotaChangeRecord.setDistributorId(distributorId);
        quotaChangeRecord.setType(type);
        quotaChangeRecord.setCount(count);
        quotaChangeRecord.setReason(reason);
        quotaChangeRecord.setCreateTime(new Date());
        quotaChangeRecord.setQuota(distributorDTO.getQuota());
        quotaChangeRecord.setReplacementAmount(distributorDTO.getReplacementAmount());
        quotaChangeRecord.setRemainingQuota(distributorDTO.getRemainingQuota() == null ? 0 : distributorDTO.getRemainingQuota() + count);
        distributorDTO.setUpdateTime(new Date());

        if (Objects.equals(distributorDTO.getRoleLevel(), DistributorRoleLevel.DISCOUNT.value) && amount != null) {
            if (type == 2) {
                quotaChangeRecord.setRemainingReplacementAmount(distributorDTO.getRemainingReplacementAmount().add(amount));
            } else {
                quotaChangeRecord.setRemainingReplacementAmount(distributorDTO.getRemainingReplacementAmount().subtract(amount));
            }
        }

        distributorDTO.setRemainingQuota(quotaChangeRecord.getRemainingQuota());
        distributorDTO.setRemainingReplacementAmount(quotaChangeRecord.getRemainingReplacementAmount() != null ? quotaChangeRecord.getRemainingReplacementAmount() : new BigDecimal(0));
        //修改经销商配额/金额
        quotaChangeRecordMapper.insert(quotaChangeRecord);
        userFeign.updateDistributor(distributorDTO);
    }
}
