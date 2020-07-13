package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.RepairWorkOrder;
import com.yimao.cloud.pojo.dto.order.RenewDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface RepairWorkOrderMapper extends Mapper<RepairWorkOrder> {

    List<RenewDTO> getRepairWorkOrderList(@Param("completeTime") String completeTime,
                                          @Param("engineerId") Integer engineerId,
                                          @Param("timeType") Integer timeType);
}
