package com.yimao.cloud.hra.mapper;

import com.yimao.cloud.hra.po.HraFlowRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

/**
 * @author Zhang Bo
 * @date 2018/5/14.
 */
public interface HraFlowRecordMapper extends Mapper<HraFlowRecord> {
    Map<String,String> hasReportByDeviceAndTicket(@Param("deviceId") String deviceId,
                                                  @Param("ticketNo") String ticketNo);
}
