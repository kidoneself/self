package com.yimao.cloud.pojo.vo.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动产品返回结果对象
 *
 * @author zhangbaobao
 * @date 2020/3/12
 */
@ApiModel
@Data
public class ProductActivityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动id")
    private Integer id;
    @ApiModelProperty(value = "活动名称")
    private String name;
    @ApiModelProperty(value = "活动状态：1.未开始,2.进行中,3.已结束,4.已终止")
    private Integer status;
    @ApiModelProperty(value = "0.未激活,1.已激活")
    private Boolean opening;
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    @ApiModelProperty(value = "标签名称")
    private String label;
    @ApiModelProperty(value = "活动类型")
    private Integer activityType;
    @ApiModelProperty(value = "活动库存")
    private Integer activityStock;
    @ApiModelProperty(value = "本轮剩余数量")
    private Integer remainingStock;
    @ApiModelProperty(value = "本轮已售数量")
    private Integer soldNum;
    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;
    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;
    @ApiModelProperty(value = "是否周期重复:0.否，1.是")
    private Boolean cycle;
    @ApiModelProperty(value = "1.每天，2.每周，3.每月几号，如果选择了是周期重复，该字段非空")
    private Integer cycleType;
    @ApiModelProperty(value = "重复周期的日期格式{\"startTime\": \"hh:mm:ss\",\"endTime\": \"hh:mm:ss\",\"weekDay\": \"1\",\"day\": 1}")
    private String cycleTime;
    @ApiModelProperty(value = "限制状态:0:不限制,1:限制")
    private Boolean limitStatus;
    @ApiModelProperty(value = "限制数量")
    private Integer limitNum;
    @ApiModelProperty(value = "活动价格")
    private BigDecimal activityPrice;
    @ApiModelProperty(value = "用于乐观锁，更新已售数量时用到")
    private Integer version;
    @ApiModelProperty(value = "端")
    private Integer terminal;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "产品图片")
    private String coverImg;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "商品正常售卖价格")
    private BigDecimal price;
    @ApiModelProperty(value = "活动开始倒计时或者结束倒计时")
    private Long countdownTime;
    @ApiModelProperty(value = "货运方式 : 1包邮，2 货到付款'")
    private Integer transportType;
    @ApiModelProperty(value = "订单实付金额")
    private BigDecimal orderAmountFee;
    @ApiModelProperty(value = "产品状态")
    private Integer productStatus;
    @ApiModelProperty(value = "产品库存")
    private Integer stock;


}
