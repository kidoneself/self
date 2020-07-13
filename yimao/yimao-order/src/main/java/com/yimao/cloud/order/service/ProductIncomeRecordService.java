package com.yimao.cloud.order.service;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRuleDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRulePartDTO;
import com.yimao.cloud.pojo.dto.order.IncomeStatsQueryDTO;
import com.yimao.cloud.pojo.dto.order.IncomeStatsResultDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeRecordPartDTO;
import com.yimao.cloud.pojo.dto.order.SalesProductDTO;
import com.yimao.cloud.pojo.dto.product.ProductCostDTO;
import com.yimao.cloud.pojo.dto.product.ProductDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeGrandTotalVO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Liu Yi
 * @date 2019/1/23.
 */
public interface ProductIncomeRecordService {

    /**
     * 产品收益分配
     *
     * @param orderId 订单id
     */
    void allotSellIncome(Long orderId);

    // /**
    //  * 分配收益明细
    //  * 根据订单号和收益记录分配收益明细
    //  */
    // void allotSellIncomePart(OrderSubDTO subDTO, IncomeRuleDTO incomeRule, ProductIncomeRecord record);

    /**
     * 创建收益分配记录（明细）
     *
     * @param record   收益分配记录
     * @param rule     收益分配规则
     * @param rulePart 收益分配规则明细
     */
    ProductIncomeRecordPart createProductIncomeRecordPart(ProductIncomeRecord record, IncomeRuleDTO rule, IncomeRulePartDTO rulePart);

    /**
     * 设置结算状态
     *
     * @param id
     * @param status
     */
    void setSettlementIncomeStatus(Integer id, Integer status);

    /**
     * 查询产品收益明细列表
     *
     * @param productIncomeQueryDTO 查询dto
     * @param pageNum               第几页
     * @param pageSize              每页长度
     */
    PageVO<ProductIncomeVO> pageQueryProductIncome(ProductIncomeQueryDTO productIncomeQueryDTO, Integer pageNum, Integer pageSize);

    /**
     * 根据订单id查询收益记录
     *
     * @param orderId 订单id
     */
    List<ProductIncomeRecordPartDTO> getProductIncomeRecordPartList(Integer orderId);

    /**
     * 根据id查询收益记录
     *
     * @param id id
     */
    IncomeRecordResultDTO getProductIncomeById(Integer id);

    /**
     * 更新产品收益记录状态为已完成
     *
     * @param orderId
     * @param orderCompleteTime
     */
    void changeProductIncomeRecordToComplete(Long orderId, Date orderCompleteTime);

    /**
     * 退单（子订单一次性全退）
     *
     * @param orderId 订单id
     */
    void refundOrder(Long orderId);

    /**
     * 退货（退一部分）
     *
     * @param orderId 订单id
     * @param num     退货数量
     */
    void refundGoods(Long orderId, Integer num);

    /**
     * 收益管理查询
     *
     * @param userId     用户ID
     * @param incomeType 收益类型 1-产品收益 2-续费收益
     * @return java.util.Map<java.lang.String ,   java.lang.Object>
     * @author hhf
     * @date 2019/4/9
     */
    Map<String, Object> listIncomeByProductType(Integer userId, Integer incomeType);

    /**
     * 查询收益明细
     *
     * @param userId      用户ID
     * @param productType 产品类型
     * @param year        年
     * @param month       月
     * @param incomeType  收益类型 1-产品收益 2-续费收益
     * @param pageNum     分页数
     * @param pageSize    分页大小
     * @return
     * @author hhf
     * @date 2019/4/9
     */
    Map<String, Object> listIncomeDetail(Integer userId, Integer productType, String year, String month, Integer incomeType, Integer pageNum, Integer pageSize);

    /**
     * 产品收益明细导出
     *
     * @author hhf
     * @date 2019/5/9
     */
    Page<IncomeExportDTO> productIncomeExport(ProductIncomeQueryDTO query, Integer pageNum, Integer pageSize);

    // Integer productIncomeExportCount(ProductIncomeQueryDTO query);

    Integer countSaleOrder(Integer userSaleId);

    Map<String, Object> listDistributorIncomeDetail(Integer distributorId, Integer productType, String year, String month, Integer incomeType, Integer pageNum, Integer pageSize);

    /**
     * 将受益标记为已完成
     *
     * @param order 订单
     */
    void completeIncome(OrderSub order);

    /**
     * 经销商app产品收益
     *
     * @author Liu Yi
     * @date 2019/8/9
     */
    ProductIncomeGrandTotalVO productIncomeGrandTotalforApp(Integer incomeType);


    // app经销商收益概况(按日和按月)
    Map<String, Object> productIncomeReportOverviewforApp(Date dateTime, Integer type, Integer incomeType);

    // app经销商收益金额明细列表(按日和按月)
    Map<String, Object> productIncomeReportOverviewListforApp(Date dateTime, Integer type, Integer incomeType, Integer pageNum, Integer pageSize);


    //app经销商收益金额明细列表
    Map<String, Object> productIncomeListForApp(Integer terminal, Integer productFirstCategoryId, Integer distributorId, Integer queryType, Integer status, Integer timeType, Integer incomeType, Date beginDate, Date endDate, Integer pageNum, Integer pageSize);

    //app经销商收益收益详情
    Map<String, Object> productIncomeDetailForApp(Integer id, Integer incomeType);

    List<ProductIncomeRecordDTO> getProductIncomeRecord(Integer id);

    List<SalesProductDTO> getSaleProductListById(Integer id);

    void editProductIncomeRecordFee(WorkOrder workOrder, ProductDTO product, ProductCostDTO newCost);

    void editProductIncomeRecordProductInfo(WorkOrder workOrder, ProductDTO product);

    List<IncomeStatsResultDTO> productIncomeStats(IncomeStatsQueryDTO query);
}
