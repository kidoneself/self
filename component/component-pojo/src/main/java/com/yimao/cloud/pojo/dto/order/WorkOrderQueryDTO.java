package com.yimao.cloud.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * @author zhilin.he
 * @description 工单条件查询
 * @date 2019/3/20 17:46
 **/
@Getter
@Setter
@ApiModel(description = "工单条件查询")
public class WorkOrderQueryDTO implements Serializable {
    private static final long serialVersionUID = -6927458680529455450L;

    @ApiModelProperty(value = "工单号")
    private String id;                 //工单号
    @ApiModelProperty(value = "主订单号")
    private Long mainOrderId;          //主订单号
    @ApiModelProperty(value = "子订单号")
    private Long subOrderId;           //子订单号
    @ApiModelProperty(value = "用户ID")
    private Integer userId;            //用户ID
    @ApiModelProperty(value = "用户姓名")
    private String userName;           //用户姓名
    @ApiModelProperty(value = "用户手机号")
    private String userPhone;          //用户手机号
    @ApiModelProperty(value = "经销商ID")
    private Integer distributorId;     //经销商ID
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;    //经销商姓名
    @ApiModelProperty(value = "经销商手机号")
    private String distributorPhone;   //经销商手机号
    @ApiModelProperty(value = "省")
    private String province;           //省
    @ApiModelProperty(value = "市")
    private String city;               //市
    @ApiModelProperty(value = "区县")
    private String region;             //区县
    @ApiModelProperty(value = "服务站ID")
    private Integer stationId;          //服务站ID

    @ApiModelProperty(value = "安装工ID")
    private Integer engineerId;             //安装工ID
    @ApiModelProperty(value = "安装工姓名")
    private Integer engineerName;           //安装工姓名
    @ApiModelProperty(value = "是否退单")
    private String isBackWorkOrder;        //是否退单
    @ApiModelProperty(value = "工单状态")
    private Integer status;                 //工单状态
    @ApiModelProperty(value = "工单类型：  1、经销商添加；2、用户自助下单")
    private Integer type;                   //工单类型：  1、经销商添加；2、用户自助下单
    @ApiModelProperty(value = "退单状态1:退单中；2：退单成功；3：待退单；N、未退单；")
    private String backOrderStatus;         //退单状态1:退单中；2：退单成功；3：待退单；N、未退单；
    @ApiModelProperty(value = "退款状态")
    private Integer backRefundStatus;       //退款状态
    @ApiModelProperty(value = "拒单状态: N、未拒单；Y、已拒单")
    private String notRefuse;               //拒单状态: N、未拒单；Y、已拒单
    @ApiModelProperty(value = "详细状态")
    private String detailStatus;            //详细状态
    @ApiModelProperty(value = "公共字段")
    private String search;                  //公共字段
    @ApiModelProperty(value = "操作类型：1、工单列表；2、退单列表；3、删除列表")
    private Integer operationType;          //操作类型：1、工单列表；2、退单列表；3、删除列表

    @ApiModelProperty(value = "接单状态：N、否；Y、是")
    private String acceptStatus;            //接单状态：N、否；Y、是
    @ApiModelProperty(value = "安装工是否预约：N、否；Y、是")
    private String appointStatus;            //安装工是否预约：N、否；Y、是
    @ApiModelProperty(value = "其他支付审核状态：0-审核中、1-审核通过、2-审核未通过")
    private String accountingStatus;         //其他支付审核状态
    @ApiModelProperty(value = "合同状态")
    private String signStatus;               //合同状态
    @ApiModelProperty(value = "是否支付：0-否；1-是")
    private Boolean pay;                     //是否支付：0-否；1-是
    @ApiModelProperty(value = "支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败")
    private Integer payStatus;             //支付状态：1、未支付；2、待审核；3、支付成功；4、支付失败
    @ApiModelProperty(value = "同步百得状态: N、未同步；Y、已同步")
    private String syncBaideStatus;
    @ApiModelProperty(value = "SN码")
    private String snCode;
    @ApiModelProperty(value = "sim卡")
    private String simCard;
    @ApiModelProperty(value = "物流编号")
    private String logisticsCode;


