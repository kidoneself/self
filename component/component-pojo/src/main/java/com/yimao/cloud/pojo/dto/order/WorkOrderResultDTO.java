package com.yimao.cloud.pojo.dto.order;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/****
 * 工单业务系统返回对象
 * @author zhangbaobao
 *
 */
@Getter
@Setter
@ApiModel(description = "业务系统列表工单对象")
public class WorkOrderResultDTO implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "工单号")
    private String id;                 //工单号（区分带字母的维修、维护和新老工单）
    @ApiModelProperty(value = "主订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long mainOrderId;          //主订单号
    @ApiModelProperty(value = "子订单号")
    @JsonSerialize(using=ToStringSerializer.class)
    private Long subOrderId;           //子订单号
    @ApiModelProperty(value = "工单状态：1-未受理；2-已受理；3-处理中；4-已完成")
    private Integer status;            //工单状态：1-未受理；2-已受理；3-处理中；4-已完成；
    @ApiModelProperty(value = "下单用户id")
    private Integer createUserId;//下单用户id
    @ApiModelProperty(value = "省")
    private String province;           //省
    @ApiModelProperty(value = "市")
    private String city;               //市
    @ApiModelProperty(value = "区县")
    private String region;             //区县
    @ApiModelProperty(value = "地址")
    private String address;            //地址
    @ApiModelProperty(value = "地址详情")
    private String addressDetail;      //地址详情
    @ApiModelProperty(value = "是否需要分配安装工:0-否；1-是")
    private Boolean dispatch;          //是否需要手动分配安装工：0-否；1-是
    @ApiModelProperty(value = "工单创建时间")
    private Date createTime;           //工单创建时间
    @ApiModelProperty(value = "工单来源")
    private Integer terminal;//工单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；
    @ApiModelProperty(value = "安装工服务站")
    private String engineerRegion;//安装工服务站
    @ApiModelProperty(value = "支付端 1:经销商支付  2:用户支付")
    private Integer payTerminal;//支付端 1:经销商支付  2:用户支付
    @ApiModelProperty(value = "支付方式：1-微信；2-支付宝；3-POS机；4-转账")
    private Integer payType;//支付方式：1-微信；2-支付宝；3-POS机；4-转账；
    @ApiModelProperty(value = "是否开票：0-不开票；1-开票")
    private Boolean invoice;//是否开票：0-不开票；1-开票
    @ApiModelProperty(value = "工单类型：  1、经销商添加；2、用户自助下单")
    private Integer type;//工单类型：  1、经销商添加；2、用户自助下单
    @ApiModelProperty(value = "工单来源文本")
    private String terminalText;//工单来源文本
    @ApiModelProperty(value = "扫描码类型：1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城")
    private Integer scanCodeType;//扫描码类型：1、直播二维码；2、企业账号二维码；3、个人二维码；4、软文二维码；5、翼猫微商城
    @ApiModelProperty(value = "安装工服务站")
    private String stationName;//安装工服务站
    @ApiModelProperty(value = "水机数量")
    private Integer count;//水机数量
    @ApiModelProperty(value = "收货人姓名")
    private String addressName;
    @ApiModelProperty(value = "收货人联系方式")
    private String addressPhone;

    @ApiModelProperty(value = "所属经销商姓名")
    private String distributorName;
    @ApiModelProperty(value = "安装工id")
    private String engineerId;
    @ApiModelProperty(value = "安装工姓名")
    private String engineerName;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "退单状态文本")
    private String chargeBackStatusText;//退单状态文本
    }
