package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.pojo.dto.order.OrderDTO;
import com.yimao.cloud.pojo.dto.order.OrderMainDTO;
import com.yimao.cloud.pojo.dto.order.OrderPayCheckDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderQueryDTO;
import com.yimao.cloud.pojo.dto.user.DistributorDTO;
import com.yimao.cloud.pojo.vo.PageVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface OrderMainService {

    /**
     * 下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param type      1-单件商品下单；2-购物车下单
     */
    OrderMain createOrder(OrderDTO orderInfo, Integer type);

    /**
     * 根据主订单号查询主订单
     *
     * @param orderId 主订单号（支付订单号）
     */
    OrderMain findById(Long orderId);

    /**
     * 更新主订单
     *
     * @param mainOrder 主订单（支付订单）
     */
    Integer update(OrderMain mainOrder);

    /**
     * M卡下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param userId    下单用户ID
     */
    OrderMainDTO createHRAOrder(OrderDTO orderInfo, Integer userId);

    /**
     * M卡批量下单
     *
     * @param orderInfo 前端传递的订单信息
     * @param userId    下单用户ID
     */
    OrderMainDTO batchCreateHRAOrder(OrderDTO orderInfo, Integer userId);

    PageVO<OrderMainDTO> orderMainPayCheckList(Integer pageNum, Integer pageSize, OrderMainDTO query);

    PageVO<WorkOrderDTO> orderDeliveryPayCheckList(Integer pageNum, Integer pageSize, WorkOrderQueryDTO query);

    List<OrderSub> orderMainPayCheckSingle(String id, Boolean pass, String reason, String adminName, Integer payTerminal, String userPhone);

    List<OrderMainDTO> orderMainPayCheckExport(OrderMainDTO query);

    List<WorkOrderDTO> orderDeliveryPayCheckListExport(WorkOrderQueryDTO query);

    PageVO<OrderPayCheckDTO> orderPaycheckRecordList(Integer pageNum, Integer pageSize, OrderPayCheckDTO orderPayCheckDTO);

    /**
     * 根据订单号查询产品公司ID
     *
     * @param id 主订单号/支付订单号
     */
    Integer getProductCompanyIdByOutTradeNo(Long id);

    OrderPayCheckDTO findPayCheckRecordInfo(Integer id, Integer orderType);

    void createHraCard(List<OrderSub> hraSubOrderList);
}
