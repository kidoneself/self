package com.yimao.cloud.pojo.query.order;

import com.yimao.cloud.pojo.query.station.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhilin.he
 * @description 续费工单查询条件
 * @date 2019/5/7 15:31
 **/
@Setter
@Getter
@ApiModel(description = "续费工单查询条件")
public class RenewOrderQuery extends BaseQuery implements Serializable{

    /**
	 *
	 */
	private static final long serialVersionUID = 5731606079534933653L;
	@ApiModelProperty(value = "续费单号")
    private String renewId;
    @ApiModelProperty(value = "设备SN码")
    private String snCode;
    @ApiModelProperty(value = "设备所在省")
    private String province;
    @ApiModelProperty(value = "设备所在市")
    private String city;
    @ApiModelProperty(value = "设备所在区")
    private String region;
    @ApiModelProperty(value = "续费工单支付状态：1-待审核，2-支付成功，3-支付失败")
    private Integer status;
    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;
    @ApiModelProperty(value = "续费开始时间")
    private Date startRenewTime;
    @ApiModelProperty(value = "续费结束时间")
    private Date endRenewTime;
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;

    @ApiModelProperty(value = "支付开始时间")
    private Date startPayTime;
    @ApiModelProperty(value = "支付结束时间")
    private Date endPayTime;

    @ApiModelProperty(value = "产品类目id")
    private Integer productCategoryId;
    @ApiModelProperty(value = "客户联系方式")
    private String waterUserPhone;
    @ApiModelProperty(value = "订单来源：8、广告屏  9、总部业务系统")
    private Integer terminal;
    @ApiModelProperty(value = "支付流水号")
    private String tradeNo;


    @ApiModelProperty(value = "是否是有效续费订单")
    private Boolean isValidRenewOrderNum;

    @ApiModelProperty(value = "提交凭证开始时间")
    private Date startCredentialSubmitTime;
    @ApiModelProperty(value = "提交凭证结束时间")
    private Date endCredentialSubmitTime;
    
    @ApiModelProperty(value = "安装工id集合-站务系统查询")
    private List<Integer> engineerIds;
    
    @ApiModelProperty(value = "经销商id集合-站务系统查询")
    private List<Integer> distributorIds;

}