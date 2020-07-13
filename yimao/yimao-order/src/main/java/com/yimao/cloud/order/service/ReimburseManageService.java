package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.hra.HraExportPhysicalDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.refundManageExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

/**
 * @author Lizhqiang
 * @date 2019-09-18
 */
public interface ReimburseManageService {
    PageVO<OrderSubDTO> onlineReimburseManagePage(Integer pageNum, Integer pageSize, OrderSubDTO dto);

    List<refundManageExportDTO> exportReimburse(OrderSubDTO dto);

    List<refundManageExportDTO> exportRefund(OrderSubDTO dto);
}
