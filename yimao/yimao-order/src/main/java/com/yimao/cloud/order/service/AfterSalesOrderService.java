package com.yimao.cloud.order.service;

import com.yimao.cloud.order.po.RefundRecord;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.RefundDetailDTO;
import com.yimao.cloud.pojo.dto.order.RentalGoodsExportDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.RefundReviewBatchVo;

import java.util.List;

/**
 * @author hhf
 * @date 2019/3/12
 */
public interface AfterSalesOrderService {

    /**
     * 线上/线下退款审核列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO>
     * @author hhf
     * @date 2019/3/13
     */
    PageVO<AfterSalesOrderDTO> refundAudit(Integer pageNum, Integer pageSize, AfterSalesOrderQueryDTO dto);

    /**
     * 线上/线下退款复核
     *
     * @param id      售后订单主键
     * @param updater 操作人
     * @return void
     * @author hhf
     * @date 2019/3/13
     */
    void refundReview(Long id, String updater);

    /**
     * 线上/线下退款批量复核
     *
     * @param ids     售后订单主键
     * @param updater 操作人
     * @return void
     * @author hhf
     * @param updater 
     * @date 2019/3/13
     */
    void refundReviewBatch(RefundReviewBatchVo refundReviewBatchVo, String updater);

    /**
     * 线上/线下退款记录列表
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param dto      查询信息
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO>
     * @author hhf
     * @date 2019/3/13
     */
    PageVO<AfterSalesOrderDTO> refundRecord(Integer pageNum, Integer pageSize, AfterSalesOrderQueryDTO dto);

    /**
     * 操作日志
     *
     * @param orderId   查询信息
     * @param pageNum   分页页数
     * @param pageSize  分页大小
     * @param operation 操作
     * @param startTime 操作开始时间
     * @param endTime   操作结束时间
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO>
     * @author hhf
     * @date 2019/3/13
     */
    PageVO<AfterSalesOrderDTO> refundLog(Integer pageNum, Integer pageSize, Long orderId, Integer operation, String startTime, String endTime);

    PageVO<AfterSalesOrderDTO> rentalGoodsList(Integer pageNum, Integer pageSize, AfterSalesConditionDTO dto);

    /*List<RentalGoodsExportDTO> exportRentalGoods(AfterSalesConditionDTO dto);

    List<RentalGoodsExportDTO> exportAuditedRentalGoods(AfterSalesConditionDTO dto);

    List<RentalGoodsExportDTO> exportAuditedRecord(AfterSalesConditionDTO dto);*/

    /**
     * @param id
     * @param auditStatus
     * @param updater
     * @return java.lang.Object
     * @description
     * @author zhilin.he  我的订单--经销商退单审核
     * @date 2019/8/22 14:15
     */
    void updateAuditStatus(Long id, Integer auditStatus, String updater);

    AfterSalesOrderDTO getSalesDetailById(Long id);
    
   /****
    * 订单退款
    * @param id
    * @param updater
    * @author zhangbaobao
    * @date 2019/9/20
    */
    boolean refund(Long id,Boolean pass,String reason, String updater) throws Exception;
    
    /****
     * 微信支付回调 更新订单状态
     * @param id
     * @param flag
     */
    void updateAfterSalesOrderAndSubOrder(Long id, boolean flag) throws Exception;

    //退款修改售后状态
    void refundUpdateAfterSaleStatus(RefundRecord record) throws Exception;
    
    /***
     * 根据售后单号查询退款详情
     * @param id
     * @return
     */
	RefundDetailDTO refundDetai(Long id);
}
