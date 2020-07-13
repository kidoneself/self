package com.yimao.cloud.hra.service;

import com.yimao.cloud.hra.po.HraTicketLifeCycle;

import java.util.Date;
import java.util.List;

public interface TicketLifeCycleService {
    // 根据赠送标志查询生命周期记录
    HraTicketLifeCycle findTicketLifeCycleByHandselFlag(Long handselFalg);

    // 更新生命周期的状态
    int updateTicketLifeCycle(HraTicketLifeCycle ticketLifeCycle);

    List<HraTicketLifeCycle> listTicketLifeCycle(String cardId, Integer origUserId, Date handselTime, Integer expiredFlag);

    void updateHandselRecordToTimeOut(HraTicketLifeCycle ticketLifeCycle);

    HraTicketLifeCycle getLifeCycle(String ticketNo, Integer origUserId, Long handselTime);

    HraTicketLifeCycle getLifeCycleByTicketNoAndOrigUserId(String tickedNo, Integer origUserId);

    List<HraTicketLifeCycle> getTicketLifeCycle(Integer userId);
}
