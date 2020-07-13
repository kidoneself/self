package com.yimao.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yimao.cloud.base.constant.Constant;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.enums.PayStatus;
import com.yimao.cloud.base.enums.PayType;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.po.OrderMain;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.WorkOrder;
import com.yimao.cloud.order.service.OfflinePayService;
import com.yimao.cloud.order.service.WorkOrderService;
import com.yimao.cloud.pojo.dto.order.WorkOrderDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class OfflinePayServiceImpl implements OfflinePayService {

    @Resource
    private OrderMainMapper orderMainMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private WorkOrderMapper workOrderMapper;
    
    @Resource
    private WorkOrderService workOrderService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void submitCredential(Long mainOrderId, Long subOrderId, String workOrderId, Integer payType, String payCredential) {
        //支付凭证校验
        if (!Constant.IMAGE_PATTERN.matcher(payCredential).matches()) {
            throw new BadRequestException("支付凭证格式不正确");
        }
        if (PayType.find(payType) == null) {
            throw new BadRequestException("支付方式错误");
        }
        Date now = new Date();
        if (subOrderId != null && StringUtil.isNotEmpty(workOrderId)) {
            //安装工APP提交支付凭证
            //更新订单状态为待审核
            OrderSub order = new OrderSub();
            order.setId(subOrderId);
            order.setStatus(OrderStatusEnum.WAITING_AUDIT.value);
            order.setPayStatus(PayStatus.WAITING_AUDIT.value);
            order.setPayType(payType);
            order.setPayCredential(payCredential);
            order.setPayCredentialSubmitTime(now);
            order.setUpdateTime(now);
            int count = orderSubMapper.updateByPrimaryKeySelective(order);
            if (count < 1) {
                log.error("线下支付提交支付凭证失败，subOrderId={}", subOrderId);
                throw new YimaoException("线下支付提交支付凭证失败");
            }
            //更新工单状态为待审核
            WorkOrder workOrder = new WorkOrder();
            workOrder.setId(workOrderId);
            workOrder.setPayType(payType);
            workOrder.setPayStatus(PayStatus.WAITING_AUDIT.value);
            workOrder.setPayCredential(payCredential);
            workOrder.setPayCredentialSubmitTime(now);
            workOrder.setUpdateTime(now);
            count = workOrderMapper.updateByPrimaryKeySelective(workOrder);
            if (count < 1) {
                log.error("线下支付提交支付凭证失败，workOrderId={}", workOrderId);
                throw new YimaoException("线下支付提交支付凭证失败");
            }
        } else {
            if (mainOrderId != null) {
                //更新主订单状态为待审核
                OrderMain update = new OrderMain();
                update.setId(mainOrderId);
                //支付类型：1-微信；2-支付宝；3-POS机；4-转账；
                update.setPayType(payType);
                update.setPayCredential(payCredential);
                update.setPayCredentialSubmitTime(now);
                update.setUpdateTime(now);
                int count = orderMainMapper.updateByPrimaryKeySelective(update);
                if (count < 1) {
                    log.error("线下支付提交支付凭证失败，mainOrderId={}", mainOrderId);
                    throw new YimaoException("线下支付提交支付凭证失败");
                }
                //更新所有子订单状态为待审核
                OrderSub order = new OrderSub();
                order.setMainOrderId(mainOrderId);
                order.setStatus(OrderStatusEnum.WAITING_AUDIT.value);
                order.setPayStatus(PayStatus.WAITING_AUDIT.value);
                order.setPayType(payType);
                order.setPayCredential(payCredential);
                order.setPayCredentialSubmitTime(now);
                order.setUpdateTime(now);
                count = orderSubMapper.updateToWaitingAuditByMainOrderId(order);
                if (count < 1) {
                    log.error("线下支付提交支付凭证失败，mainOrderId={}", mainOrderId);
                    throw new YimaoException("线下支付提交支付凭证失败");
                }
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void otherPay(WorkOrderDTO wo) {
    	
    	log.info("===========otherPayReq======"+JSONObject.toJSONString(wo));
    	//更新工单
    	workOrderService.updateWorkOrder(wo);
    	
    	//更新订单
    	this.submitCredential(wo.getMainOrderId(), wo.getSubOrderId(), wo.getId(), wo.getPayType(), wo.getPayCredential());
		
	}
}
