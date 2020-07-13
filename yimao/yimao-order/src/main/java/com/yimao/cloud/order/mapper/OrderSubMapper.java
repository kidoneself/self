package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderCountDTO;
import com.yimao.cloud.pojo.dto.order.OrderExportDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubDTO;
import com.yimao.cloud.pojo.dto.order.OrderSubListDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.WxOrderDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.station.ProductSalesStatusDTO;
import com.yimao.cloud.pojo.dto.station.ProductTabulateDataDTO;
import com.yimao.cloud.pojo.dto.station.ProductTwoCategoryPicResDTO;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.export.order.OrderBillExport;
import com.yimao.cloud.pojo.query.order.OrderBillQuery;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.vo.order.OrderBillVO;
import com.yimao.cloud.pojo.vo.station.OrderGeneralSituationVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhang Bo
 * @date 2018/11/8.
 */
public interface OrderSubMapper extends Mapper<OrderSub> {

    /**
     * @param orderConditionDTO 订单查询条件
     * @return java.lang.Object
     * @description 查询订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
    Page<OrderSubDTO> orderList(OrderConditionDTO orderConditionDTO);

    /**
     * @param orderConditionDTO 订单查询条件
     * @return java.lang.Object
     * @description 查询我的订单列表根据订单状态
     * @author zhilin.he
     * @date 2019/1/12 13:48
     */
//    Page<OrderSubListDTO> myOrderList(OrderConditionDTO orderConditionDTO);

    /**
     * @param orderSubDTO 订单信息
     * @return Integer
     * @description 修改订单型号和地址信息
     * @author zhilin.he
     * @date 2019/1/25 14:09
     */
    Integer updateOrderInfo(OrderSubDTO orderSubDTO);

    /**
     * @param orderSubDTO
     * @param ids
     * @return java.lang.Integer
     * @description 批量更新订单状态（批量取消订单）
     * @author zhilin.he
     * @date 2019/2/22 16:49
     */
    Integer updateOrderStatusBatch(@Param(value = "orderSubDTO") OrderSubDTO orderSubDTO, @Param(value = "ids") List<Long> ids);

    /**
     * @param ids 订单id集合
     * @return java.lang.Object
     * @description 批量删除订单
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    Integer updateOrderDeleted(@Param(value = "ids") List<Long> ids);

    /**
     * @param orderSubDTO 订单信息
     * @return java.lang.Object
     * @description 更新是否查看订单
     * @author zhilin.he
     * @date 2019/1/12 13:42
     */
    Integer updateOrderIsLook(OrderSubDTO orderSubDTO);

    /**
     * @param orderSubDTO
     * @return java.lang.Integer
     * @description 我的订单-水机订单-子订单子状态总数
     * @author zhilin.he
     * @date 2019/1/16 18:38
     */
    Integer selectOrderCountBySubStatus(OrderSubDTO orderSubDTO);

    /**
     * @param distributorId
     * @return java.lang.Integer
     * @description 查询经销商及其下属客户订单总数
     * @author zhilin.he
     * @date 2019/1/29 17:39
     */
    Integer selectOrderCountByDistributorId(@Param(value = "distributorId") Integer distributorId);

    /**
     * 统计 待发货统计 待出库订单统计 订单
     *
     * @return List
     * @author hhf
     * @date 2019/3/22
     */
    List<Map<String, Object>> countDeliveryOrder();

    /**
     * 有效销售总额,有效订单数
     *
     * @param sign 1-昨天
     * @return java.util.Map<java.lang.String                               ,                                                               java.lang.Object>
     * @author hhf
     * @date 2019/3/26
     */
    Map<String, Object> selectTotalBusiness(@Param(value = "sign") Integer sign);

    /**
     * 获取前五的一级类目产品销售金额和销售数量
     *
     * @param
     * @return java.util.List<com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO>
     * @author hhf
     * @date 2019/3/26
     */
    List<BusinessProfileDetailDTO> selectBusiness();

