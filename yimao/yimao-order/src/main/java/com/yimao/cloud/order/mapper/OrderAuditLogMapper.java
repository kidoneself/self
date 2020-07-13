package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderAuditLog;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description   订单审核或处理记录
 * @author zhilin.he
 * @date 2019/2/14 16:58
 */
public interface OrderAuditLogMapper extends Mapper<OrderAuditLog> {


    Page<OrderAuditLogDTO> orderAuditLogList(AfterSalesConditionDTO dto);
}
