package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.ProductIncomeRecord;
import com.yimao.cloud.pojo.dto.order.IncomeExportDTO;
import com.yimao.cloud.pojo.dto.order.IncomeRecordResultDTO;
import com.yimao.cloud.pojo.dto.order.ProductIncomeQueryDTO;
import com.yimao.cloud.pojo.dto.order.SalesProductDTO;
import com.yimao.cloud.pojo.vo.order.ProductIncomeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductIncomeRecordMapper extends Mapper<ProductIncomeRecord> {

    Page<ProductIncomeVO> queryProductIncomeList(ProductIncomeQueryDTO productIncomeQueryDTO);

    Page<ProductIncomeVO> queryRenewProductIncomeList(ProductIncomeQueryDTO productIncomeQueryDTO);
//
//    Double getUserSaleTotalIncome(@Param("userId") Long userId);
//
//    List<ProductSaleIncome> getTotalIncomeByProductType(@Param("userId") Long userId);
//
//    List<ProductSaleIncome> queryDistributorIncome(@Param("minTime") String minTime,
//                                      @Param("maxTime") String maxTime,
//                                      @Param("province") String province,
//                                      @Param("city") String city,
//                                      @Param("region") String region);
//
//    List<SaleDataDto> queryDistributorIncomeGroupByDateAndOrderType(@Param("minTime") String minTime,
//                                                                    @Param("maxTime") String maxTime,
//                                                                    @Param("province") String province,
//                                                                    @Param("city") String city,
//                                                                    @Param("region") String region);
//
//    List<SaleDataDto> queryDistributorIncomeGroupByStationAndOrderType(@Param("minTime") String minTime,
//                                                                       @Param("maxTime") String maxTime,
//                                                                       @Param("province") String province,
//                                                                       @Param("city") String city,
//                                                                       @Param("region") String region);
//
//
//    List<Sale> queryUserSaleIncome(@Param("minTime") String minTime,
//                                   @Param("maxTime") String maxTime,
//                                   @Param("province") String province,
//                                   @Param("city") String city,
//                                   @Param("region") String region);
//
//    List<SaleDataDto> queryUserSaleIncomeGroupByDateAndOrderType(@Param("minTime") String minTime,
//                                                                 @Param("maxTime") String maxTime,
//                                                                 @Param("province") String province,
//                                                                 @Param("city") String city,
//                                                                 @Param("region") String region);
//
//    List<SaleDataDto> queryUserSaleIncomeGroupByStationAndOrderType(@Param("minTime") String minTime,
//                                                                    @Param("maxTime") String maxTime,
//                                                                    @Param("province") String province,
//                                                                    @Param("city") String city,
//                                                                    @Param("region") String region);
//
//    List<Sale> getUserSaleCount(@Param("minTime") String minTime,
//                                @Param("maxTime") String maxTime,
//                                @Param("province") String province,
//                                @Param("city") String city,
//                                @Param("region") String region);
//
//    List<UserInfoDto> getUserSaleCountByStation(@Param("minTime") String minTime,
//                                                @Param("maxTime") String maxTime,
//                                                @Param("province") String province,
//                                                @Param("city") String city,
//                                                @Param("region") String region);
//
    /*List<ProductIncomeRecord> getUserSaleWithdrawIncome(@Param("userSaleId") Integer userSaleId,
                                                        @Param("hasBeenWithdrawn") Integer hasBeenWithdrawn,
                                                        @Param("auditStatus") Integer auditStatus,
                                                        @Param("partnerTradeNoList") List<String> partnerTradeNoList);

    Page<ProductIncomeRecord> saleList();*/

    IncomeRecordResultDTO selectProductIncomeByPrimaryKey(Integer id);

    Page<IncomeExportDTO> productIncomeExport(ProductIncomeQueryDTO query);

    Integer productIncomeExportCount(ProductIncomeQueryDTO query);

    ProductIncomeRecord selectForComplete(@Param("orderId") Long orderId, @Param("incomeType") Integer incomeType);

    List<SalesProductDTO> getSaleProductListById(@Param("id") Integer id);

    ProductIncomeRecord selectProductIncomeRecordByOrderId(@Param("subOrderId") Long subOrderId, @Param("incomeType") Integer incomeType);

    Page<IncomeExportDTO> productRenewIncomeExport(ProductIncomeQueryDTO query);

    Integer productRenewIncomeExportCount(ProductIncomeQueryDTO query);

    void updateStatusAndSettlementMonth(@Param("id") Integer id, @Param("status") Integer status, @Param("settlementMonth") String settlementMonth);

    void updateStatusAndSettlementMonthByDistributorId(@Param("distributorId") Integer distributorId,
                                                       @Param("oldStatus") Integer oldStatus,
                                                       @Param("newStatus") Integer newStatus,
                                                       @Param("settlementMonth") String settlementMonth);

    boolean existsWithOrderId(@Param("orderId") String orderId, @Param("incomeType") Integer incomeType);
}