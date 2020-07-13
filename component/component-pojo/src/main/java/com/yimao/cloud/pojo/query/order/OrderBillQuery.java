package com.yimao.cloud.pojo.query.order;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：订单对账查询条件实体类。
 *
 * @Author Zhang Bo
 * @Date 2019/5/29
 */
@Getter
@Setter
public class OrderBillQuery implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品公司id，为0标识所有产品公司")
    private Integer productCompanyId;

    @ApiModelProperty(value = "订单号查询（主订单号或子订单号）")
    private Long idKey;

    @ApiModelProperty(value = "订单状态：0-待付款；1-待审核；2-待发货；3-待出库；4-待收货；5-交易成功；6-售后中；7-交易关闭；8-已取消；")
    private Integer status;

    @ApiModelProperty(value = "后台产品三级类目id")
    private Integer productCategoryId;

    @ApiModelProperty(value = "下单用户id")
    private Integer userId;

    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；")
    private Integer terminal;

    @ApiModelProperty(value = "第三方支付流水号")
    private String tradeNo;

    @ApiModelProperty(value = "支付类型：1-微信；2-支付宝；3-POS机；4-转账；")
    private Integer payType;

    @ApiModelProperty(value = "1-实物商品；2-电子卡券；3-租赁商品")
    private Integer mode;

    @ApiModelProperty(value = "支付开始时间")
    private String beginPayTime;

    @ApiModelProperty(value = "支付结束时间")
    private String endPayTime;
}
