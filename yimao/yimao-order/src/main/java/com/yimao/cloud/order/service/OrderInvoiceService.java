package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.OrderInvoiceExportDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.util.List;

public interface OrderInvoiceService {
    PageVO<OrderInvoiceDTO> pageQueryInvoice(OrderInvoiceQueryDTO query, Integer pageNum, Integer pageSize);

    void save(String creator, OrderInvoice orderInvoice);

    OrderInvoiceDTO getInvoiceById(Integer id);

    /**
     * 续费工单--确认开票
     */
    void confirmInvoice(String renewId);

    /**
     * 续费工单--编辑发票
     */
    void updateVerify(OrderInvoiceDTO dto);

    /**
     * 根据订单号查询，是否存在开票信息
     *
     * @param orderId 订单ID
     * @return
     */
    OrderInvoice getInvoiceByOrderId(Long orderId);

    /**
     * 查询开票表中是否存在相同订单的数据
     *
     * @param mainOrderId
     * @param orderId
     * @return true 已存在  false 不存在
     */
    Boolean checkExistByOrderId(Long mainOrderId, Long orderId);

    void updateInvoice(OrderInvoice orderInvoice);
}
