package com.yimao.cloud.order.service;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderDelivery;
import com.yimao.cloud.pojo.dto.order.*;
import com.yimao.cloud.pojo.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Chen Hui Yang
 * @date 2018/12/27
 */
public interface OrderDeliveryService {
    void add(OrderDelivery orderDelivery);

    Object importExcel(MultipartFile multipartFile);

    Object query(String logisticsNo, Long orderId);

    PageVO<DeliveryInfoDTO> list(DeliveryDTO deliveryDTO, Integer pageNum, Integer pageSize);

    void setDelivery(Long id);

    void setBatchDelivery(List<Long> ids);

    PageVO<OrderDeliveryRecordDTO> deliveryRecordList(Integer pageNum, Integer pageSize, String orderId, String logisticsNo, String startTime, String endTime, Integer userId, String addreessName, Integer terminal);

    Page<Object> deliveryExport(Integer exportType, DeliveryConditionDTO dto);

    DeliveryDetailInfoDTO findDeliveryDetail(Integer id);

    /*List<DeliveryInfoExportDTO> queryDeliveryRecordList(DeliveryConditionDTO dto);*/
}
