package com.yimao.cloud.order.service;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.PayRecord;
import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderProductCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubListDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.order.WorkOrderReqDTO;
import com.yimao.cloud.pojo.dto.order.WxOrderDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.ProductTabulateDataDTO;
import com.yimao.cloud.pojo.dto.user.UserDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.OrderBillVO;
import com.yimao.cloud.pojo.vo.station.OrderGeneralSituationVO;
import com.yimao.cloud.pojo.vo.station.ProductSalesStatusAndTwoCategoryPicResVO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface OrderSubService {

    /**
     * 获取子订单信息（带订单详情）
     *
     * @param id 子订单号
     */
    OrderSubDTO findFullOrderById(Long id);

    /**
     * 后台管理系统查询订单详情
     *
     * @param id
     */
    OrderSubDTO getOrderDetailById(Long id);

    /**
     * @param orderConditionDTO 订单查询条件
     * @param pageNum           当前页
     * @param pageSize          每页显示条数
     * @description 查询订单列表, 根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/11 13:56
     */
    PageVO<OrderSubDTO> orderList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

//    /**
//     * @param orderConditionDTO 订单查询条件
//     * @param pageNum           当前页
//     * @param pageSize          每页显示条数
//     * @description 查询经销商app-我的订单列表,根据订单查询条件
//     * @author zhilin.he
//     * @date 2019/1/11 13:56
//     */
//    PageVO<OrderSubListDTO> myOrderList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

    /**
     * 查询订单详情
     *
     * @param id
     */
    OrderSubDTO findOrderById(Long id);

    /**
     * 我的订单--查询订单详情
     */
    OrderSubListDTO getMyOrderDetailById(Long id, Integer operationType, List<Long> ids, Integer subType);

    /**
     * 删除订单（逻辑删除）
     *
     * @param userName 用户姓名
     * @param orderId  订单号
     */
    void deleteOrderSub(String userName, Long orderId);

    /**
     * 根据主订单ID查询子订单列表
     *
     * @param mainOrderId
     */
    List<OrderSub> findOrdersByMainOrderId(Long mainOrderId);

    /**
     * @param orderSubDTO
     * @param orderIds
     * @description 批量更新订单状态（批量取消订单）
     * @author zhilin.he
     * @date 2019/2/22 16:49
     */
    JSONObject updateOrderStatusBatch(OrderSubDTO orderSubDTO, List<Long> orderIds);

    /**
     * 批量删除订单
     *
     * @param orderIds
     */
    void updateOrderDeleted(List<Long> orderIds, String userName);

    /**
     * @param orderSubDTO
     * @description 我的订单-查询主订单信息和统计水机状态数量
     * @author zhilin.he
     * @date 2019/1/16 16:49
     */
    Map<String, Object> orderNumInfo(OrderSubDTO orderSubDTO);

    /**
     * 查询订单对账列表
     *
     * @param query    查询条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     */
    PageVO<OrderBillVO> queryOrderBillList(OrderBillQuery query, Integer pageNum, Integer pageSize);

    /**
     * 订单对账：汇总导出
     *
     * @param query 查询条件
     */
    List<OrderBillExport> exportOrderBill(OrderBillQuery query);

    /**
     * 获取经销商及其下属客户订单总数
     *
     * @param distributorId 经销商Id
     * @author hhf
     * @date 2019/1/26
     */
    Integer getOrderCountByUserId(Integer distributorId);

    void updateOrderInfo(OrderSubDTO subOrder);


    PageVO<WxOrderDTO> wxOrderList(Integer userId, Integer status, String beginTime, String endTime, String keys, String orderId, Integer pageNum, Integer pageSize);

    Integer selectOrderCount(Integer userId, Integer status);

    /**
     * @param userDTO
     * @Description: 我的e家：客户订单数量
     * @author ycl
     * @Return: java.lang.Integer
     * @Create: 2019/4/23 10:42
     */
    Integer auditCount(UserDTO userDTO);

    /**
     * @param userId
     * @param orderKeys
     * @param beginTime
     * @param endTime
     * @param keys
     * @param orderId
     * @param pageNum
     * @param pageSize
     * @Description: 查询客户订单列表
     * @author ycl
     * @Create: 2019/4/22 14:58
     */
    PageVO<WxOrderDTO> auditList(Integer userId, String orderKeys, String beginTime, String endTime, String keys, String orderId, Integer pageNum, Integer pageSize);

    Integer orderExportListCount(OrderConditionDTO query);

    /**
     * 将订单设置为已完成
     *
     * @param id 子订单号
     */
    void completeOrder(Long id);

    /**
     * @Author ycl
     * @Description
     * @Date 15:07 2019/6/27
     * @Param 设置订单详情主体信息
     **/
    void fillHraCardSalesObject(Integer stationId, Long orderId);

    boolean checkOrderByMobile(String mobile);

    /**
     * 当前用户的不同订单状态下的订单数量
     */
    List<OrderCountDTO> checkUserOrderByStatus(Integer userId);

    /**
     * 根据累计的水机、健康产品已完成的工单数，发放HRA优惠卡
     *
     * @param distributorId 经销商e家号
     * @param phone         手机号
     * @param userType      用户类型
     */
    Integer countCompletedOrderFromDate(Integer distributorId, String phone, Integer userType);

    /**
     * 将超时未支付的订单状态设置为【交易关闭】
     *
     * @param date 创建时间比较值
     */
    void timeout(Date date);

    Integer selectCustomerOrderCount(Integer userId);


    List<OrderProductCountDTO> customerOrderCountByType(Integer userId, Integer queryType, Integer subDistributorId);

    /****
     * 退款完成更新子单状态
     * @param orderSub
     * @return
     */
    int updateSubStatus(OrderSub orderSub);


    /**
     * 我的订单(app端独有)
     *
     * @param orderConditionDTO 参数
     * @param pageNum           当前页
     * @param pageSize          每页数
     * @return page
     */
    PageVO<OrderSubListDTO> myOrderSubList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

    /**
     * 我的客户订单(app端独有)
     *
     * @param orderConditionDTO 参数
     * @param pageNum           当前页
     * @param pageSize          每页数
     * @return
     */
    PageVO<OrderSubListDTO> myCustomerOrderList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

    List<OrderSub> timeoutOrder(Date date);

    void payCallback(PayRecord record);

    FlowStatisticsDTO getProductAndHraSaleData(StatisticsQuery query);

    List<ProductTabulateDataDTO> getProductTabulateData(StatisticsQuery query);

    ProductSalesStatusAndTwoCategoryPicResVO getProductSalesStatusAndTwoCategoryPicRes(StatisticsQuery query, String categoryName);

    OrderGeneralSituationVO getOrderGeneralSituation(Set<Integer> areas, List<Integer> engineerIds, List<Integer> distributorIds, Integer type);

    void cancelOrderWhenOffshelf(Integer productId);

    /**
     * @param
     * @description 经销商app-经营报表-汇总统计
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query);

    /**
     * @param
     * @description 经销商app-经营报表-累计销售金额统计表
     * @author Liu Yi
     * @date 2020/4/27 15:21
     */
    List<SalesStatsDTO> getOrderSalesTotalReport(SalesStatsQueryDTO query);

    SalesStatsResultDTO getProductSaleStats(SalesStatsQueryDTO query);

	List<SalesStatsDTO> getProductSaleAmountStats(SalesStatsQueryDTO query);

	List<SalesStatsDTO> getTradeSucOrderStats(SalesStatsQueryDTO query);
	
	/***
	 * 安装工退单--更新子订单信息
	 * @param req
	 * @param subOrderId
	 * @return
	 */
	void updateOrderStatusForEngineer(WorkOrderReqDTO req, Long subOrderId);

}
