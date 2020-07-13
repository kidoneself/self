package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.dto.DistributorIncomeDTO;
import com.yimao.cloud.order.po.ProductIncomeRecordPart;
import com.yimao.cloud.pojo.dto.order.*;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProductIncomeRecordPartMapper extends Mapper<ProductIncomeRecordPart> {
    /**
     * @description 根据收益id获取收益对象信息
     * @author Liu Yi
     * @date 2019/1/29
     */
    List<ProductIncomeRecordPartDTO> getPartByProductIncomeRecordId(Integer productIncomeRecordId);

    List<IncomeRecordPartResultDTO> getPartByIncomeRecordId(Integer productIncomeRecordId);

    Integer batchInsert(List<ProductIncomeRecordPart> list);

    /**
     * 收益管理查询
     */
    List<DistributorIncomeDTO> listUserIncomeByProductType(@Param("userId") Integer userId,
                                                           @Param("distributorId") Integer distributorId,
                                                           @Param("withdrawStatus") Integer withdrawStatus,
                                                           @Param("isVipUser") boolean isVipUser,
                                                           @Param("isDistributor") boolean isDistributor,
                                                           @Param("incomeType") Integer incomeType);

    // 用户userId查询
    BigDecimal listUserTotalIncome(@Param("userId") Integer userId,
                                   @Param("distributorId") Integer distributorId,
                                   @Param("orderCompleteStatus") Integer orderCompleteStatus,
                                   @Param("year") String year,
                                   @Param("month") String month,
                                   @Param("productType") Integer productType,
                                   @Param("isVipUser") boolean isVipUser,
                                   @Param("isDistributor") boolean isDistributor,
                                   @Param("incomeType") Integer incomeType);

    // 经销商Id查询
    BigDecimal listDistributorTotalIncome(@Param("distributorId") Integer distributorId,
                                          @Param("orderCompleteStatus") Integer orderCompleteStatus,
                                          @Param("year") String year,
                                          @Param("month") String month,
                                          @Param("productType") Integer productType,
                                          @Param("sonId") Integer sonId);

    // 用户userId查询
    Page<UserIncomeDTO> listUserIncomeDetail(@Param("userId") Integer userId,
                                             @Param("distributorId") Integer distributorId,
                                             @Param("orderCompleteStatus") Integer orderCompleteStatus,
                                             @Param("year") String year,
                                             @Param("month") String month,
                                             @Param("productType") Integer productType,
                                             @Param("isVipUser") boolean isVipUser,
                                             @Param("isDistributor") boolean isDistributor,
                                             @Param("incomeType") Integer incomeType);

    // 经销商Id查询
    Page<UserIncomeDTO> listDistributorIncomeDetail(@Param("distributorId") Integer distributorId,
                                                    @Param("orderCompleteStatus") Integer orderCompleteStatus,
                                                    @Param("year") String year,
                                                    @Param("month") String month,
                                                    @Param("productType") Integer productType,
                                                    @Param("sonId") Integer sonId,
                                                    @Param("incomeType") Integer incomeType);

    List<ProductIncomeRecordPartDTO> getUserSaleWithdrawIncome(@Param("userId") Integer userId,
                                                               @Param("hasBeenWithdrawn") Integer hasBeenWithdrawn,
                                                               @Param("auditStatus") Integer auditStatus,
                                                               @Param("incomeType") Integer incomeType,
                                                               @Param("partnerTradeNoList") List<String> partnerTradeNoList);

    Integer countSaleOrder(@Param("userSaleId") Integer userSaleId);

    BigDecimal getProductIncomeGrandTotalforApp(@Param("distributorId") Integer distributorId,
                                                @Param("userId") Integer userId,
                                                @Param("userType") Integer userType,
                                                @Param("status") Integer status,
                                                @Param("type") Integer type,
                                                @Param("incomeType") Integer incomeType,
                                                @Param("createTime") Date createTime);

    List<Map<String, Object>> productIncomeReportOverviewforApp(@Param("distributorId") Integer distributorId,
                                                                @Param("userId") Integer userId,
                                                                @Param("userType") Integer userType,
                                                                @Param("createTime") Date createTime,
                                                                @Param("type") Integer type,
                                                                @Param("incomeType") Integer incomeType);

    List<Map<String, Object>> renewIncomeReportOverviewforApp(@Param("distributorId") Integer distributorId,
                                                              @Param("userId") Integer userId,
                                                              @Param("userType") Integer userType,
                                                              @Param("createTime") Date createTime,
                                                              @Param("type") Integer type,
                                                              @Param("incomeType") Integer incomeType);

    BigDecimal productIncomeTotalMoney(@Param("distributorId") Integer distributorId,
                                       @Param("userId") Integer userId,
                                       @Param("userType") Integer userType,
                                       @Param("createTime") Date createTime,
                                       @Param("type") Integer type,
                                       @Param("incomeType") Integer incomeType);

    Page<Map<String, Object>> productIncomeListForApp(@Param("distributorId") Integer distributorId,
                                                      @Param("sonId") Integer sonId,
                                                      @Param("queryType") Integer queryType,
                                                      @Param("userId") Integer userId,
                                                      @Param("userType") Integer userType,
                                                      @Param("terminal") Integer terminal,
                                                      @Param("productFirstCategoryId") Integer productFirstCategoryId,
                                                      @Param("status") Integer status,
                                                      @Param("startTime") Date startTime,
                                                      @Param("endTime") Date endTime,
                                                      @Param("type") Integer type,
                                                      @Param("incomeType") Integer incomeType,
                                                      @Param("createTime") Date createTime);

    //获取指定日期的总金额
    BigDecimal incomeMoneyForDate(@Param("distributorId") Integer distributorId,
                                  @Param("sonId") Integer sonId,
                                  @Param("userId") Integer userId,
                                  @Param("userType") Integer userType,
                                  @Param("terminal") Integer terminal,
                                  @Param("productFirstCategoryId") Integer productFirstCategoryId,
                                  @Param("status") Integer status,
                                  @Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("type") Integer type,
                                  @Param("incomeType") Integer incomeType,
                                  @Param("createTime") Date createTime);

    Map<String, Object> productIncomeDetailForApp(@Param("id") Integer id,
                                                  @Param("distributorId") Integer distributorId,
                                                  @Param("userId") Integer userId,
                                                  @Param("userType") Integer userType);

    Map<String, Object> renewIncomeDetailForApp(@Param("id") Integer id,
                                                @Param("distributorId") Integer distributorId,
                                                @Param("userId") Integer userId,
                                                @Param("userType") Integer userType);

    ProductIncomeRecordPart selectVipUserIncome(@Param("mainOrderId") Long mainOrderId,
                                                @Param("orderId") String orderId,
                                                @Param("subjectCode") String subjectCode);

    boolean existsIncomeWithUserId(@Param("userId") Integer userId);

    /***
     * 收益统计(2.1版本)
     * @param query
     * @return
     */
    List<IncomeStatsResultDTO> productIncomeStatInfo(IncomeStatsQueryDTO query);

    /**
     * 会员用户的收益金额
     *
     * @param vipUserId 会员用户
     * @param recordId  记录id
     * @return 金额
     */
    BigDecimal vipUserIncomeMoneyByPartId(@Param("vipUserId") Integer vipUserId,
                                          @Param("recordId") Integer recordId);

    ProductIncomeRecordPart selectPartIdBySubOrderId(@Param("subOrderId") Long subOrderId);
    
    /***
     * 安装工转让，更新收益数据
     * @param pirp
     * @return
     */
	int updateIncomeForEngineer(ProductIncomeRecordPartDTO pirp);
}