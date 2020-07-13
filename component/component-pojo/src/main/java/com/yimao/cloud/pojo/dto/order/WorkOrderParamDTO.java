package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：新建水机安装工单参数信息
 *
 */
@Getter
@Setter
@ApiModel(description = "工单条件参数查询")
public class WorkOrderParamDTO {

    @ApiModelProperty(value = "工单号")
    private String workOrderId;                   //工单号
    @ApiModelProperty(value = "子订单号")
    private Long subOrderId;           //子订单号
    @ApiModelProperty(value = "红包配置id")
    private String redAssemblyId;            //红包配置id
    @ApiModelProperty(value = "红包金额")
    private BigDecimal redAssemlyMoney;            //红包金额
    @ApiModelProperty(value = "用户ID")
    private Integer userId;            //用户ID
    @ApiModelProperty(value = "产品型号id")
    private Integer productModelId;        //产品型号id
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;     //经销商ID
    @ApiModelProperty(value = "安装工ID")
    private Integer engineerId;        //安装工ID
    @ApiModelProperty(value = "区县id")
    private Integer addrRegionId;       //区县id
    @ApiModelProperty(value = "地址")
    private String address;            //地址
    @ApiModelProperty(value = "派单方式：1-用户派单；2-系统派单")
    private Integer dispatchType;      //派单方式：1-用户派单；2-系统派单
    @ApiModelProperty(value = "支付端 1:经销商支付  2:其他(他人代付)  3:用户支付")
    private Integer payTerminal;           //支付端 1:经销商支付  2:其他(他人代付)  3:用户支付
    @ApiModelProperty(value = "开户费用")
    private BigDecimal openAccountFee;            //开户费用
    @ApiModelProperty(value = "派单时间")
    private Date dispatchTime;         //派单时间
    @ApiModelProperty(value = "备注")
    private String remark;             //备注
    @ApiModelProperty(value = "计费方式ID")
    private Integer costId;            //计费方式ID
    @ApiModelProperty(value = "倒计时时间")
    private Date countdownTime;            //倒计时时间
    @ApiModelProperty(value = "完整状态: N、体验工单；Y、安装工单")
    private String completeStatus;                    //完整状态: N、体验工单；Y、安装工单
    @ApiModelProperty(value = "扫描码类型")
    private Integer scanCodeType;                      //扫描码类型
    @ApiModelProperty(value = "是否分配: 0、未分配；1、已分配")
    private Integer isAssign;                    //是否分配: 0、未分配；1、已分配
//    private Integer productId;                  //产品id(微服务指产品类型id)

}
