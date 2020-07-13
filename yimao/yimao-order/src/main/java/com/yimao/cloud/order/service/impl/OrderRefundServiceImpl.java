package com.yimao.cloud.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yimao.cloud.base.cache.UserCache;
import com.yimao.cloud.base.enums.AuditSubTypeEnum;
import com.yimao.cloud.base.enums.AuditTypeEnum;
import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.enums.OrderSubStatusEnum;
import com.yimao.cloud.base.exception.BadRequestException;
import com.yimao.cloud.base.exception.NotFoundException;
import com.yimao.cloud.base.exception.YimaoException;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.base.utils.StringUtil;
import com.yimao.cloud.order.mapper.AfterSalesStatusRecordMapper;
import com.yimao.cloud.order.mapper.OrderAuditLogMapper;
import com.yimao.cloud.order.mapper.OrderRefundMapper;
import com.yimao.cloud.order.mapper.OrderStatusRecordMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.order.po.AfterSalesStatusRecord;
import com.yimao.cloud.order.po.OrderAuditLog;
import com.yimao.cloud.order.po.OrderStatusRecord;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.service.OrderRefundService;
import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderRefundAuditDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
@Service
@Slf4j
public class OrderRefundServiceImpl implements OrderRefundService {

    @Resource
    private OrderRefundMapper orderRefundMapper;
    @Resource
    private OrderSubMapper orderSubMapper;
    @Resource
    private OrderAuditLogMapper orderAuditLogMapper;
    @Resource
    private UserCache userCache;
    @Resource
    private OrderStatusRecordMapper orderStatusRecordMapper;
    @Resource
    private AfterSalesStatusRecordMapper afterSalesStatusRecordMapper;


