package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.WorkOrderRefuse;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WorkOrderRefuseMapper extends Mapper<WorkOrderRefuse> {
    List<WorkOrderRefuse> selectByWorkOrderId(@Param("workOrderId") String workOrderId, @Param("province") String province,
                                              @Param("city") String city, @Param("region") String region);
}
