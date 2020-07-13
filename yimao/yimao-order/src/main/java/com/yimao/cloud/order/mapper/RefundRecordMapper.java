package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.RefundRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface RefundRecordMapper extends Mapper<RefundRecord> {
    boolean existsWithOutRefundNo(@Param("outRefundNo") String outRefundNo, @Param("platform") Integer platform);
}
