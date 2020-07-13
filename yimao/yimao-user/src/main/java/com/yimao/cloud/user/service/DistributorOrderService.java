package com.yimao.cloud.user.service;

import com.yimao.cloud.pojo.dto.order.AgentSalesOverviewDTO;
import com.yimao.cloud.pojo.dto.order.PayRecordDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesStatsResultDTO;
import com.yimao.cloud.pojo.dto.station.FlowStatisticsDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderAllInfoDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderDTO;
import com.yimao.cloud.pojo.dto.user.DistributorOrderExportDTO;
import com.yimao.cloud.pojo.dto.user.SalePerformRankDTO;
import com.yimao.cloud.pojo.dto.user.UserCompanyApplyDTO;
import com.yimao.cloud.pojo.query.station.StatisticsQuery;
import com.yimao.cloud.pojo.query.user.DistributorOrderQueryDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.station.DistributorOrderVO;
import com.yimao.cloud.user.po.Distributor;
import com.yimao.cloud.user.po.DistributorOrder;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 经销商订单业务管理
 *
 * @author Lizhqiang
 * @date 2018/12/17
 */


public interface DistributorOrderService {

    /**
     * @param
     * @return void
     * @description 后台新增订单
     * @author Liu Yi
     * @date 2019/8/23 14:32
     */
    DistributorOrder insertOrderBySystem(DistributorOrderDTO orderDTO);

    /**
     * @param
     * @return void
     * @description 注册类型订单(app)
     * @author Liu Yi
     * @date 2019/8/21 10:38
     */
    Map<String, Object> registerOrder(DistributorOrder distributorOrder, String mobile, UserCompanyApplyDTO userCompanyApplyDTO, String creator);

    /**
     * 更新经销商订单
     *
     * @param orderDTO
     * @return
     */
    void updateOrder(DistributorOrderDTO orderDTO);

    /**
     * @param
     * @return void
     * @description 续费订单
     * @author Liu Yi
     * @date 2019/8/23 14:32
     */
    Map<String, Object> renewOrder(DistributorOrderDTO orderDTO);

    /**
     * @param
     * @return void
     * @description 升级订单
     * @author Liu Yi
     * @date 2019/8/23 14:32
     */
    Map<String, Object> upgradeOrder(DistributorOrderDTO orderDTO);

    /**
     * 分页查询经销商订单   可带条件
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<DistributorOrderDTO> page(DistributorOrderQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * @param
     * @return java.util.List<com.yimao.cloud.pojo.dto.user.DistributorOrderDTO>
     * @description 导出经销商订单
     * @author Liu Yi
     * @date 2019/8/26 9:55
     */
    List<DistributorOrderExportDTO> listOrderExport(DistributorOrderQueryDTO query);


    /**
     * 经销商订单详情、及订单相关详情
     *
     * @param id
     * @return
     */
    DistributorOrderAllInfoDTO findDistributorOrderAllInfoByOrderId(Long id);

    /**
     * 经销商订单基本详情
     *
     * @param id
     * @return
     */
    DistributorOrderDTO findBasicDistributorOrderById(Long id);

    /**
     * @param
     * @return void
     * @description 提交支付凭证
     * @author Liu Yi
     * @date 2019/8/23 14:32
     */
    void submitCredential(Long id, Integer payType, String payCredential);

    /**
     * 获取注册、升级、续费价格
     *
     * @param origLevel
     * @param destLevel
     * @param distributorId 经销商id
     * @return
     */
    BigDecimal getOrderPrice(Integer origLevel, Integer destLevel, Integer distributorId, Integer orderType);

    /**
     * 待办事项统计（企业信息审核，支付审核）
     *
     * @return Map
     * @author hhf
     * @date 2019/3/23
     */
    Map<String, Long> distributorOrderOverview();

    /**
     * @return java.util.List<com.yimao.cloud.user.po.DistributorOrder>
     * @description 根据经销商账户查询经销商订单
     * @author Liu Yi
     * @date 14:53 2019/8/20
     **/
    List<DistributorOrderDTO> listByDistributorAccount(String distributorAccount, Integer orderType);

    /**
     * @param distributorId 经销商id
     * @return java.util.List<com.yimao.cloud.user.po.DistributorOrder>
     * @description 根据创建人（经销商）查询订单
     * @author Liu Yi
     * @date 14:53 2019/8/20
     **/
    List<DistributorOrderDTO> unfinishedOrderListByCreator(Integer distributorId);

    /**
     * @param orderType        订单类型1-升级 2-续费
     * @param distributorLevel 升级级别，续费时不用传
     * @return java.lang.String
     * @description 获取经销商升级或者续费提示信息
     * @author Liu Yi
     * @date 2019/8/23 13:31
     */
    String getDistributorOrderRemindMessage(Integer orderType, Integer distributorLevel);

    /**
     * @param
     * @return java.lang.String
     * @description 上传营业执照
     * @author Liu Yi
     * @date 2019/8/29 16:12
     */
    String uploadBusinessLicenseImage(MultipartFile multipartFile);

    /**
     * @param
     * @return void
     * @description 重新提交企业审核
     * @author Liu Yi
     * @date 2019/9/2 16:12
     */
    void renewCommitCompanyApply(UserCompanyApplyDTO dto);

    /**
     * @param record
     * @return void
     * @description 经销商订单支付回调
     * @author Liu Yi
     * @date 2019/9/16 17:49
     */
    void distributorOrderPayCallback(PayRecordDTO record);

    boolean upgradeValidityTime(Integer validityTime, Distributor distributor);

    /**
     * 完成注册订单
     *
     * @param order
     */
    void finishRegisterOrder(DistributorOrder order);

    /**
     * 完成续费订单
     *
     * @param order
     */
    void finishRenewOrder(DistributorOrder order);

    /**
     * 完成升级订单
     *
     * @param order
     */
    void finishUpgradeOrder(DistributorOrder order);

	FlowStatisticsDTO getDistributorOrderData(StatisticsQuery query);
	
    /**
     * 根据条件查询经销商订单列表（站务系统调用）
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVO<DistributorOrderVO> stationGetDistributorOrderInfo(DistributorOrderQueryDTO query, Integer pageNum, Integer pageSize);

    /****
     * 获取招商统计信息
     * @param query
     * @return
     */
    SalesStatsResultDTO getInvestmentSalesStats(SalesStatsQueryDTO query);

    /***
     * 根据年月日统计招商销售额
     * @param query
     * @return
     */
    List<SalesStatsDTO> getInvestmentSalesAmountStats(SalesStatsQueryDTO query);

    /***
     * 根据年月统计经销商的各类型增长趋势信息
     * @param query
     * @return
     */
	List<SalesStatsDTO> getDistributorIncreaseTrendStats(SalesStatsQueryDTO query);

    /***
	 * 根据日期获取获取销售业绩排行数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<SalePerformRankDTO> getDistributorPerformRank(String startTime, String endTime);

    AgentSalesOverviewDTO getOrderSalesHomeReport(SalesStatsQueryDTO query);

    List<SalesStatsDTO> getOrderSalesTotalReport(SalesStatsQueryDTO query);

    List<String> queryDistributorOrderTypeNames();
}
