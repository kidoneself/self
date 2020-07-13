package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.refundManageExportDTO;


import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * @author Lizhqiang
 * @date 2019-09-19
 */
public interface ReimburseManageMapper extends Mapper<OrderSub> {


    Page<OrderSubDTO> onlineReimburseManagePage(OrderSubDTO query);

    Page<OrderSubDTO> refundRecord(OrderSubDTO dto);

    //线上线下退款导出
    Page<refundManageExportDTO> exportOnline(OrderSubDTO dto);


    //退款记录导出
    Page<refundManageExportDTO> exportRefund(OrderSubDTO dto);

    int onlineReimburseManageCount(@Param("typePay")Integer typePay);
}
