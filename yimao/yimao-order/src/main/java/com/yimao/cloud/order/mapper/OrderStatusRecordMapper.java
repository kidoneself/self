package com.yimao.cloud.order.mapper;

import com.yimao.cloud.order.po.OrderStatusRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2019/1/11
 */
public interface OrderStatusRecordMapper extends Mapper<OrderStatusRecord> {

    // 批量插入订单状态变更
    Integer insertBatch(List<OrderStatusRecord> list);

	OrderStatusRecord selectUptodateOrderStatusByOrderSubId(Long id);

}
