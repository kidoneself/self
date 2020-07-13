package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author zhilin.he
 * @description 订单售后审核条件
 * @date 2019/2/13 16:48
 **/
@Data
@ApiModel(description = "订单售后审核条件")
public class OrderRefundAuditDTO implements Serializable {
    private static final long serialVersionUID = 8036777209729774340L;

    @ApiModelProperty(value = "所选售后id集合")
    private String saleOrderIds;
    @ApiModelProperty(value = "审核类型  1.取消订单退款（未收货），2.申请退货退款（已收货），3：提现")
    private Integer auditType;
    @ApiModelProperty(value = "子审核类型  1-业务部门审核，2-400客服审核（租赁商品），3-400客服提交物流，4-物资审核")
    private Integer auditSubType;
    @ApiModelProperty(value = "审核状态：1-审核通过；2-审核不通过")
    private Integer auditStatus;
    @ApiModelProperty(value = "审核不通过原因")
    private String auditReason;
    @ApiModelProperty(value = "详情描述")
    private String detailReason;
    @ApiModelProperty(value = "物流单号")
    private String logisticsNo;
    @ApiModelProperty(value = "物流公司名称")
    private String logisticsCompanyName;
    @ApiModelProperty(value = "用户ip")
    private String ip;

    @ApiModelProperty(value = "售后信息集合")
    private List<OrderSalesInfoDTO> orderSalesInfoList;


}
