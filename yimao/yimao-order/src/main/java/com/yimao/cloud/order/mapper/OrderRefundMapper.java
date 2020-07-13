package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface OrderRefundMapper extends Mapper<AfterSalesOrder> {

    /**
     * @description   查询售后订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @return java.lang.Object
     */
    Page<OrderSalesInfoDTO> orderSalesList(OrderConditionDTO orderConditionDTO);

    /**
     * @description   查询订单售后详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    OrderSalesInfoDTO orderRefundInfo(@Param(value = "id")Long id);


    /**
     * @description   查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    OrderSalesInfoDTO orderRefundAuditInfo(@Param(value = "id")Long id);

    /**
     * @description   关闭退货
     * @author zhilin.he
     * @date 2019/1/28 15:49
     * @param id
     * @return void
     */
    int updateOrderSalesById(Long id);

    /**
     * @description   查询提交物流列表根据订单售后id集合
     * @author zhilin.he
     * @date 2019/1/11 13:56
     * @param ids   订单售后id集合
     * @return java.lang.Object
     */
    Page<OrderSalesInfoDTO> orderLogisticSubmitList(@Param(value = "ids") List<Long> ids);

    /**
     * @description   查询售后订单审核记录或处理记录列表
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @return java.lang.Object
     */
    Page<OrderSalesInfoDTO> orderRefundAuditList(OrderConditionDTO orderConditionDTO);

    /**
     * @description   财务线上退款审核列表
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @return java.lang.Object
     */
    Page<OrderSalesInfoDTO> financeRefundOnlineList(OrderConditionDTO orderConditionDTO);


}
