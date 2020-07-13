package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.*;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.order.feign.SystemFeign;
import com.yimao.cloud.order.feign.WaterFeign;
import com.yimao.cloud.order.mapper.*;
import com.yimao.cloud.order.po.*;
import com.yimao.cloud.order.service.OrderAuditLogService;
import com.yimao.cloud.order.service.OrderInvoiceService;
import com.yimao.cloud.order.service.QuotaChangeRecordService;
import com.yimao.cloud.order.service.SyncWorkOrderService;
import com.yimao.cloud.pojo.dto.order.AfterSalesConditionDTO;
import com.yimao.cloud.pojo.dto.order.ExamineRecordDTO;
import com.yimao.cloud.pojo.dto.order.OrderAuditLogDTO;
import com.yimao.cloud.pojo.dto.system.OnlineAreaDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-08-23 16:49:38
 **/
@Service
@Slf4j
public class OrderAuditLogServiceImpl implements OrderAuditLogService {

    @Resource
    private OrderAuditLogMapper orderAuditLogMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private AfterSalesOrderMapper afterSalesOrderMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private QuotaChangeRecordService quotaChangeRecordService;
    @Resource
    private WorkOrderOperationMapper workOrderOperationMapper;
    @Resource
    private QuotaChangeRecordMapper quotaChangeRecordMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private OrderInvoiceService orderInvoiceService;
    @Resource
    private OrderInvoiceMapper orderInvoiceMapper;
    @Resource
    private SyncWorkOrderService syncWorkOrderService;
    @Resource
    private SystemFeign systemFeign;
    @Resource
    private WaterFeign waterFeign;

    @Override
    public PageVO<OrderAuditLogDTO> orderAuditLogList(Integer pageNum, Integer pageSize, AfterSalesConditionDTO dto) {
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderAuditLogDTO> page = orderAuditLogMapper.orderAuditLogList(dto);
        return new PageVO<>(pageNum, page);
    }