    @ApiModelProperty(value = "开始时间", example = "2018-12-28 11:00:00")
    private Date startTime;                //开始时间
    @ApiModelProperty(value = "结束时间", example = "2018-12-28 11:00:00")
    private Date endTime;                  //结束时间
    @ApiModelProperty(value = "完成时间", example = "2018-12-28 11:00:00")
    private Date completeTime;             //完成时间
    @ApiModelProperty(value = "支付时间", example = "2018-12-28 11:00:00")
    private Date payTime;                  //支付时间
    @ApiModelProperty(value = "结算月份", example = "2018-12")
    private String accountMonth;             //结算月份
    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;               //支付类型
    @ApiModelProperty(value = "工单来源:0、经销商APP添加；1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城；")
    private Integer terminal;              //工单来源
    @ApiModelProperty(value = "经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）")
    private Integer distributorRoleLevel;   //经销商角色等级
    @ApiModelProperty(value = "扫描码类型：1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城；")
    private Integer scanCodeType;           //扫描码类型
    @ApiModelProperty(value = "工单取消开始时间", example = "2018-12-28 11:00:00")
    private Date cancelStartTime;           //工单取消开始时间
    @ApiModelProperty(value = "工单取消结束时间", example = "2018-12-28 11:00:00")
    private Date cancelEndTime;             //工单取消结束时间
    @ApiModelProperty(value = "完成类型: 1、正常完成；2、自动完成")
    private Integer completeType;          //完成类型: 1、正常完成；2、自动完成
    @ApiModelProperty(value = "是否未分配：true、未分配；false、已分配")
    private Boolean isNotAllot;            //是否未分配：true、未分配；false、已分配。
    @ApiModelProperty(value = "工单删除开始时间", example = "2018-12-28 11:00:00")
    private Date deleteStartTime;           //工单删除开始时间
    @ApiModelProperty(value = "工单删除结束时间", example = "2018-12-28 11:00:00")
    private Date deleteEndTime;             //工单删除结束时间
    @ApiModelProperty(value = "产品ID")
    private Integer productId;              //产品ID
    @ApiModelProperty(value = "工单安装到了第几步骤")
    private Integer step;                   //工单安装到了第几步骤
    @ApiModelProperty(value = "完成状态: N、未完成；Y、已完成")
    private String completeStatus;         //完成状态: N、未完成；Y、已完成
    @ApiModelProperty(value = "倒计时时间")
    private Date countdownTime;            //倒计时时间
    @ApiModelProperty(value = "老的子订单号")
    private String oldSubOrderId;
    @ApiModelProperty(value = "老的经销商ID")
    private String oldDistributorId;
    @ApiModelProperty(value = "支付开始时间")
    private Date startPayTime;
    @ApiModelProperty(value = "支付结束时间")
    private Date endPayTime;

    @ApiModelProperty(value = "支付类型 1-立即支付 ，2线下支付 ")
    private Integer payTerminal;

    private Integer state; //工单状态：1-未受理 2-处理中 3-已完成
    private String signContractStatus;    //用户是否签署合同：N、否；Y、是

    //业务系统查询工单条件
    @ApiModelProperty(value = "付款开始时间", example = "2018-12-28 11:00:00")
    private Date payStartTime;//支付开始时间
    @ApiModelProperty(value = "付款开始时间", example = "2018-12-28 11:00:00")
    private Date payEndTime;//支付结束日期
    @ApiModelProperty(value = "工单完成开始时间", example = "2018-12-28 11:00:00")
    private Date finishStartTime;//工单开始时间
    @ApiModelProperty(value = "工单完成截止时间", example = "2018-12-28 11:00:00")
    private Date finishEndTime;//工单结束时间
    @ApiModelProperty(value = "完款开始时间", example = "2018-12-28 11:00:00")
    private Date finishMoneyStartTime;//完款开始时间
    @ApiModelProperty(value = "完款截止开始时间", example = "2018-12-28 11:00:00")
    private Date finishMoneyEndTime;//完款结束时间
    @ApiModelProperty(value = "收益类型:1-产品收益，2-续费收益")
    private Integer incomeType;//收益类型
    private String delStatus;//删除状态N:未删除,Y:已删除



    @ApiModelProperty(position = 100, value = "工单号")
    private String workOrderId;
    @ApiModelProperty(position = 100, value = "工单号")
    private String orderId;
    @ApiModelProperty(position = 100, value = "退单状态")
    private Integer chargeBackStatus;//0,1,2
    
    private Integer pageSize;
    private String queryTime;
}