    /**
     * @description   查询订单列表,根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/11 13:56
     * @param orderConditionDTO   订单查询条件
     * @param pageNum   当前页
     * @param pageSize  每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderInfoDTO
     */
    @Override
    public PageVO<OrderSalesInfoDTO> orderSalesList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize) {
        if (orderConditionDTO == null) {
            throw new BadRequestException("查询条件不能为空，其中有必填字段！");
        }
        //售后类型：1.取消订单退款（未收货），表示仅退款；2.申请退货退款（已收货），表示退款退货
        if (orderConditionDTO.getSalesType() == null) {
            throw new BadRequestException("售后类型不能为空！");
        }
        //商品类型（大类）:1实物商品，2电子卡券，3租赁商品，判断是租赁商品菜单还是，实物商品菜单
        if (orderConditionDTO.getProductType() == null) {
            throw new BadRequestException("商品类型不能为空！");
        }
        log.info("售后类型:" + orderConditionDTO.getSalesType() + ",商品类型:" + orderConditionDTO.getProductType());
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderSalesInfoDTO> ptPage = orderRefundMapper.orderSalesList(orderConditionDTO);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * @description   根据售后id查询售后订单详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    @Override
    public OrderSalesInfoDTO orderRefundInfo(Long id) {
        try {
            return orderRefundMapper.orderRefundInfo(id);
        } catch (Exception e) {
            log.error(" 根据售后id查询售后信息失败！");
            throw new NotFoundException(" 根据售后id查询售后信息失败！");
        }
    }

    /**
     * @description   查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    @Override
    public OrderSalesInfoDTO orderRefundAuditInfo(Long id) {
        try {
            return orderRefundMapper.orderRefundAuditInfo(id);
        } catch (Exception e) {
            log.error(" 根据售后审核id查询售后审核或处理记录信息失败！");
            throw new NotFoundException(" 根据售后审核id查询售后审核或处理记录信息失败！");
        }
    }

    /**
     * @description   售后状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param salesAuditStatus
     * @return void
     */
    @Override
    public void updateSalesAuditStatus(Long salesId,Integer salesAuditStatus){
    //根据订单号查询订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        orders.setStatus(salesAuditStatus);
        int result = orderRefundMapper.updateByPrimaryKeySelective(orders);
        if (result < 1) {
            throw new YimaoException("订单删除失败!");
        }
    }

    /**
     * @description   业务部门审核状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param businessAuditStatus
     * @param adminName
     * @param reason
     * @return void
     */
    @Override
    public void updateBusinessAuditStatus(Long salesId,Integer businessAuditStatus,String adminName,String reason){
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        orders.setBusinessAuditStatus(businessAuditStatus);
        orders.setBusinessAuditTime(new Date());
        orders.setBusinessman(adminName);
        orders.setBusinessReason(reason);
        int result = orderRefundMapper.updateByPrimaryKeySelective(orders);
        if (result < 1) {
            throw new YimaoException("业务部门审核状态变更失败!");
        }
    }

    /**
     * @description   物资部门审核状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param buyAuditStatus
     * @param userName
     * @param reason
     * @return void
     */
    @Override
    public void updateBuyAuditStatus(Long salesId,Integer buyAuditStatus,String userName,String reason){
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        orders.setBuyAuditStatus(buyAuditStatus);
        orders.setBuyAuditTime(new Date());
        orders.setBuyer(userName);
        orders.setBuyAuditReason(reason);
        int result = orderRefundMapper.updateByPrimaryKeySelective(orders);
        if (result < 1) {
            throw new YimaoException("物资部门审核状态变更失败!");
        }
    }

    /**
     * @description   财务部门审核状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param financeAuditStatus
     * @param userName
     * @param reason
     * @return void
     */
    @Override
    public void updateFinanceAuditStatus(Long salesId,Integer financeAuditStatus,String userName,String reason){
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        orders.setFinanceAuditStatus(financeAuditStatus);
        orders.setFinanceTime(new Date());
        orders.setFinancer(userName);
        orders.setFinanceReason(reason);
        int result = orderRefundMapper.updateByPrimaryKeySelective(orders);
        if (result < 1) {
            throw new YimaoException("财务部门审核状态变更失败!");
        }
    }

    /**
     * @description   400客服审核状态或提交物流变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param customerServiceAuditStatus
     * @param userName
     * @param reason
     * @return void
     */
    @Override
    public void updateCustomerServiceAuditStatus(Long salesId,Integer customerServiceAuditStatus,String userName,String reason){
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        orders.setCustomerServiceAuditStatus(customerServiceAuditStatus);
        orders.setCustomerServiceTime(new Date());
        orders.setCustomerService(userName);
        orders.setCustomerServiceReason(reason);
        int result = orderRefundMapper.updateByPrimaryKeySelective(orders);
        if (result < 1) {
            throw new YimaoException("400客服审核状态或提交物流变更失败!");
        }
    }


    /**
     * @description   关闭退货
     * @author zhilin.he
     * @date 2019/1/28 15:49
     * @param id
     * @return void
     */
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void orderRefundClose(Long id,String userName) {
        //根据订单号查询售后订单
        AfterSalesOrder orderRefunds = new AfterSalesOrder();
        orderRefunds.setOrderId(id);
        AfterSalesOrder orderRefund = orderRefundMapper.selectOne(orderRefunds);
        if (orderRefund == null) {
            log.error("根据订单id查询不到售后订单,订单ID：" + id);
            throw new NotFoundException("根据订单id查询不到售后订单！");
        }
        //物质确认审核状态：1-审核通过；2-审核不通过（1-收货确认；2-未确认）
        if(orderRefund.getBuyAuditStatus() == 1){
            throw new NotFoundException("该售后订单已确认收货，不可关闭退货了！");
        }
        //根据订单号查询订单
        OrderSub orderSub = orderSubMapper.selectByPrimaryKey(id);
        if (orderSub == null) {
            log.error("根据订单id查询不到订单,订单ID：" + id);
            throw new NotFoundException("根据订单id查询不到订单！");
        }

        orderSub.setStatus(OrderStatusEnum.SUCCESSFUL_TRADE.value);
        orderSub.setSubStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);
        orderSub.setUpdateTime(new Date());
        int orderResult = orderSubMapper.updateByPrimaryKeySelective(orderSub);
        if (orderResult < 1) {
            throw new YimaoException("关闭退货，修改订单状态失败!");
        }
        //添加订单状态变更记录
        OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
        orderStatusRecord.setId(id);
        orderStatusRecord.setCreateTime(new Date());
        orderStatusRecord.setCreator(userName);
        orderStatusRecord.setDestStatus(OrderStatusEnum.SUCCESSFUL_TRADE.value);//变更之后的状态
        orderStatusRecord.setOrigStatus(OrderStatusEnum.AFTER_SALE.value);//变更之前的状态
        orderStatusRecord.setRemark("关闭退货,回退到原交易成功状态！");
        int result = orderStatusRecordMapper.insert(orderStatusRecord);
        if (result < 1) {
            throw new YimaoException("关闭退货,添加订单状态变更记录失败!");
        }
        Integer salesStatus = orderRefund.getStatus();
        orderRefund.setStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);
        int orderRefundResult = orderRefundMapper.updateByPrimaryKeySelective(orderRefund);
        if (orderRefundResult < 1) {
            throw new YimaoException("关闭退货，修改订单售后状态失败!");
        }
        //添加订单售后状态变更记录
        AfterSalesStatusRecord afterSalesStatusRecord = new AfterSalesStatusRecord();
        afterSalesStatusRecord.setId(id);
        afterSalesStatusRecord.setCreateTime(new Date());
        afterSalesStatusRecord.setCreator(userName);
        afterSalesStatusRecord.setDestStatus(OrderSubStatusEnum.AFTER_SALE_FAILURE.value);//变更之后的状态
        afterSalesStatusRecord.setOrigStatus(salesStatus);//变更之前的状态
        afterSalesStatusRecord.setRemark("关闭退货,售后失败！");
        int afterResult = afterSalesStatusRecordMapper.insert(afterSalesStatusRecord);
        if (afterResult < 1) {
            throw new YimaoException("关闭退货,添加订单售后状态变更记录失败!");
        }
    }

    /**
     * @description  售后审核（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     * @param orderRefundAuditDTO
     * @param userDTO
     */
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void refundAuditBatch(OrderRefundAuditDTO orderRefundAuditDTO, String adminName) {
        if (StringUtil.isEmpty(orderRefundAuditDTO.getSaleOrderIds())) {
            throw new BadRequestException("请选择要批量审核的售后单号！");
        }
        if (orderRefundAuditDTO.getAuditType() == null) {
            throw new BadRequestException("审核类型参数不能为空！");
        }
        if (orderRefundAuditDTO.getAuditSubType() == null) {
            throw new BadRequestException("子审核类型参数不能为空！");
        }
        if (orderRefundAuditDTO.getAuditStatus() == null) {
            throw new BadRequestException("审核状态参数不能为空！");
        }
        if (StringUtil.isEmpty(orderRefundAuditDTO.getLogisticsCompanyName())) {
            throw new BadRequestException("物流公司名称参数不能为空！");
        }
        if (StringUtil.isEmpty(orderRefundAuditDTO.getLogisticsNo())) {
            throw new BadRequestException("物流单号参数不能为空！");
        }

        List<String> idList = StringUtil.spiltListByStr(orderRefundAuditDTO.getSaleOrderIds());
        if (CollectionUtil.isNotEmpty(idList)) {
            List<Long> ids = idList.stream().map(Long::parseLong).collect(Collectors.toList());
            ids.stream().forEach(salesId -> refundAudit(salesId,orderRefundAuditDTO, adminName));
        }
    }

    /**
     * @description  售后审核
     */
    public void refundAudit(Long salesId,OrderRefundAuditDTO orderRefundAuditDTO, String adminName) {
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(salesId);
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + salesId);
            throw new NotFoundException("售后单号不存在");
        }
        String operation = "";
        String menuName = "";
        //审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现
        if(orderRefundAuditDTO.getAuditType() == AuditTypeEnum.CANCELLATION_ORDER_REFUND.value){
            //子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核
            if(orderRefundAuditDTO.getAuditSubType() == AuditSubTypeEnum.CUSTOMER_SERVICE_AUDIT.value){
                orders.setCustomerServiceAuditStatus(orderRefundAuditDTO.getAuditStatus());
                orders.setCustomerService(adminName);
                orders.setCustomerServiceTime(new Date());
                orders.setCustomerServiceReason(orderRefundAuditDTO.getAuditReason());
                int result = orderRefundMapper.updateByPrimaryKey(orders);
                if (result < 1) {
                    throw new BadRequestException("售后退款租赁商品400客服审核失败!");
                }
                if(orderRefundAuditDTO.getAuditStatus() == 1){
                    operation = "审核通过";
                }else if(orderRefundAuditDTO.getAuditStatus() == 2){
                    operation = "审核不通过";
                }
                menuName = "退款审核";
            }else if(orderRefundAuditDTO.getAuditSubType() == AuditSubTypeEnum.MATERIAL_AUDIT.value){
                orders.setBuyAuditStatus(orderRefundAuditDTO.getAuditStatus());
                orders.setBuyer(adminName);
                orders.setBuyAuditTime(new Date());
                orders.setBuyAuditReason(orderRefundAuditDTO.getAuditReason());
                int result = orderRefundMapper.updateByPrimaryKey(orders);
                if (result < 1) {
                    throw new BadRequestException("售后退款实物商品物资部门审核失败!");
                }
                if(orderRefundAuditDTO.getAuditStatus() == 1){
                    operation = "审核通过";
                }else if(orderRefundAuditDTO.getAuditStatus() == 2){
                    operation = "审核不通过";
                }
                menuName = "退款审核";
            }
        }else if(orderRefundAuditDTO.getAuditType()  == AuditTypeEnum.APPLICATION_FOR_REFUND.value){
            //子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核
            if(orderRefundAuditDTO.getAuditSubType() == AuditSubTypeEnum.BUSINESS_AUDIT.value){
                orders.setBusinessAuditStatus(orderRefundAuditDTO.getAuditStatus());
                orders.setBusinessman(adminName);
                orders.setBusinessAuditTime(new Date());
                orders.setBusinessReason(orderRefundAuditDTO.getAuditReason());
                orderRefundMapper.updateByPrimaryKey(orders);
                int result = orderRefundMapper.updateByPrimaryKey(orders);
                if (result < 1) {
                    throw new BadRequestException("售后退款退货业务部门审核失败!");
                }
                if(orderRefundAuditDTO.getAuditStatus() == 1){
                    operation = "审核通过";
                }else if(orderRefundAuditDTO.getAuditStatus() == 2){
                    operation = "审核不通过";
                }
                menuName = "退货审核";
            }else if(orderRefundAuditDTO.getAuditSubType() == AuditSubTypeEnum.MATERIAL_AUDIT.value){
                orders.setCustomerServiceAuditStatus(orderRefundAuditDTO.getAuditStatus());
                orders.setCustomerService(adminName);
                orders.setCustomerServiceTime(new Date());
                orders.setCustomerServiceReason(orderRefundAuditDTO.getAuditReason());
                orderRefundMapper.updateByPrimaryKey(orders);
                if(orderRefundAuditDTO.getAuditStatus() == 1){
                    operation = "已提交";
                }else if(orderRefundAuditDTO.getAuditStatus() == 2){
                    operation = "未提交";
                }
                menuName = "提交物流";
            }else if(orderRefundAuditDTO.getAuditSubType() == AuditSubTypeEnum.CUSTOMER_SERVICE_LOGISTICS.value){
                orders.setBuyAuditStatus(orderRefundAuditDTO.getAuditStatus());
                orders.setBuyer(adminName);
                orders.setBuyAuditTime(new Date());
                orders.setBuyAuditReason(orderRefundAuditDTO.getAuditReason());
                int result = orderRefundMapper.updateByPrimaryKey(orders);
                if (result < 1) {
                    throw new BadRequestException("售后退款退货400客服提交物流信息失败!");
                }
                if(orderRefundAuditDTO.getAuditStatus() == 1){
                    operation = "收货确认";
                }else if(orderRefundAuditDTO.getAuditStatus() == 2){
                    operation = "未确认";
                }
                menuName = "退货审核";
            }
        }
        //添加审核日志或处理记录
        OrderAuditLog orderAuditLog = new OrderAuditLog();
        orderAuditLog.setSalesId(orders.getId());
        orderAuditLog.setOrderId(orders.getOrderId());
        orderAuditLog.setType(orderRefundAuditDTO.getAuditType());
        orderAuditLog.setSubType(orderRefundAuditDTO.getAuditSubType());
        orderAuditLog.setOperation(operation);
        orderAuditLog.setMenuName(menuName);
        orderAuditLog.setIp(orderRefundAuditDTO.getIp());
        orderAuditLog.setCreator(adminName);
        orderAuditLog.setCreateTime(orders.getBuyAuditTime());
        orderAuditLog.setAuditReason(orderRefundAuditDTO.getAuditReason());
        orderAuditLog.setDetailReason(orderRefundAuditDTO.getDetailReason());
        orderAuditLogMapper.insert(orderAuditLog);
    }



    /**
     * @description   查询提交物流列表根据订单售后id集合
     * @author zhilin.he
     * @date 2019/1/11 13:56
     * @param saleOrderIds   订单售后id集合
     * @param pageNum   当前页
     * @param pageSize  每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    @Override
    public PageVO<OrderSalesInfoDTO> orderLogisticSubmitList(String saleOrderIds, Integer pageNum, Integer pageSize) {
        List<String> idList = StringUtil.spiltListByStr(saleOrderIds);
        List<Long> ids = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(idList)) {
            ids = idList.stream().map(Long::parseLong).collect(Collectors.toList());
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderSalesInfoDTO> ptPage = orderRefundMapper.orderLogisticSubmitList(ids);
        return new PageVO<>(pageNum, ptPage);
    }

    /**
     * @description  提交物流（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     * @param orderRefundAuditDTO
     * @param userDTO
     */
    @Transactional(rollbackFor = {Exception.class}, propagation = Propagation.REQUIRED)
    @Override
    public void refundAuditLogisticBatch(OrderRefundAuditDTO orderRefundAuditDTO, String adminName) {
        if (StringUtil.isEmpty(orderRefundAuditDTO.getSaleOrderIds())) {
            throw new BadRequestException("请选择要批量审核的售后单号！");
        }
        if (orderRefundAuditDTO.getAuditType() == null) {
            throw new BadRequestException("审核类型参数不能为空！");
        }
        if (orderRefundAuditDTO.getAuditSubType() == null) {
            throw new BadRequestException("子审核类型参数不能为空！");
        }
        if (orderRefundAuditDTO.getAuditStatus() == null) {
            throw new BadRequestException("审核状态参数不能为空！");
        }
        if (CollectionUtil.isEmpty(orderRefundAuditDTO.getOrderSalesInfoList())) {
            throw new BadRequestException("售后订单物流信息不能为空！");
        }
        orderRefundAuditDTO.getOrderSalesInfoList().stream().forEach(orderSalesInfoDTO -> refundAuditLogistic(orderSalesInfoDTO,orderRefundAuditDTO, adminName));
    }

    /**
     * @description  提交物流
     */
    public void refundAuditLogistic(OrderSalesInfoDTO orderSalesInfoDTO,OrderRefundAuditDTO orderRefundAuditDTO, String adminName) {
        if (orderSalesInfoDTO == null) {
            throw new BadRequestException("售后订单物流信息不能为空！");
        }
        if (StringUtil.isEmpty(orderSalesInfoDTO.getLogisticsCompanyName())) {
            throw new BadRequestException("物流公司名称参数不能为空！");
        }
        if (StringUtil.isEmpty(orderSalesInfoDTO.getLogisticsNo())) {
            throw new BadRequestException("物流单号参数不能为空！");
        }
        //根据售后单号查询售后订单
        AfterSalesOrder orders = orderRefundMapper.selectByPrimaryKey(orderSalesInfoDTO.getId());
        if (orders == null) {
            log.error("根据售后id查询不到订单,售后ID：" + orderSalesInfoDTO.getId());
            throw new NotFoundException("售后单号不存在");
        }
        String operation = "";
        String menuName = "";
        //审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现
        //子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核
        orders.setCustomerServiceAuditStatus(orderRefundAuditDTO.getAuditStatus());
        orders.setCustomerService(adminName);
        orders.setCustomerServiceTime(new Date());
        orders.setCustomerServiceReason(orderRefundAuditDTO.getAuditReason());
        int result = orderRefundMapper.updateByPrimaryKey(orders);
        if (result < 1) {
            throw new BadRequestException("售后退款退货400客服提交物流信息失败!");
        }
        if(orderRefundAuditDTO.getAuditStatus() == 1){
            operation = "已提交";
        }else if(orderRefundAuditDTO.getAuditStatus() == 2){
            operation = "未提交";
        }
        menuName = "提交物流";

        //添加审核日志或处理记录
        OrderAuditLog orderAuditLog = new OrderAuditLog();
        orderAuditLog.setSalesId(orders.getId());
        orderAuditLog.setOrderId(orders.getOrderId());
        orderAuditLog.setType(AuditTypeEnum.APPLICATION_FOR_REFUND.value);
        orderAuditLog.setSubType(AuditSubTypeEnum.CUSTOMER_SERVICE_LOGISTICS.value);
        orderAuditLog.setOperation(operation);
        orderAuditLog.setMenuName(menuName);
        orderAuditLog.setIp(orderRefundAuditDTO.getIp());
        orderAuditLog.setCreator(adminName);
        orderAuditLog.setCreateTime(orders.getBuyAuditTime());
        orderAuditLog.setAuditReason(orderRefundAuditDTO.getAuditReason());
        orderAuditLog.setDetailReason(orderRefundAuditDTO.getDetailReason());
        orderAuditLogMapper.insert(orderAuditLog);
    }

    /**
     * @description   查询售后订单审核记录或处理记录列表
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @return java.lang.Object
     */
    @Override
    public PageVO<OrderSalesInfoDTO> orderRefundAuditList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize) {
        if (orderConditionDTO == null) {
            throw new BadRequestException("查询条件不能为空，其中有必填字段！");
        }
        //审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现
        if (orderConditionDTO.getAuditType() == null) {
            throw new BadRequestException("审核类型不能为空！");
        }
        //子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核(收货确认)
        if (orderConditionDTO.getAuditSubType() == null) {
            throw new BadRequestException("子审核类型不能为空！");
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<OrderSalesInfoDTO> ptPage = orderRefundMapper.orderRefundAuditList(orderConditionDTO);
        return new PageVO<>(pageNum, ptPage);
    }



}
