package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderPayCheck;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import tk.mybatis.mapper.common.Mapper;

public interface OrderPayCheckMapper extends Mapper<OrderPayCheck> {

	Page<OrderPayCheckDTO> selectPayCheckRecordList(OrderPayCheckDTO orderPayCheckDTO);

	Page<OrderPayCheckDTO> selectPayCheckRecordListExport(OrderPayCheckDTO orderPayCheckDTO);

	OrderPayCheckDTO selectOrderMainPayCheckRecordInfo(Integer id);

	OrderPayCheckDTO selectOrderRenewPayCheckRecordInfo(Integer id);

	OrderPayCheckDTO selectWorkOrderPayCheckRecordInfo(Integer id);
}
