package com.yimao.cloud.order.mapper;

import com.github.pagehelper.Page;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.pojo.dto.order.*;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

/**
 *
 * @author hhf
 * @date 2019/3/12
 */
public interface AfterSalesOrderMapper  extends Mapper<AfterSalesOrder> {

    Page<AfterSalesOrderDTO> refundAudit(AfterSalesOrderQueryDTO dto);

    Page<AfterSalesOrderDTO> refundRecord(AfterSalesOrderQueryDTO dto);

    Page<AfterSalesOrderDTO> refundLog(Long orderId, Integer operation, String startTime, String endTime);

    /**
     * 统计线上线下待审核订单
     *
     * @param sign 1-线上 2-线下
     * @return java.lang.Long
     * @author hhf
     * @date 2019/3/23
     */
    Long countReturn4Audit(@Param("sign") Integer sign);

    Page<AfterSalesOrderDTO> rentalGoodsList(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportRentalGoods(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportAuditedRentalGoods(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportAuditedRecord(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportAllMatterGoods(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportAuditMatterGoods(AfterSalesConditionDTO dto);

    Page<RentalGoodsExportDTO> exportMatterGoodsRecord(AfterSalesConditionDTO dto);
    
    /****
     * 更新售后单状态
     * @param afterSalesOrder
     * @return
     */
    int updateAfterSalesOrderStatus(AfterSalesOrder afterSalesOrder);
    
    /***
     * 查询退款详情
     * @param id
     * @return
     */
	RefundDetailDTO getRefunddetailById(@Param("id") Long id);
   /* 退款退货订单统计
    *
    * @param salesType   售后类型：1.取消订单退款（未收货），2.申请退货退款（已收货）
    * @param productType 商品类型（大类）:1实物商品，2电子卡券，3租赁商品
    * @param status      状态:1-待审核(物资)   2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功
    * @param auditStatus 400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）
    * @return java.lang.Long
    * @author hhf
    * @date 2019/3/23
    */
	Integer selectCountAfterSalesOrder4Condition(@Param("salesType")Integer salesType, 
			@Param("productType")Integer productType, 
			@Param("status")Integer status,
			@Param("auditStatus")Integer auditStatus);

    Integer getSalesReturnOrderNum(@Param("areas") Set<Integer> areas);
}
