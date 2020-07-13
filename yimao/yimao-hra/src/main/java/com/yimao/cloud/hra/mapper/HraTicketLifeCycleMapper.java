package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.HraTicketLifeCycle;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao
 * @date
 */
public interface HraTicketLifeCycleMapper extends Mapper<HraTicketLifeCycle> {

    //根据经销商id，查询优惠卡赠送记录
    Page<HraTicketLifeCycle> ticketGiveByUserId(@Param("userId") Long userId,
                                                @Param("cashId") Long cashId,
                                                @Param("beginTime") String beginTime,
                                                @Param("endTime") String endTime);

    void updateHraTicketLifeCycleToBeReceive(@Param("sharerId") Integer sharerId,
                                             @Param("destId") Integer destId,
                                             @Param("handselList") List<String> handselList,
                                             @Param("receiveTime") Date receiveTime,
                                             @Param("handselFlag") Long handselFlag,
                                             @Param("expiredFlag") int expiredFlag);

    Integer hasGiveCard(@Param("ticketNo") String ticketNo, @Param("userId") Integer userId);

    HraTicketLifeCycle findTicketCycle(Map<String, Object> map);

    HraTicketLifeCycle getTicketCycle(Map<String, Object> map);

    HraTicketLifeCycle findTicketCycleByMap(Map<String, Object> map);

    List<HraTicketLifeCycle> getTicketCycleByTimes(Long[] handselTimes);

    List<HraTicketLifeCycle> selectLifeCycleByMap(Map<String, Object> map);

//    HraTicketLifeCycleDTO getOrigUserInfo(@Param("origUserId") Integer origUserId);
}
