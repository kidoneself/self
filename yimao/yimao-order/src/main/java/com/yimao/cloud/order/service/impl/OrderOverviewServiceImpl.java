package com.yimao.cloud.order.service.impl;

import com.yimao.cloud.base.enums.OrderStatusEnum;
import com.yimao.cloud.base.utils.CollectionUtil;
import com.yimao.cloud.order.mapper.AfterSalesOrderMapper;
import com.yimao.cloud.order.mapper.OrderMainMapper;
import com.yimao.cloud.order.mapper.OrderRenewMapper;
import com.yimao.cloud.order.mapper.OrderSubMapper;
import com.yimao.cloud.order.mapper.ReimburseManageMapper;
import com.yimao.cloud.order.mapper.WithdrawSubMapper;
import com.yimao.cloud.order.mapper.WorkOrderMapper;
import com.yimao.cloud.order.po.AfterSalesOrder;
import com.yimao.cloud.order.po.OrderSub;
import com.yimao.cloud.order.po.WithdrawSub;
import com.yimao.cloud.order.service.OrderOverviewService;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderOverviewServiceImpl implements OrderOverviewService {

    @Resource
    private AfterSalesOrderMapper afterSalesOrderMapper;

    @Resource
    private OrderRenewMapper orderRenewMapper;

    @Resource
    private WithdrawSubMapper WithdrawSubMapper;

    @Resource
    private OrderSubMapper orderSubMapper;
    
    @Resource
    private OrderMainMapper orderMainMapper;
    
    @Resource
    private WorkOrderMapper workOrderMapper;
    
    @Resource
    private ReimburseManageMapper reimburseManageMapper;

    /**
     * 待办事项统计
     *
     * @return Map
     * @author hhf
     * @date 2019/3/22
     */
    @Override
    public Map<String, Long> orderOverview() {
        Map<String, Long> map = new HashMap<>();
        // 待发货统计列表 待出库订单统计
        List<Map<String, Object>> mapList = orderSubMapper.countDeliveryOrder();
        if (CollectionUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> objectMap : mapList) {
                if (OrderStatusEnum.TO_BE_DELIVERED.value == (int) objectMap.get("status")) {
                    map.put("toBeDelivered", (Long) objectMap.get("count"));
                } else {
                    map.put("toBeOutOfStock", (Long) objectMap.get("count"));
                }
            }
        }
        // 仅退款（租赁商品取消待审核,实物商品待审核）,退款退货（退货待审核,待提交物流,待确认收货）
        map.put("leaseProductToAudit", countAfterSalesOrder4Condition(1, 3, 1, null));
        map.put("realProductToAudit", countAfterSalesOrder4Condition(1, 1, 1, null));
        map.put("returnProductToAudit", countAfterSalesOrder4Condition(2, 1, 1, null));
        map.put("submitLogistics", countAfterSalesOrder4Condition(2, 1, 2, 2));
        map.put("confirmReceipt", countAfterSalesOrder4Condition(2, 1, 2, 1));

        // 财务处理
        // 支付待审核
        map.put("payAudit", countOrderSub4Pay());
        // 续费支付待审核
        map.put("renewAudit", orderRenewMapper.countRenewOrder4Audit());
        // 提现待审核
        map.put("withdrawSubAudit", countWithdrawSub());
        // 线上退款待审核，线下退款待审核
        // map.put("onlineToAudit", afterSalesOrderMapper.countReturn4Audit(1));
        // map.put("offlineToAudit", afterSalesOrderMapper.countReturn4Audit(2));
        map.put("onlineToAudit", (long)reimburseManageMapper.onlineReimburseManageCount(1));
        map.put("offlineToAudit", (long)reimburseManageMapper.onlineReimburseManageCount(2));
        return map;
    }


    /**
     * 退款退货订单统计
     *
     * @param salesType   售后类型：1.取消订单退款（未收货），2.申请退货退款（已收货）
     * @param productType 商品类型（大类）:1实物商品，2电子卡券，3租赁商品
     * @param status      状态:1-待审核(物资)   2-待退货入库  3-待退款(财务)  4-售后失败 5-售后成功
     * @param auditStatus 400客服审核状态或提交物流：1-审核通过；2-审核不通过（1-已提交物流，2-未提交）
     * @return java.lang.Long
     * @author hhf
     * @date 2019/3/23
     */
    private Long countAfterSalesOrder4Condition(Integer salesType, Integer productType, Integer status, Integer auditStatus) {

//        Example example = new Example(AfterSalesOrder.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("salesType", salesType);
//        criteria.andEqualTo("productType", productType);
//        criteria.andEqualTo("status", status);
//        criteria.andIn("", values);
//        if (auditStatus != null) {
//            criteria.andEqualTo("customerServiceAuditStatus", auditStatus);
//        }
        return (long) afterSalesOrderMapper.selectCountAfterSalesOrder4Condition(salesType,productType,status,auditStatus);
    }

    /**
     * 统计支付待审核
     *
     * @return java.lang.Long
     * @author hhf
     * @date 2019/3/23
     */
    private Long countOrderSub4Pay() {
//        Example example = new Example(OrderSub.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("deleted", 0);
//        criteria.andEqualTo("payStatus", 2);
//        return (long) orderSubMapper.selectCountByExample(example);
    	//立即支付审核树
    	int orderMainCount=orderMainMapper.selectPayCheckCount();
    	//货到付款审核数
    	int workOrderCount=workOrderMapper.selectDeliveryPayCheckCount();
    	return (long) (orderMainCount+workOrderCount);
    }

    /**
     * 统计带提现订单
     *
     * @return java.lang.Long
     * @author hhf
     * @date 2019/3/23
     */
    private Long countWithdrawSub() {
        Example example = new Example(WithdrawSub.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", 3);
        return (long) WithdrawSubMapper.selectCountByExample(example);
    }

    @Override
    public BusinessProfileDTO orderOverviewBusiness() {
        BusinessProfileDTO dto = new BusinessProfileDTO();
        // 有效销售总额,有效订单总数,
        Map<String, Object> totalBusiness = orderSubMapper.selectTotalBusiness(null);
        if (totalBusiness != null) {
            dto.setSaleTotal((BigDecimal) totalBusiness.get("fee"));
            dto.setOrderTotal(((Long) totalBusiness.get("count")).intValue());
        }
        // 昨日有效销售额,昨日订单数
        Map<String, Object> map = orderSubMapper.selectTotalBusiness(1);
        if (map != null) {
            dto.setYestSaleTotal((BigDecimal) map.get("fee"));
            dto.setYestOrderTotal(((Long) map.get("count")).intValue());
        }
        //下单次数 成交笔数
        dto.setBargainTotal(orderSubMapper.selectCount4Pay());
        dto.setBuyTotal(orderSubMapper.selectCount4Create());
        // 获取前五的一级类目产品销售金额和销售数量
        List<BusinessProfileDetailDTO> list = orderSubMapper.selectBusiness();
        dto.setDetails(list);
        return dto;
    }
}
