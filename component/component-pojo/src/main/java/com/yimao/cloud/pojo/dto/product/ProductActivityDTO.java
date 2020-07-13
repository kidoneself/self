package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/***
 * 产品活动DTO对象
 * @author zhangbaobao
 *@date 2020/3/12
 */
@ApiModel
@Data
public class ProductActivityDTO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "活动id")
	private Integer id;
	@ApiModelProperty(value = "活动名称")
	private String name;
	@ApiModelProperty(value = "1.未开始,2.进行中,3.已结束,4.已终止")
	private Integer status;
	@ApiModelProperty(value = "0.未激活,1.已激活")
	private Boolean opening;
	@ApiModelProperty(value = "产品id")
	private Integer productId;
	@ApiModelProperty(value = "活动标签")
	private String label;
	@ApiModelProperty(value = "活动类型：5-限时抢购")
	private Integer activityType;
	@ApiModelProperty(value = "活动库存")
	private Integer activityStock;
	@ApiModelProperty(value = "剩余库存")
	private Integer remainingStock;
	@ApiModelProperty(value = "已售数量")
	private Integer soldNum;
	@ApiModelProperty(value = "活动开始时间")
	private Date startTime;
	@ApiModelProperty(value = "活动结束时间")
	private Date endTime;
	@ApiModelProperty(value = "是否重复周期")
	private Boolean cycle;
	@ApiModelProperty(value = "重复周期类型:1.每天，2.每周，3.每月几号")
	private Integer cycleType;
	@ApiModelProperty(value = "重复时间")
	private String cycleTime;
	@ApiModelProperty(value = "限制状态:0:不限制,1:限制")
	private Boolean limitStatus;
	@ApiModelProperty(value = "限制数量")
	private Integer limitNum;
	@ApiModelProperty(value = "活动价格")
	private BigDecimal activityPrice;
	@ApiModelProperty(value = "活动创建时间")
	private Date createTime;
	@ApiModelProperty(value = "活动创建者")
	private String creator;
	@ApiModelProperty(value = "活动更新时间")
	private Date updateTime;
	@ApiModelProperty(value = "活动更新者")
	private String updater;
	@ApiModelProperty(value = "用于乐观锁，更新已售数量时用到")
	private Integer version;
	private Integer terminal;//来源：1-终端app；2-微信公众号；3-翼猫APP；4-小程序；
}
