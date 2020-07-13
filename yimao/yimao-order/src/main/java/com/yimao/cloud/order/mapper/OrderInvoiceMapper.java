package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceExportDTO;
import com.yimao.cloud.pojo.dto.order.OrderInvoiceQueryDTO;
import tk.mybatis.mapper.common.Mapper;

public interface OrderInvoiceMapper extends Mapper<OrderInvoice> {
    Page<OrderInvoiceDTO> pageQuery(OrderInvoiceQueryDTO query);

    Page<OrderInvoiceExportDTO> orderInvoiceExport (OrderInvoiceQueryDTO query);
}
