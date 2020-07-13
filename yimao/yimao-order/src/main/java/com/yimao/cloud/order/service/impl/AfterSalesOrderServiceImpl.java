package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.enums.AuditSubTypeEnum;
import com.yimao.cloud.base.enums.AuditTypeEnum;
import com.yimao.cloud.base.enums.FinancialStateEnum;
import com.yimao.cloud.base.enums.OrderFrom;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.enums.OrderSubStatusEnum;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.enums.ProductActivityType;
import com.yimao.cloud.base.enums.ProductModeEnum;
import com.yimao.cloud.base.enums.StatusEnum;
import com.yimao.cloud.base.enums.WorkOrderChargebackEnum;
import com.yimao.cloud.base.enums.WorkOrderCompleteEnum;
import com.yimao.cloud.base.enums.WorkOrderStateEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.AmountUtils;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.DateUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.feign.ProductFeign;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.mapper.AfterSalesOrderMapper;
import com.yimao.cloud.order.mapper.AfterSalesStatusRecordMapper;
import com.yimao.cloud.order.mapper.OrderAuditLogMapper;
import com.yimao.cloud.order.mapper.OrderInvoiceMapper;
import com.yimao.cloud.order.mapper.OrderStatusRecordMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.SubOrderDetailMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.mapper.WorkOrderOperationMapper;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.order.po.AfterSalesStatusRecord;
import com.yimao.cloud.order.po.OrderAuditLog;
import com.yimao.cloud.order.po.OrderInvoice;
import com.yimao.cloud.order.po.OrderStatusRecord;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.RefundRecord;
import com.yimao.cloud.order.po.SubOrderDetail;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.po.WorkOrderOperation;
import com.yimao.cloud.order.service.AfterSalesOrderService;
import com.yimao.cloud.order.service.OrderInvoiceService;
import com.yimao.cloud.order.service.OrderSubService;
import com.yimao.cloud.order.service.ProductIncomeRecordService;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.order.service.RefundService;
import com.yimao.cloud.order.service.SyncWorkOrderService;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderDTO;
import com.yimao.cloud.pojo.dto.order.AfterSalesOrderQueryDTO;
import com.yimao.cloud.pojo.dto.order.RefundDetailDTO;
import com.yimao.cloud.pojo.dto.order.RefundRequest;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import com.yimao.cloud.pojo.vo.order.RefundReviewBatchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AfterSalesOrderServiceImpl implements AfterSalesOrderService {

    @Resource
    private AfterSalesOrderMapper afterSalesOrderMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private AfterSalesStatusRecordMapper afterSalesStatusRecordMapper;
    @Resource
    private OrderStatusRecordMapper orderStatusRecordMapper;
    @Resource
    private OrderAuditLogMapper orderAuditLogMapper;
    @Resource
    private SubOrderDetailMapper detailMapper;
    @Resource
    private RefundService refundService;
    @Resource
    private OrderSubService orderSubService;
    @Resource
    private ProductIncomeRecordService productIncomeRecordService;
    @Resource
    private WorkOrderOperationMapper workOrderOperationMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private OrderInvoiceService orderInvoiceService;
    @Resource
    private OrderInvoiceMapper orderInvoiceMapper;
    @Resource
    private SyncWorkOrderService syncWorkOrderService;
    @Resource
    private QuotaChangeRecordService quotaChangeRecordService;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private ProductFeign productFeign;

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
    @Override
    public PageVO<AfterSalesOrderDTO> refundAudit(Integer pageNum, Integer pageSize, AfterSalesOrderQueryDTO dto) {

        PageHelper.startPage(pageNum, pageSize);
        Page<AfterSalesOrderDTO> page = afterSalesOrderMapper.refundAudit(dto);
        return new PageVO<>(pageNum, page);
    }

    /**
     * 线上/线下退款复核
     *
     * @param id      主键
     * @param updater 操作人
     * @return void
     * @author hhf
     * @date 2019/3/13
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void refundReview(Long id, String updater) {
        /*AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
        if (Objects.isNull(afterSalesOrder)) {
            throw new NotFoundException("未找到退单数据");
        }
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
        if (Objects.nonNull(orderSub)) {
        	try {
				refund(id, updater);
			} catch (Exception e) {
				log.error("===========批量审核失败============售后单号====:"+id+",异常信息="+e.getMessage());
				throw new YimaoException("审核失败");
			}
        }*/
    }

    /**
     * 线上/线下退款批量复核
     *
     * @param updater 操作人
     * @return void
     * @author hhf
     * @date 2019/3/13
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void refundReviewBatch(RefundReviewBatchVo refundReviewBatchVo, String updater) {
        log.info("==============refundReviewBatch ids=" + JSONObject.toJSONString(refundReviewBatchVo));
        if (Objects.isNull(refundReviewBatchVo) || refundReviewBatchVo.getIds().isEmpty()) {
            throw new YimaoException("请求参数不能为空");
        }
        for (Long id : refundReviewBatchVo.getIds()) {
            try {
                if (!refund(id, refundReviewBatchVo.getPass(), refundReviewBatchVo.getReason(), updater)) {
                    throw new YimaoException("审核失败");
                }
            } catch (Exception e) {
                log.error("===========批量审核失败============售后单号====:" + id + ",异常信息=" + e.getMessage());
                throw new YimaoException(e.getMessage());
            }
        }
    }

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
    @Override
    public PageVO<AfterSalesOrderDTO> refundRecord(Integer pageNum, Integer pageSize, AfterSalesOrderQueryDTO dto) {
        PageHelper.startPage(pageNum, pageSize);
        Page<AfterSalesOrderDTO> page = afterSalesOrderMapper.refundRecord(dto);
        return new PageVO<>(pageNum, page);
    }

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
    @Override
    public PageVO<AfterSalesOrderDTO> refundLog(Integer pageNum, Integer pageSize, Long orderId, Integer operation, String startTime, String endTime) {
        PageHelper.startPage(pageNum, pageSize);
        Page<AfterSalesOrderDTO> page = afterSalesOrderMapper.refundLog(orderId, operation, startTime, endTime);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public PageVO<AfterSalesOrderDTO> rentalGoodsList(Integer pageNum, Integer pageSize, AfterSalesConditionDTO dto) {
        PageHelper.startPage(pageNum, pageSize);
        Page<AfterSalesOrderDTO> page = afterSalesOrderMapper.rentalGoodsList(dto);
        return new PageVO<>(pageNum, page);
    }

    /*@Override
    public List<RentalGoodsExportDTO> exportRentalGoods(AfterSalesConditionDTO dto) {
        return afterSalesOrderMapper.exportRentalGoods(dto);
    }

    @Override
    public List<RentalGoodsExportDTO> exportAuditedRentalGoods(AfterSalesConditionDTO dto) {
        return afterSalesOrderMapper.exportAuditedRentalGoods(dto);
    }

    @Override
    public List<RentalGoodsExportDTO> exportAuditedRecord(AfterSalesConditionDTO dto) {
        return afterSalesOrderMapper.exportAuditedRecord(dto);
    }*/

    /**
     * @param id
     * @param auditStatus
     * @return java.lang.Object
     * @description
     * @author zhilin.he  我的订单--经销商退单审核
     * @date 2019/8/22 14:15
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateAuditStatus(Long id, Integer auditStatus, String updater) {
        AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
        if (Objects.isNull(afterSalesOrder)) {
            throw new NotFoundException("未找到退单数据");
        }
        if (afterSalesOrder.getStatus() != OrderSubStatusEnum.PENDING_AUDIT_DISTRIBUTOR.value) {
            throw new BadRequestException("该售后单已不属于待经销商审核状态");
        }

        //查询订单信息
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
        if (Objects.isNull(orderSub)) {
            throw new NotFoundException("未找到订单数据");
        }
        String operation = "";
        Date now = new Date();
        AfterSalesStatusRecord afterSalesStatusRecord = new AfterSalesStatusRecord();
        //同意退单-进入总部审核
        if (auditStatus == 1) {
            operation = "审核通过";
            afterSalesOrder.setStatus(OrderSubStatusEnum.PENDING_AUDIT_HEAD.value);
            afterSalesStatusRecord.setDestStatus(OrderSubStatusEnum.PENDING_AUDIT_HEAD.value);
            //修改为待总部审核
            orderSub.setSubStatus(OrderSubStatusEnum.PENDING_AUDIT_HEAD.value);
            orderSubMapper.updateByPrimaryKeySelective(orderSub);
        } else {
            //继续服务 - 回到之前的状态,订单继续原流程
            operation = "审核不通过";
            orderSub.setStatus(OrderStatusEnum.PENDING_RECEIPT.value);
            orderSub.setSubStatus(null);
            orderSub.setUpdateTime(now);
            int result = orderSubMapper.updateByPrimaryKey(orderSub);
            if (result < 0) {
                throw new YimaoException(500, "订单操作失败。");
            }

            //订单状态记录
            OrderStatusRecord record = new OrderStatusRecord();
            record.setId(orderSub.getId());
            record.setCreateTime(now);
            record.setCreator(updater);
            //变更之后的状态
            record.setDestStatus(OrderStatusEnum.PENDING_RECEIPT.value);
            //变更之前的状态
            record.setOrigStatus(OrderStatusEnum.AFTER_SALE.value);
            record.setRemark("经销商审核订单");
            int result1 = orderStatusRecordMapper.insert(record);
            if (result1 < 0) {
                throw new YimaoException(500, "订单状态操作失败。");
            }
            afterSalesOrder.setStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);
            afterSalesStatusRecord.setDestStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);

            //获取工单信息
            WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(afterSalesOrder.getOrderId());
            if (Objects.isNull(workOrder)) {
                throw new YimaoException(500, "工单号不能为空。");
            }

            //是否是上线新流程校验
            workOrder.setStatus(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state); //老流程-已受理
            if (Objects.nonNull(workOrder.getProvince()) && Objects.nonNull(workOrder.getCity()) && Objects.nonNull(workOrder.getRegion())) {
                OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                if (onlineArea != null && Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                    workOrder.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state); //新流程-处理中
                }
            }
            String workOrderId = workOrder.getId();
            workOrder.setChargeback(false);
            workOrder.setStep(0);
            workOrder.setNextStep(2);   //处理中-已接单
            workOrderMapper.updateWorkOrder(workOrder);

            //保存工单操作记录
            WorkOrderOperation orderOperation = new WorkOrderOperation();
            orderOperation.setWorkOrderId(workOrderId);
            orderOperation.setOperation("经销商退单");
            orderOperation.setRemark("经销商审核不通过");
            orderOperation.setAdmin(updater);
            orderOperation.setCreateTime(now);
            workOrderOperationMapper.insertSelective(orderOperation);

            //同步
            syncWorkOrderService.syncWorkOrder(workOrderId);
            //如果存在开票信息，删除
            OrderInvoice invoice = orderInvoiceService.getInvoiceByOrderId(orderSub.getId());
            if (Objects.nonNull(invoice)) {
                orderInvoiceMapper.delete(invoice);
            }
        }

        int result = afterSalesOrderMapper.updateByPrimaryKey(afterSalesOrder);
        if (result < 0) {
            throw new YimaoException(500, "售后订单操作失败。");
        }

        // 售后记录表记录状态
        afterSalesStatusRecord.setCreateTime(now);
        afterSalesStatusRecord.setCreator(updater);
        afterSalesStatusRecord.setAfterSalesOrderId(id);
        afterSalesStatusRecord.setOrigStatus(OrderSubStatusEnum.PENDING_AUDIT_DISTRIBUTOR.value);
        afterSalesStatusRecordMapper.insert(afterSalesStatusRecord);

        //添加审核日志
        OrderAuditLog orderAuditLog = new OrderAuditLog();
        orderAuditLog.setSalesId(afterSalesOrder.getId());
        orderAuditLog.setOrderId(afterSalesOrder.getOrderId());
        orderAuditLog.setType(AuditTypeEnum.CANCELLATION_ORDER_REFUND.value);
        orderAuditLog.setSubType(AuditSubTypeEnum.CUSTOMER_SERVICE_AUDIT.value);
        orderAuditLog.setOperationStatus(auditStatus == 1);
        orderAuditLog.setOperation(operation);
        orderAuditLog.setMenuName("经销商审核");
        orderAuditLog.setCreator(updater);
        orderAuditLog.setCreateTime(now);
        orderAuditLogMapper.insert(orderAuditLog);
    }


    @Override
    public AfterSalesOrderDTO getSalesDetailById(Long id) {
        AfterSalesOrderDTO afterSalesOrderDTO = new AfterSalesOrderDTO();
        AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
        if (null == afterSalesOrder) {
            throw new NotFoundException("不存在该退款单");
        }
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
        SubOrderDetail orderDetail = detailMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());

        Example example = new Example(OrderAuditLog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("salesId", id);
        List<OrderAuditLog> orderAuditLogs = orderAuditLogMapper.selectByExample(example);
        afterSalesOrderDTO.setId(id);
        afterSalesOrderDTO.setOrderId(afterSalesOrder.getOrderId());
        afterSalesOrderDTO.setMainOrderId(afterSalesOrder.getMainOrderId());
        if (afterSalesOrder.getProductType() == ProductModeEnum.LEASE.value) {
            afterSalesOrderDTO.setAuditTime(afterSalesOrder.getCustomerServiceTime());
        }

        if (afterSalesOrder.getProductType() == ProductModeEnum.REALTHING.value) {
            afterSalesOrderDTO.setAuditTime(afterSalesOrder.getBuyAuditTime());
        }

        afterSalesOrderDTO.setCustomerServiceAuditStatus(afterSalesOrder.getCustomerServiceAuditStatus());
        if (null != orderSub) {
            afterSalesOrderDTO.setRefer(orderSub.getRefer());
            afterSalesOrderDTO.setProductNum(orderSub.getCount());
            afterSalesOrderDTO.setOrderResource(orderSub.getTerminal() + "");
            afterSalesOrderDTO.setCancelReason(orderSub.getCancelReason());
        }
        if (null != orderDetail) {
            afterSalesOrderDTO.setProductImg(orderDetail.getProductImg());
            afterSalesOrderDTO.setProductOneCategoryName(orderDetail.getProductFirstCategoryName());
            afterSalesOrderDTO.setProductTwoCategoryName(orderDetail.getProductTwoCategoryName());
            afterSalesOrderDTO.setProductCategoryName(orderDetail.getProductCategoryName());
            afterSalesOrderDTO.setDistributorId(orderDetail.getDistributorId());
            afterSalesOrderDTO.setDistributorName(orderDetail.getDistributorName());
            afterSalesOrderDTO.setEngineerName(orderDetail.getEngineerName());
            afterSalesOrderDTO.setUserName(orderDetail.getAddresseeName());
            afterSalesOrderDTO.setAddresseePhone(orderDetail.getAddresseePhone());
            afterSalesOrderDTO.setAddresseeProvince(orderDetail.getAddresseeProvince());
            afterSalesOrderDTO.setAddresseeCity(orderDetail.getAddresseeCity());
            afterSalesOrderDTO.setAddresseeRegion(orderDetail.getAddresseeRegion());
            afterSalesOrderDTO.setAddresseeStreet(orderDetail.getAddresseeStreet());
        }
        if (CollectionUtil.isNotEmpty(orderAuditLogs)) {
            afterSalesOrderDTO.setAuditReason(orderAuditLogs.get(0).getAuditReason());
            afterSalesOrderDTO.setDetailReason(orderAuditLogs.get(0).getDetailReason());
        }
        afterSalesOrderDTO.setUserId(afterSalesOrder.getUserId());
        afterSalesOrderDTO.setTerminal(afterSalesOrder.getTerminal());
        afterSalesOrderDTO.setNum(afterSalesOrder.getNum());
        afterSalesOrderDTO.setRefundFee(afterSalesOrder.getRefundFee());
        afterSalesOrderDTO.setRealRefundFee(afterSalesOrder.getRealRefundFee());
        afterSalesOrderDTO.setFormalitiesFee(afterSalesOrder.getFormalitiesFee());
        afterSalesOrderDTO.setCreateTime(afterSalesOrder.getCreateTime());
        afterSalesOrderDTO.setRefundReason(afterSalesOrder.getRefundReason());
        afterSalesOrderDTO.setStatus(afterSalesOrder.getStatus());
        return afterSalesOrderDTO;
    }

    /***
     * 订单退款
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public boolean refund(Long id, Boolean pass, String reason, String updater) throws Exception {
        try {
            //定义订单对象
            AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
            if (Objects.isNull(afterSalesOrder)) {
                throw new NotFoundException("未找到退单数据");
            }
            OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
            if (Objects.isNull(orderSub)) {
                throw new NotFoundException("未找到退单数据");
            }

            //数据校验 //售后单状态为待退款状态或者是售后失败的
            if (!this.checkOrderData(id, afterSalesOrder, orderSub)) {
                return false;
            }

            afterSalesOrder.setFinancer(updater);//操作人
            if (!StringUtil.isEmpty(reason)) {
                afterSalesOrder.setFinanceReason(reason);
            }
            //目前只支持微信和支付宝的退款
            if (pass && null != orderSub.getPayType() && (orderSub.getPayType() == PayType.WECHAT.value || orderSub.getPayType() == PayType.ALIPAY.value)) {
                RefundRequest refund = new RefundRequest();
                refund.setOutTradeNo(String.valueOf(afterSalesOrder.getMainOrderId()));
                refund.setOutRefundNo(String.valueOf(afterSalesOrder.getId()));
                refund.setRefundFee(afterSalesOrder.getRefundFee());
                refund.setRefundReason(afterSalesOrder.getRefundReason());
                refund.setPayTradeNo(orderSub.getTradeNo());
                refundService.refund(refund);
                //更新售后订单的审核人，支付回调没有审核人参数导致线上退款审核没有审核人
                AfterSalesOrder updateAfterSalesOrder = new AfterSalesOrder();
                updateAfterSalesOrder.setId(id);
                updateAfterSalesOrder.setFinancer(updater);
                afterSalesOrderMapper.updateByPrimaryKeySelective(updateAfterSalesOrder);
            } else {
                //线下退款
                afterRefund(afterSalesOrder, orderSub, pass, PayType.OTHER.value);
            }

            //工单操作记录
            if (Objects.equals(orderSub.getProductType(), ProductModeEnum.LEASE.value)) {
                WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(orderSub.getId());
                if (Objects.nonNull(workOrder)) {
                    WorkOrderOperation operation = new WorkOrderOperation();
                    operation.setAdmin(workOrder.getEngineerName());
                    operation.setWorkOrderId(workOrder.getId());
                    operation.setOperation("审核通过");
                    operation.setRemark("财务复核通过");
                    operation.setAdmin(updater);
                    operation.setCreateTime(new Date());
                    workOrderOperationMapper.insertSelective(operation);
                }
            }
        } catch (Exception e) {
            log.error("============该售后单(id=" + id + ")退款异常,异常信息===" + e.getMessage());
            throw new Exception("售后单[id=" + id + "]退款异常");
        }

        return true;


    }

    /****
     * 订单退款后操作 支付宝直接返回结果处理,微信异步回调通知
     * @param pay_type
     * @throws Exception
     */
    private void afterRefund(AfterSalesOrder afterSalesOrder, OrderSub orderSub, boolean flag, Integer pay_type) throws Exception {
        //存储原来售后单的状态
        Integer origStatus = afterSalesOrder.getStatus();
        afterSalesOrder.setFinanceAuditStatus(flag ? FinancialStateEnum.PASS_AUDIT.value : FinancialStateEnum.UN_PASS_AUDIT.value);
        afterSalesOrder.setFinanceTime(new Date());
        int destStatus = flag ? OrderSubStatusEnum.AFTER_SALE_SUCCESS.value : OrderSubStatusEnum.AFTER_SALE_FAILURE.value;
        this.updateAfterSalesOrderStatus(afterSalesOrder, origStatus, destStatus);
        log.info("更新订单状态");
        //更新子订单状态
        if (flag) {
            //如果审核通过,则需要把子订单的主状态status 设置为交易关闭
            orderSub.setStatus(OrderStatusEnum.CLOSED.value);
        } else {
            //变更为原来的状态
            //查询最新变更的订单状态纪录
            OrderStatusRecord orderStatusRecord = orderStatusRecordMapper.selectUptodateOrderStatusByOrderSubId(orderSub.getId());
            if (Objects.nonNull(orderStatusRecord) && orderStatusRecord.getDestStatus() == OrderStatusEnum.AFTER_SALE.value) {
                orderSub.setStatus(orderStatusRecord.getOrigStatus());
            }

        }
        orderSub.setSubStatus(destStatus);
        orderSub.setRefundTime(new Date());
        orderSub.setUpdateTime(new Date());
        int result = orderSubService.updateSubStatus(orderSub);
        if (result != 1) {
            log.error("==========退款完成更新子单状态失败==========orderid=" + orderSub.getId());
            throw new Exception("退款完成更新子单状态失败");
        }

        //审核通过
        if (flag) {
            //产品收益明细-改为退单
            productIncomeRecordService.refundGoods(orderSub.getId(), afterSalesOrder.getNum());

            //如果是水机产品 还要修改配额
            if (null != orderSub.getProductType() && Objects.equals(orderSub.getProductType(), ProductModeEnum.LEASE.value)) {
                //修改工单为已退单
                WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(orderSub.getId());
                if (Objects.isNull(workOrder)) {
                    throw new YimaoException(500, "工单号不能为空。");
                }
                workOrder.setChargebackStatus(Integer.parseInt(WorkOrderChargebackEnum.BACK_COMPLETE.getState()));
                workOrder.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());
                workOrder.setUpdateTime(new Date());
                workOrderMapper.updateByPrimaryKeySelective(workOrder);
                //同步
                syncWorkOrderService.syncWorkOrder(workOrder.getId());

                log.info("=================产品线下审核通过,恢复配额  start=");
                quotaChangeRecordService.quotaChange(orderSub.getId(), orderSub.getDistributorId(), "线下退款", 2, afterSalesOrder.getNum(), null);
                log.info("=================产品线下审核通过,恢复配额  end=");
            }

            //2020-03-17
            //活动商品还配额
            if (orderSub.getActivityType() != null && orderSub.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
                productFeign.addProductActivityStock(orderSub.getActivityId(), orderSub.getCount());
            }
        }

    }

    /****
     * 校验退单和订单数据
     * @param id
     * @return
     */
    private boolean checkOrderData(Long id, AfterSalesOrder afterSalesOrder, OrderSub orderSub) {
        if (Objects.isNull(id) || null == afterSalesOrder || null == orderSub) {
            log.error("==============退款参数(after_sales_order[id])为空!,请检查=============");
            return false;
        }
        // 状态为待退款 或者退款失败
        if ((afterSalesOrder.getStatus().intValue() != OrderSubStatusEnum.PENDING_REFUND.value
                && afterSalesOrder.getStatus().intValue() != OrderSubStatusEnum.AFTER_SALE_FAILURE.value)) {
            log.error("===============售后单号(" + id + ")退单数据不存在或者状态不符合退单逻辑===========afterSalesOrder="
                    + JSONObject.toJSONString(afterSalesOrder));
            return false;
        }

        // 支付金额和退款金额都大于0,并且退款金额不能大于支付金额
        if (!AmountUtils.compareTo(afterSalesOrder.getRealRefundFee(), afterSalesOrder.getPayFee())) {
            log.error("===============售后单号(" + id + ")退款金额不能大于支付金额(或者金额为空)===========afterSalesOrder="
                    + JSONObject.toJSONString(afterSalesOrder));
            return false;
        }

        // 子订单主状态为售后中,子状态为待退款或者是售后失败状态
        if ((orderSub.getSubStatus().intValue() != OrderSubStatusEnum.PENDING_REFUND.value
                && orderSub.getSubStatus().intValue() != OrderSubStatusEnum.AFTER_SALE_FAILURE.value)
                || !orderSub.getPay() || orderSub.getStatus().intValue() != OrderStatusEnum.AFTER_SALE.value) {
            log.error("===============售后单号(" + id + ")子订单信息不存在或者子单状态不符合退款逻辑================ordersub="
                    + JSONObject.toJSONString(orderSub));
            return false;
        }
        return true;
    }

    /****
     * 更新售后单状态
     * @param afterSalesOrder
     * @param origStatus
     * @param destStatus
     * @return
     * @throws Exception
     */
    public void updateAfterSalesOrderStatus(AfterSalesOrder afterSalesOrder, Integer origStatus, int destStatus) throws Exception {
        afterSalesOrder.setStatus(destStatus);
        //更新售后单状态
        int result = afterSalesOrderMapper.updateAfterSalesOrderStatus(afterSalesOrder);
        if (result != 1) {
            log.error("==========更新售后单状态失败==========id=" + afterSalesOrder.getId());
            throw new Exception("==========更新售后单状态失败==========");
        }

        //添加变更状态记录
        AfterSalesStatusRecord afterSalesStatusRecord = new AfterSalesStatusRecord(afterSalesOrder.getId(), origStatus, destStatus, new Date(), "admin", "售后订单退款状态变更");
        result = afterSalesStatusRecordMapper.saveAfterSalesStatusRecord(afterSalesStatusRecord);
        if (result != 1) {
            log.error("==========保存售后单变更记录失败,请及时处理,售后单编号:" + afterSalesOrder.getId() + ",原售后单状态:" + origStatus + ",目标状态:" + destStatus);
        }
    }


    //微信退款回调更新订单状态
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void updateAfterSalesOrderAndSubOrder(Long id, boolean flag) throws Exception {
        try {
            AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
            if (null == afterSalesOrder) {
                throw new Exception("微信退款回调更新订单状态失败 售后单号(" + id + "),订单信息不存在");
            }
            OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
            if (null == orderSub) {
                throw new Exception("微信退款回调更新订单状态失败 售后单号(" + id + "),订单信息不存在");
            }
            afterRefund(afterSalesOrder, orderSub, flag, PayType.WECHAT.value);
        } catch (Exception e) {
            log.error("==========微信退款回调更新订单状态失败 售后单号(" + id + "),异常信息==" + e.getMessage());
            throw new Exception("微信退款回调更新订单状态失败 售后单号(" + id + "),异常信息");
        }

    }


    /**
     * 退款回调-修改售后状态
     *
     * @param record 退款对象
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void refundUpdateAfterSaleStatus(RefundRecord record) {
        //修改售后单状态  【是否存在续费退款的功能？？】
        AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(Long.parseLong(record.getOutRefundNo()));
        if (Objects.isNull(afterSalesOrder) || !Objects.equals(OrderSubStatusEnum.PENDING_REFUND.value, afterSalesOrder.getStatus())) {
            log.error("退款回调失败，售后单异常");
            throw new YimaoException("退款回调失败，售后单异常");
        }
        afterSalesOrder.setStatus(OrderSubStatusEnum.AFTER_SALE_SUCCESS.value); //退款状态-售后成功
        afterSalesOrder.setRefundTime(record.getRefundTime());      //退款到账时间
        afterSalesOrder.setRefundTradeNo(record.getRefundTradeNo());//退款流水号
        afterSalesOrder.setFinanceAuditStatus(1);
        afterSalesOrder.setFinanceTime(new Date());
        afterSalesOrderMapper.updateByPrimaryKeySelective(afterSalesOrder);

        //修改订单状态-是否是退款单
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
        if (Objects.isNull(orderSub)) {
            log.error("退款回调失败，获取不到有效订单。");
            throw new YimaoException("退款回调失败，获取不到有效订单");
        }
        orderSub.setStatus(OrderStatusEnum.CLOSED.value);
        orderSub.setSubStatus(OrderSubStatusEnum.AFTER_SALE_SUCCESS.value);
        orderSubMapper.updateSubStatus(orderSub);

        //租赁商品，工单状态修改为已退款
        if (Objects.equals(orderSub.getProductType(), ProductModeEnum.LEASE.value)) {
            WorkOrder workOrder = workOrderMapper.findWorkOrderByOrderId(orderSub.getId());
            if (Objects.isNull(workOrder)) {
                log.error("退款回调失败，工单不存在。");
                throw new YimaoException(500, "工单号不能为空。");
            }
            workOrder.setChargebackStatus(Integer.parseInt(WorkOrderChargebackEnum.BACK_COMPLETE.getState()));
            workOrder.setUpdateTime(new Date());
            workOrderMapper.updateByPrimaryKeySelective(workOrder);
            //同步
            syncWorkOrderService.syncWorkOrder(workOrder.getId());
        }

        //产品收益明细-改为退单
        productIncomeRecordService.refundGoods(orderSub.getId(), afterSalesOrder.getNum());
        //配额记录
        quotaChangeRecordService.quotaChange(afterSalesOrder.getOrderId(), orderSub.getDistributorId(), "财务退款成功", 2, 1, null);
        //2020-03-17
        //活动商品还配额
        if (orderSub.getActivityType() != null && orderSub.getActivityType() == ProductActivityType.PANIC_BUYING.value) {
            productFeign.addProductActivityStock(orderSub.getActivityId(), orderSub.getCount());
        }
    }

    @Override
    public RefundDetailDTO refundDetai(Long id) {
        RefundDetailDTO dto = afterSalesOrderMapper.getRefunddetailById(id);
        return convertData(dto);
    }

    /**
     * 格式化日期
     *
     * @param dto
     * @return
     */
    private RefundDetailDTO convertData(RefundDetailDTO dto) {
        if (dto == null) {
            return new RefundDetailDTO();
        }
        if (null != dto.getRefundTime()) {
            dto.setRefundTimeText(DateUtil.dateToString(dto.getRefundTime()));
        }
        if (null != dto.getRefundVerifyTime()) {
            dto.setRefundVerifyTimeText(DateUtil.dateToString(dto.getRefundVerifyTime()));
        }
        //订单来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；
        if (null != dto.getTerminal()) {
            dto.setOrderSource(OrderFrom.getOrderFromName(dto.getTerminal()));
        }
        return dto;
    }