    /**
     * 成交笔数
     *
     * @return java.lang.Integer
     * @author hhf
     * @date 2019/3/27
     */
    Integer selectCount4Pay();

    /**
     * 下单数
     *
     * @return java.lang.Integer
     * @author hhf
     * @date 2019/3/27
     */
    Integer selectCount4Create();


    Page<WxOrderDTO> wxOrderList(@Param(value = "userId") Integer userId,
                                 @Param(value = "status") Integer status,
                                 @Param(value = "beginTime") String beginTime,
                                 @Param(value = "endTime") String endTime,
                                 @Param(value = "keys") String keys,
                                 @Param(value = "orderId") String orderId);


    Integer selectOrderCount(@Param(value = "userId") Integer userId, @Param(value = "status") Integer status);

    Integer auditCount(@Param("list") List<Integer> list, @Param("flag") int flag);

    Page<WxOrderDTO> auditList(@Param("list") List<Integer> list,
                               @Param("incomeList") List<String> incomeList,
                               @Param("flag") int flag,
                               @Param("beginTime") String beginTime,
                               @Param("endTime") String endTime,
                               @Param("keys") String keys,
                               @Param("orderKeys") String orderKeys,
                               @Param("orderId") String orderId);


    Page<OrderBillVO> listOrderBill(OrderBillQuery query);

    List<OrderBillExport> exportOrderBill(OrderBillQuery query);

    Page<OrderExportDTO> orderExportList(OrderConditionDTO query);

    Integer orderExportListCount(OrderConditionDTO query);

    Integer batchUpdateStatus(Map map);

    /**
     * 当前用户的不同订单状态下的订单数量
     */
    List<OrderCountDTO> selectUserOrderCountByStatus(@Param(value = "userId") Integer userId);

    OrderSub selectForComplete(@Param(value = "id") Long id);

    List<OrderSub> selectDeliveryOrderForComplete(@Param(value = "status") Integer status, @Param(value = "time") Date time);

    Integer countDistributorCompletedOrder(@Param(value = "distributorId") Integer distributorId, @Param(value = "date") Date date);

    Integer countUserCompletedOrder(@Param(value = "phone") String phone, @Param(value = "date") Date date);

    List<OrderSub> selectByMainOrderIdForAfterPay(@Param(value = "mainOrderId") Long mainOrderId);

    void timeout(@Param(value = "date") Date date, @Param(value = "status") Integer status);

    Integer selectCustomerOrderCount(@Param(value = "userId") Integer userId, @Param(value = "ids") List<Integer> ids);

    /**
     * 客户订单数量： 根据经销商ID和类型，查询不同状态下的数量
     *
     * @param distributorId          经销商ID
     * @param userId                 用户ID
     * @param firstProductCategoryId 类型
     * @param orderStatus            订单状态
     * @param userIdentity           是否是经销商
     * @return map
     */
    Integer customerOrderCountByDistributor(@Param("userId") Integer userId,
                                            @Param("distributorId") Integer distributorId,
                                            @Param("firstProductCategoryId") Integer firstProductCategoryId,
                                            @Param("orderStatus") Integer orderStatus,
                                            @Param("userIdentity") Integer userIdentity,
                                            @Param("queryType") Integer queryType,
                                            @Param("subDistributorId") Integer subDistributorId);

//    /**
//     * 客户订单数量： 根据客户ID和类型，查询不同状态下的数量
//     *
//     * @param userId                 经销商ID
//     * @param firstProductCategoryId 类型
//     * @param orderStatus            订单状态
//     * @return map
//     */
//    Integer customerOrderCountByUser(@Param("userId") Integer userId,
//                                     @Param("firstProductCategoryId") Integer firstProductCategoryId,
//                                     @Param("orderStatus") Integer orderStatus);

    int updateCheckSubOrderStatus(OrderSub updateSub);

    /****
     * 退款完成更新子单状态和退款日期
     * @param updateSub
     * @return
     */
    int updateSubStatus(OrderSub updateSub);


