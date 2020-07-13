package com.yimao.cloud.order.service;

import com.yimao.cloud.pojo.dto.order.OrderConditionDTO;
import com.yimao.cloud.pojo.dto.order.OrderRefundAuditDTO;
import com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO;
import com.yimao.cloud.pojo.vo.PageVO;

/**
 * @author Zhang Bo
 * @date 2018/11/12.
 */
public interface OrderRefundService {

    /**
     * @description   查询售后订单列表根据订单查询条件
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @param pageNum   当前页
     * @param pageSize  每页显示条数
     * @return java.lang.Object
     */
    PageVO<OrderSalesInfoDTO> orderSalesList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

    /**
     * @description    根据售后id查询订单售后详情
     * @author zhilin.he
     * @date 2019/1/28 15:23
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    OrderSalesInfoDTO orderRefundInfo(Long id);

    /**
     * @description   查询订单售后审核或处理记录详情
     * @author zhilin.he
     * @date 2019/2/13 10:39
     * @param id
     * @return com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    OrderSalesInfoDTO orderRefundAuditInfo(Long id);

    /**
     * @description   关闭退货
     * @author zhilin.he
     * @date 2019/1/28 15:49
     * @param id
     * @return void
     */
    void orderRefundClose(Long id,String userName);

    /**
     * @description   售后状态变更
     * @author zhilin.he
     * @date 2019/1/29 15:39
     * @param salesId
     * @param salesAuditStatus
     * @return void
     */
    void updateSalesAuditStatus(Long salesId,Integer salesAuditStatus);

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
    void updateBusinessAuditStatus(Long salesId,Integer businessAuditStatus,String adminName,String reason);

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
    void updateBuyAuditStatus(Long salesId,Integer buyAuditStatus,String userName,String reason);

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
    void updateFinanceAuditStatus(Long salesId,Integer financeAuditStatus,String userName,String reason);

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
    void updateCustomerServiceAuditStatus(Long salesId,Integer customerServiceAuditStatus,String userName,String reason);

    /**
     * @description  售后审核（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     * @param orderRefundAuditDTO
     * @param userDTO
     */
    void refundAuditBatch(OrderRefundAuditDTO orderRefundAuditDTO, String adminName);


    /**
     * @description   查询提交物流列表根据订单售后id集合
     * @author zhilin.he
     * @date 2019/1/11 13:56
     * @param saleOrderIds   订单售后id集合
     * @param pageNum   当前页
     * @param pageSize  每页显示条数
     * @return com.yimao.cloud.pojo.vo.PageVO<com.yimao.cloud.pojo.dto.order.OrderSalesInfoDTO
     */
    PageVO<OrderSalesInfoDTO> orderLogisticSubmitList(String saleOrderIds, Integer pageNum, Integer pageSize);

    /**
     * @description  提交物流（批量）
     * @author zhilin.he
     * @date 2019/2/13 16:02
     * @param orderRefundAuditDTO
     * @return org.springframework.http.ResponseEntity
     */
    void refundAuditLogisticBatch(OrderRefundAuditDTO orderRefundAuditDTO, String adminName);

    /**
     * @description   查询售后订单审核记录或处理记录列表
     * @author zhilin.he
     * @date 2019/1/12 13:48
     * @param orderConditionDTO   订单查询条件
     * @return java.lang.Object
     */
    PageVO<OrderSalesInfoDTO> orderRefundAuditList(OrderConditionDTO orderConditionDTO, Integer pageNum, Integer pageSize);

}