//    private WorkOrder cleanWorkOrderSetup(WorkOrder workOrder) {
//        workOrder.setDeviceId(null);
//        workOrder.setOperationTime(null);
//        workOrder.setChargeback(false);
//        workOrder.setChargebackType(null);//1-经销商退单；2-客服退单
//        workOrder.setChargebackNum(null);
//        workOrder.setChargebackReason(null);
//        workOrder.setChargebackRemark(null);
//        workOrder.setChargebackSncode(null);
//        workOrder.setChargebackStatus(null);
//        workOrder.setChargebackTime(null);
//        workOrder.setCompleteType(null);//异常完成
//        workOrder.setCompleteTime(null);
//        workOrder.setSn(null);
//        workOrder.setStep(0);
//        workOrder.setPickTime(null);
//        workOrder.setProtocol(null);//协议编号
//        workOrder.setConfirmation(null);//确认单号
//        workOrder.setSignStatus("N");//用户是否签署合同：N、否；Y、是
//        workOrder.setSignTime(null); //合同签约时间
//        workOrder.setSignOrderId(null);//合同签约单号
//        workOrder.setSignUserName(null);//签约合同用户姓名
//        workOrder.setSignUserPhone(null);//签约合同用户手机号
//        workOrder.setSignUserIdCard(null);//签约合同用户身份证
//        workOrder.setSignUserEmail(null);//签约合同用户邮箱
//        workOrder.setSignUserAddress(null);//签约合同用户地址
//        workOrder.setSignClient(null);              //合同签约客户端
//        workOrder.setWaterStatus(null);                    //原水水源信息输入状态：N、输入失败；Y、输入成功
//        workOrder.setWaterStatusText(null);                //原水源状态文本
//        workOrder.setMunicipal(null);                    // 原水水源是否为市政自来水-1是/否0
//        workOrder.setWaterSourceDescription(null);         // 原水水源描述
//        workOrder.setTds(null);                            // 原水TDS值
//        workOrder.setHydraulic(null);                      // 原水水压值
//        workOrder.setOtherSource(null);                    //其他原水水源
//        workOrder.setSimCard(null);                        //sim卡
//        workOrder.setSimCardTime(null);                      //sim卡输入日期
//        workOrder.setLogisticsCode(null);                  //物流编码(批次码)
//        workOrder.setMaterielCode(null);                   //物料码
//        workOrder.setSupplierName(null);                   //生产商
//        workOrder.setWaterImages(null);                    //水质图片
//        workOrder.setUploadImgTime(null);                    //上传水质图片日期
//        workOrder.setInvoice(null);                //是否开票：0-不开票；1-开票
//        workOrder.setInvoiceType(null);            //开票类型：1-普通；2-增值税；
//        workOrder.setInvoiceHeaderType(null);      //开票抬头类型：1-公司；2-个人；
//        workOrder.setInvoiceTitle(null);            //开票抬头
//        workOrder.setInvoiceTaxNum(null);           //税号
//        workOrder.setInvoiceBank(null);             //开户行
//        workOrder.setInvoiceBankNum(null);          //开户号
//        workOrder.setInvoiceAddress(null);
//        return workOrder;
//    }
}