    @Override
    public ExamineRecordDTO getExamineRecordDetailById(Long id) {
        ExamineRecordDTO recordDTO = new ExamineRecordDTO();
        OrderAuditLog orderAuditLog = orderAuditLogMapper.selectByPrimaryKey(id);
        if (null != orderAuditLog) {
            OrderSub orderSub = orderSubMapper.selectByPrimaryKey(orderAuditLog.getOrderId());
            //售后单号
            recordDTO.setSalesId(orderAuditLog.getSalesId());
            //子订单号
            recordDTO.setOrderId(orderAuditLog.getOrderId());
            //审核状态
            recordDTO.setOperationStatus(orderAuditLog.getOperationStatus());
            //审核人
            recordDTO.setCreator(orderAuditLog.getCreator());
            //审核不通过原因
            recordDTO.setAuditReason(orderAuditLog.getAuditReason());
            //审核时间
            recordDTO.setHandleTime(orderAuditLog.getCreateTime());
            //详细描述
            recordDTO.setDetailReason(orderAuditLog.getDetailReason());
            if (null != orderSub) {
                //主订单号
                recordDTO.setMainOrderId(orderSub.getMainOrderId());
                //工单号
                recordDTO.setRefer(orderSub.getRefer());
            }
            AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(orderAuditLog.getSalesId());
            if (null != afterSalesOrder) {
                //售后申请端
                recordDTO.setSalesTerminal(afterSalesOrder.getTerminal());
                //申请数量
                recordDTO.setNum(afterSalesOrder.getNum());
                //申请时间
                recordDTO.setCreateTime(afterSalesOrder.getCreateTime());
            }

        }
        return recordDTO;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void auditOrder(Long id, String updater) {
        //售后单号
        AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
        if (null == afterSalesOrder) {
            throw new NotFoundException("未找到退款订单");
        }
        if (afterSalesOrder.getStatus() != OrderSubStatusEnum.PENDING_AUDIT_HEAD.value) {
            log.error("审核失败，售后状态异常");
            throw new YimaoException("审核失败，售后状态异常");
        }

        Integer auditLogSubType = null; //子审核类型
        WorkOrder workOrder = null; //工单
        Date now = new Date();
        OrderSub sub = new OrderSub();
        sub.setId(afterSalesOrder.getOrderId());
        //租赁商品待发货状态(安装工未接单)下取消---->生成售后单-->400(售后审核)
        if (afterSalesOrder.getProductType() == ProductModeEnum.LEASE.value) {
            auditLogSubType = SubTypeEnum.CUSTOMER_SERVICE_AUDIT.value;
            OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
            if (Objects.nonNull(orderSub)) {
                workOrder = workOrderMapper.findWorkOrderByOrderId(orderSub.getId());
                if (Objects.isNull(workOrder)) {
                    throw new YimaoException("审核失败，获取工单信息异常。");
                }

                WorkOrder update = new WorkOrder();
                update.setId(workOrder.getId());
                Integer afterStatus;//售后状态:0-待审核(经销商)  1-待审核(总部) 2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功；
                Integer chargeBackStatus = null;//退单状态：0-待退单；1-退单中；2-退单成功；
                //如果是货到付款(未支付) || 折机版下单
                Boolean flag = (Objects.nonNull(orderSub.getPayTerminal()) && Objects.equals(orderSub.getPayTerminal(), 2) && !orderSub.getPay()) || (Objects.equals(orderSub.getActivityType(), 2) && Objects.equals(orderSub.getProductType(), ProductModeEnum.LEASE.value));
                if (flag) {
                    //修改订单/工单状态
                    afterStatus = OrderSubStatusEnum.AFTER_SALE_SUCCESS.value;
                    chargeBackStatus = Integer.parseInt(WorkOrderChargebackEnum.BACK_COMPLETE.getState());
                    update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
                    //订单状态-交易关闭[售后成功]
                    sub.setStatus(OrderStatusEnum.CLOSED.value);
                    sub.setSubStatus(OrderSubStatusEnum.AFTER_SALE_SUCCESS.value);

                    //还配额/金额
                    Example example = new Example(QuotaChangeRecord.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("type", 1);
                    criteria.andEqualTo("orderId", afterSalesOrder.getOrderId());
                    QuotaChangeRecord quotaChangeRecord = quotaChangeRecordMapper.selectOneByExample(example);
                    if (quotaChangeRecord != null) {
                        if (quotaChangeRecord.getCount() != 0) {
                            quotaChangeRecordService.quotaChange(orderSub.getId(), orderSub.getDistributorId(), "货到付款或折机退单-配额", 2, 1, null);
                        } else {
                            quotaChangeRecordService.quotaChange(orderSub.getId(), orderSub.getDistributorId(), "货到付款或折机退单-金额", 2, 0, afterSalesOrder.getPayFee());
                        }
                    }
                } else {
                    //修改订单/工单状态
                    afterStatus = OrderSubStatusEnum.PENDING_REFUND.value;
                    chargeBackStatus = Integer.parseInt(WorkOrderChargebackEnum.BACKING.getState());
                    update.setStatus(WorkOrderStatusEnum.COMPLETED.value);
                    sub.setStatus(OrderStatusEnum.AFTER_SALE.value);
                    sub.setSubStatus(OrderSubStatusEnum.PENDING_REFUND.value);
                }

                //更新工单
                update.setChargeback(true);//是否退单
                update.setChargebackStatus(chargeBackStatus);
                update.setCompleteType(WorkOrderCompleteEnum.AUTO_COMPLETE.getState());//异常完成-老流程
                update.setChargebackReason(workOrder.getChargebackReason());
                update.setChargebackRemark(workOrder.getChargebackRemark());
                update.setUpdateTime(now);
                workOrderMapper.updateWorkOrderRefundAudit(update);

                //400审核-审核完成-交易关闭
                afterSalesOrder.setStatus(afterStatus);
                afterSalesOrder.setCustomerServiceAuditStatus(1);
                afterSalesOrder.setCustomerServiceTime(now);
                afterSalesOrder.setCustomerService(updater);
            } else {
                log.error("审核失败，订单不存在");
                throw new YimaoException("审核失败，订单不存在");
            }
            //实物商品
        } else if (afterSalesOrder.getProductType() == ProductModeEnum.REALTHING.value) {
            auditLogSubType = SubTypeEnum.MATERIAL_REVIEW.value;
            afterSalesOrder.setStatus(OrderSubStatusEnum.PENDING_REFUND.value);
            afterSalesOrder.setBuyAuditStatus(1);       //物资审核通过
            afterSalesOrder.setBuyAuditTime(now);
            afterSalesOrder.setBuyer(updater);
            //修改订单状态为待退款
            sub.setSubStatus(OrderSubStatusEnum.PENDING_REFUND.value);
        }

        afterSalesOrder.setSalesType(AuditTypeEnum.CANCELLATION_ORDER_REFUND.value);
        afterSalesOrder.setAuditee(ReviewerEnum.HEADQUARTERS.name);
        int num = afterSalesOrderMapper.updateByPrimaryKeySelective(afterSalesOrder);
        if (num == 0) {
            throw new YimaoException("售后审核失败");
        }

        //更新订单
        sub.setUpdateTime(now);
        sub.setAuditTime(now);
        orderSubMapper.updateByPrimaryKeySelective(sub);

        if (workOrder != null) {
            //保存工单操作记录
            WorkOrderOperation operation = new WorkOrderOperation();
            operation.setAdmin(workOrder.getEngineerName());
            operation.setWorkOrderId(workOrder.getId());
            operation.setOperation("审核通过");
            operation.setRemark("同意退单");
            operation.setAdmin(updater);
            operation.setCreateTime(now);
            workOrderOperationMapper.insertSelective(operation);
            //同步工单
            syncWorkOrderService.syncWorkOrder(workOrder.getId());
        }

        //订单审核日志
        OrderAuditLog auditLog = new OrderAuditLog();
        auditLog.setSalesId(id);
        auditLog.setOrderId(afterSalesOrder.getOrderId());
        auditLog.setType(AuditTypeEnum.CANCELLATION_ORDER_REFUND.value);
        auditLog.setOperationStatus(true);
        auditLog.setCreator(updater);
        auditLog.setCreateTime(now);
        auditLog.setSubType(auditLogSubType);
        int count = orderAuditLogMapper.insert(auditLog);
        if (count == 0) {
            throw new YimaoException("添加审核记录失败");
        }
    }


    @Override
    public void batchAuditAdopt(List<Long> ids) {
        String updater = userCache.getCurrentAdminRealName();
        for (Long id : ids) {
            auditOrder(id, updater);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    @Override
    public void auditNoPassage(Long id, String auditReason, String detailReason, String updater) {
        AfterSalesOrder afterSalesOrder = afterSalesOrderMapper.selectByPrimaryKey(id);
        if (null == afterSalesOrder) {
            log.error("审核失败，未找到退款订单");
            throw new NotFoundException("未找到退款订单");
        }
        if (afterSalesOrder.getStatus() != OrderSubStatusEnum.PENDING_AUDIT_HEAD.value) {
            log.error("审核失败，售后状态异常");
            throw new YimaoException("审核失败，售后状态异常");
        }
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(afterSalesOrder.getOrderId());
        if (Objects.isNull(orderSub)) {
            log.error("审核失败，订单不存在");
            throw new YimaoException("审核失败，订单不存在");
        }

        Integer auditLogSubType = null; //子审核类型
        WorkOrder workOrder = null; //工单
        Date now = new Date();
        //租赁商品
        if (afterSalesOrder.getProductType() == ProductModeEnum.LEASE.value) {
            auditLogSubType = SubTypeEnum.CUSTOMER_SERVICE_AUDIT.value;
            workOrder = workOrderMapper.findWorkOrderByOrderId(orderSub.getId());
            if (Objects.isNull(workOrder)) {
                throw new YimaoException("审核失败，获取工单信息异常。");
            }

            WorkOrder wo = new WorkOrder();
            wo.setId(workOrder.getId());
            //处理中取消-[只要接单，接单时间]
            if (Objects.nonNull(workOrder.getEngineerId()) && Objects.nonNull(workOrder.getAcceptTime())) {
                orderSub.setSubStatus(null);
                orderSub.setStatus(OrderStatusEnum.PENDING_RECEIPT.value);//待收货
                //是否是上线新流程校验
                wo.setStatus(WorkOrderStateEnum.WORKORDER_STATE_ACCEPTED.state); //老流程-已受理
                if (Objects.nonNull(workOrder.getProvince()) && Objects.nonNull(workOrder.getCity()) && Objects.nonNull(workOrder.getRegion())) {
                    OnlineAreaDTO onlineArea = systemFeign.getOnlineAreaByPCR(workOrder.getProvince(), workOrder.getCity(), workOrder.getRegion());
                    if (onlineArea != null && Objects.equals(onlineArea.getSyncState(), StatusEnum.YES.value())) {
                        wo.setStatus(WorkOrderStateEnum.WORKORDER_STATE_SERVING.state); //新流程-处理中
                    }
                }
                wo.setChargeback(false);
                wo.setStep(0);
                wo.setNextStep(2);//已处理
            } else {//未受理取消
                orderSub.setSubStatus(null);
                orderSub.setStatus(OrderStatusEnum.TO_BE_DELIVERED.value);//待发货
                wo.setStatus(WorkOrderStateEnum.WORKORDER_STATE_NOT_ACCEPTED.state); //未受理
                wo.setStep(0);
                wo.setNextStep(0);
                wo.setChargeback(false);
            }
            wo.setUpdateTime(now);
            workOrderMapper.updateWorkOrder(wo);

            //审核不通过-400
            afterSalesOrder.setCustomerServiceAuditStatus(2);
            afterSalesOrder.setCustomerServiceTime(now);
            afterSalesOrder.setCustomerService(updater);
            afterSalesOrder.setCustomerServiceReason(auditReason);

        } else if (afterSalesOrder.getProductType() == ProductModeEnum.REALTHING.value) {//实物商品
            auditLogSubType = SubTypeEnum.MATERIAL_REVIEW.value;
            //更新订单/工单状态
            orderSub.setStatus(OrderStatusEnum.TO_BE_OUT_OF_STOCK.value);
            orderSub.setSubStatus(null);

            //审核不通过-物资
            afterSalesOrder.setBuyAuditStatus(2);
            afterSalesOrder.setBuyAuditTime(now);
            afterSalesOrder.setBuyer(updater);
            afterSalesOrder.setBuyAuditReason(auditReason);
        }

        afterSalesOrder.setStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);
        afterSalesOrder.setSalesType(AuditTypeEnum.CANCELLATION_ORDER_REFUND.value);
        afterSalesOrder.setAuditee(ReviewerEnum.HEADQUARTERS.name);
        int num = afterSalesOrderMapper.updateByPrimaryKeySelective(afterSalesOrder);
        if (num == 0) {
            throw new YimaoException("售后审核失败");
        }

        //订单更新
        orderSub.setAuditTime(now);
        orderSub.setUpdateTime(now);
        orderSubMapper.updateByPrimaryKey(orderSub);
        //所有操作完成--同步工单
        if (workOrder != null) {
            //保存工单操作记录
            WorkOrderOperation operation = new WorkOrderOperation();
            operation.setAdmin(workOrder.getEngineerName());
            operation.setWorkOrderId(workOrder.getId());
            operation.setOperation("审核不通过");
            operation.setRemark(auditReason);
            operation.setAdmin(updater);
            operation.setCreateTime(now);
            workOrderOperationMapper.insertSelective(operation);
            //同步
            syncWorkOrderService.syncWorkOrder(workOrder.getId());

            //如果存在开票信息，删除
            OrderInvoice invoice = orderInvoiceService.getInvoiceByOrderId(orderSub.getId());
            if (Objects.nonNull(invoice)) {
                orderInvoiceMapper.delete(invoice);
            }
        }

        OrderAuditLog auditLog = new OrderAuditLog();
        auditLog.setSalesId(id);
        auditLog.setOrderId(afterSalesOrder.getOrderId());
        auditLog.setType(AuditTypeEnum.CANCELLATION_ORDER_REFUND.value);
        auditLog.setOperationStatus(false);
        auditLog.setCreator(updater);
        auditLog.setCreateTime(now);
        auditLog.setAuditReason(auditReason);
        auditLog.setDetailReason(detailReason);
        auditLog.setSubType(auditLogSubType);
        int count = orderAuditLogMapper.insert(auditLog);
        if (count == 0) {
            throw new YimaoException("添加审核记录失败");
        }
    }


    @Override
    public void batchAuditNoPassage(List<Long> ids, String auditReason, String detailReason) {
        String updater = userCache.getCurrentAdminRealName();
        for (Long id : ids) {
            auditNoPassage(id, auditReason, detailReason, updater);
        }
    }
}
