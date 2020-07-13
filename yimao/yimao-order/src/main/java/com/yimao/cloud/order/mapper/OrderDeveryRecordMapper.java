package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderDeliveryRecord;
import com.yimao.cloud.pojo.dto.order.OrderDeliveryRecordDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderDeveryRecordMapper extends Mapper<OrderDeliveryRecord> {

    void insertBatch(List<OrderDeliveryRecord> deliveryRecords);

    Page<OrderDeliveryRecordDTO> deliveryRecordList(@Param("orderId") String orderId,
                                                    @Param("logisticsNo") String logisticsNo,
                                                    @Param("startTime") String startTime,
                                                    @Param("endTime") String endTime,
                                                    @Param("userId") Integer userId,
                                                    @Param("addreessName") String addreessName,
                                                    @Param("terminal") Integer terminal);
}
