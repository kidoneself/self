package com.yimao.cloud.order.service;

import java.math.BigDecimal;

public interface QuotaChangeRecordService {

    void quotaChange(Long orderId, Integer distributorId, String reason, Integer type, Integer count, BigDecimal amount);

}
