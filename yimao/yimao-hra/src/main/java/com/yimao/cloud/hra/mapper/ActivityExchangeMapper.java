package com.yimao.cloud.hra.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.hra.po.ActivityExchange;
import com.yimao.cloud.pojo.dto.hra.ActivityExchangeDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * @author Lizhqiang
 * @date 2019/4/10
 */
public interface ActivityExchangeMapper extends Mapper<ActivityExchange> {
    Page<ActivityExchangeDTO> getActivityExchangePage(@Param(value = "side") Integer side,
                                                      @Param(value = "channel") Integer channel,
                                                      @Param(value = "exchangeCode") String exchangeCode,
                                                      @Param(value = "batchNumber") String batchNumber,
                                                      @Param(value = "exchangeStatus") String exchangeStatus,
                                                      @Param(value = "ticketNo") String ticketNo,
                                                      @Param(value = "ticketStatus") Integer ticketStatus);

    void insertBatch(@Param(value = "exchangeList") List<ActivityExchange> exchangeList);

    void exChangeSet(@Param(value = "terminal") Integer terminal,
                     @Param(value = "limitType") Integer limitType,
                     @Param(value = "times") Integer times);
}