    /**
     * 我的订单 （新加）
     *
     * @param orderConditionDTO 参数
     * @return page
     */
    Page<OrderSubListDTO> myOrderSubList(OrderConditionDTO orderConditionDTO);

    /**
     * 我的客户订单 （新加）
     *
     * @param orderConditionDTO 参数
     * @return page
     */
    Page<OrderSubListDTO> myCustomerOrderSubList(OrderConditionDTO orderConditionDTO);

    List<OrderSubDTO> selectSubOrdersByMainOrderId(Long mainOrderId);

    Integer getProductCompanyIdByMainOrderId(@Param("mainOrderId") Long mainOrderId);

    int updateToWaitingAuditByMainOrderId(OrderSub order);

    /**
     * 有收益的客户订单数量
     *
     * @param userIdentity 1-自己下单 2-客户下单
     * @param userId       用户id
     * @param mid          经销商id
     * @return 数量
     */
    Integer myCustomerOrderCount(@Param("userIdentity") Integer userIdentity,
                                 @Param("userId") Integer userId,
                                 @Param("mid") Integer mid);

    /**
     * 超时未支付的水机订单
     *
     * @param date
     * @return
     */
    List<OrderSub> timeoutOrder(@Param(value = "date") Date date);

    boolean existsWithRefer(@Param(value = "refer") String refer);

    /**
     * 查询待发货实物商品订单
     * 这里的OrderSub发货时间取得不是订单的发货时间而是发货记录的创建时间（出库时间）
     *
     * @return
     */
    List<OrderSub> selectWaitSendOrder();

    Page<OrderBillExport> exportOrderBillPage(OrderBillQuery query);

    /**
     * 分销用户的订单数量
     *
     * @param userId
     * @return int
     */
    Integer vipUserCount(@Param(value = "userId") Integer userId);

    /**
     * 站务系统统计-流水-产品销量统计
     *
     * @param query
     * @return
     */
    List<FlowStatisticsDTO> getProductSaleData(StatisticsQuery query);

    List<FlowStatisticsDTO> getHraSaleData(StatisticsQuery query);

    List<FlowStatisticsDTO> getProductSalePicData(StatisticsQuery query);

    List<FlowStatisticsDTO> getHraSalePicData(StatisticsQuery query);

    List<ProductTabulateDataDTO> getProductTabulateData(StatisticsQuery query);

    List<ProductSalesStatusDTO> getProductSalesStatus(StatisticsQuery query);

    List<ProductTwoCategoryPicResDTO> getProductTwoCategoryPicRes(StatisticsQuery query);

    OrderGeneralSituationVO getOrderGeneralSituation(@Param("areas") Set<Integer> areas);

    List<FlowStatisticsDTO> getTotalProductAndHraPicData(StatisticsQuery query);

    Integer getAlreadyBuyCount(@Param("productId") Integer productId,
                               @Param("userId") Integer userId,
                               @Param("startTime") Date startTime,
                               @Param("endTime") Date endTime);

    List<OrderSub> listForCancelWhenOffshelf(@Param("productId") Integer productId);

    void cancelWhenOffshelf(@Param("id") Long id);

    /***
     * 计算产品销售排行数据
     * @param startTime
     * @param endTime
     * @return
     */
	List<SalePerformRankDTO> getProductOrderPerformRank(@Param("startTime")String startTime, @Param("endTime")String endTime);

	/***
	 * 产品销售额统计
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getProdSalesData(SalesStatsQueryDTO query);

    /***
	 * 交易订单数统计
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getTradeOrderData(SalesStatsQueryDTO query);

    /****
	 * 水机占比数据统计
	 * @param query
	 * @return
	 */
	List<SalesStatsDTO> getwaterModelPropData(SalesStatsQueryDTO query);


    /**
     * @param
     * @return java.util.List<com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO>
     * @description 经销商APP产品销售报表统计
     * @author Liu Yi
     * @date 2020/5/6 9:51
     */
    List<FlowStatisticsDTO> getOrderSalesHomeReport(SalesStatsQueryDTO query);
}
