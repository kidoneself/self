package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderDelivery;
import com.yimao.cloud.pojo.dto.order.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2018/12/27
 */
public interface OrderDeliveryMapper extends Mapper<OrderDelivery> {

    // 批量插入收货单
    void insertBatch(List<OrderDeliveryDTO> list);

    Page<DeliveryInfoDTO> pageQueryList(DeliveryDTO deliveryDTO);

    /**
     * @description
     * @author zhilin.he
     * @date 2019/1/15 16:01
     * @param deliveryDTO
     * @return java.util.List<com.yimao.cloud.pojo.dto.order.OrderDeliveryDTO>
     */
    List<OrderDeliveryDTO> selectOrderDeliveryList(OrderDeliveryDTO deliveryDTO);


    Page<Object> deliveryLedgerExport(DeliveryConditionDTO dto);

    Page<Object> deliveryInfoExport(DeliveryConditionDTO dto);

    Page<Object> queryDeliveryList(DeliveryConditionDTO dto);

    Page<DeliveryInfoExportDTO> deliveryRecordLedgerExport(DeliveryConditionDTO dto);

    Page<DeliveryInfoExportDTO> deliveryRecordInfoExport(DeliveryConditionDTO dto);
}
