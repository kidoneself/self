package com.yimao.cloud.pojo.dto.hra;

import com.yimao.cloud.pojo.dto.system.StationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-02-20 14:45:32
 **/
@Data
public class HraAllotTicketDTO implements Serializable {


    @ApiModelProperty(position = 1,value = "id")
    private Integer id;
    @ApiModelProperty(position = 2,value = "卡号")
    private String ticketNo;
    @ApiModelProperty(position = 3,value = "每张评估券的价格")
    private BigDecimal ticketPrice;
    @ApiModelProperty(position = 4,value = "状态：1未使用，2已用，3禁用，4过期  5已预约")
    private Integer ticketStatus;
    @ApiModelProperty(position = 5,value = "赠送状态：1-赠送中，2-已被领取")
    private Integer handselStatus;
    @ApiModelProperty(position = 6,value = "评估券类型：Y/F/M")
    private String ticketType;
    @ApiModelProperty(position = 7,value = "若已用，检测的设备编号")
    private String deviceId;
    @ApiModelProperty(position = 8,value = "创建者")
    private String creator;
    @ApiModelProperty(position = 9,value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 10,value = "评估卷使用的服务站")
    private Integer stationId;
    @ApiModelProperty(position = 11,value = "使用时间")
    private Date useTime;
    @ApiModelProperty(position = 12,value = "有效期开始时间")
    private Date validBeginTime;
    @ApiModelProperty(position = 13,value = "有效期结束时间")
    private Date validEndTime;
    @ApiModelProperty(position = 14,value = "评估卡ID")
    private String cardId;
    @ApiModelProperty(position = 15,value = "顾客表关联字段，评估卷关联到预约用户或评估用户")
    private Integer customerId;
    @ApiModelProperty(position = 16,value = "预约操作来源：1终端用户app，2微信公众号，3评估系统，4经销商APP 5后台管理系统")
    private Integer reserveFrom;
    @ApiModelProperty(position = 17,value = "预约操作的时间")
    private Date reserveTime;
    @ApiModelProperty(position = 18,value = "预约评估的开始日期时间")
    private Date reserveBeginTime;
    @ApiModelProperty(position = 19,value = "预约评估的结束日期时间")
    private Date reserveEndTime;
    @ApiModelProperty(position = 20,value = "备注")
    private String remark;
    @ApiModelProperty(position = 21,value = "用户ID")
    private Integer userId;
    @ApiModelProperty(position = 22,value = "是否只有此服务站可用")
    private Boolean selfStation;
    @ApiModelProperty(position = 23,value = "可用次数")
    private Integer useCount;
    @ApiModelProperty(position = 24,value = "总次数")
    private Integer total;
    @ApiModelProperty(position = 25,value = "体检卡样式：1-金色，2-紫色")
    private Integer ticketStyle;
    @ApiModelProperty(position = 26,value = "优惠卡显示的内容")
    private String ticketContent;
    @ApiModelProperty(position = 27,value = "背景图")
    private String image;
    @ApiModelProperty(position = 28,value = "背景图（已使用）")
    private String imageUsed;
    @ApiModelProperty(position = 29,value = "是否过期")
    private Boolean hasExpire;
    @ApiModelProperty(position = 30,value = "是否可以赠送")
    private boolean handsel;


    @ApiModelProperty(position = 31,value = "卡ID")
    private String hcId;
    @ApiModelProperty(position = 32,value = "卡类型")
    private String hcCardType;
    @ApiModelProperty(position = 33,value = "经销商ID")
    private Integer distributorId;
    @ApiModelProperty(position = 34,value = "经销商ID")
    private Integer hcUserId;
    @ApiModelProperty(position = 35,value = "经销商ID")
    private Long orderId;
    @ApiModelProperty(position = 36,value = "经销商ID")
    private String hcCreator;
    @ApiModelProperty(position = 37,value = "经销商ID")
    private Date hcCreateTime;
    @ApiModelProperty(position = 38,value = "订单来源：1终端app，2微信公众号，3管理后台")
    private Integer orderFrom;
    @ApiModelProperty(position = 39,value = "评估卡的销售价格")
    private BigDecimal cardPrice;
    @ApiModelProperty(position = 40,value = "评估卡类型：1 购买型，2 服务站演示用卡，3 免费赠送卡")
    private Integer productModelId;
    @ApiModelProperty(position = 41,value = "生成评估券的数量")
    private Integer ticketNum;
    @ApiModelProperty(position = 42,value = "是否删除 1：已删除  0：未删除")
    private Boolean hcDeleteStatus;


    @ApiModelProperty(position = 43,value = "评估卡")
    private HraCardDTO hraCard;

    @ApiModelProperty(position = 44,value = "服务站门店")
    private StationDTO station;//

    @ApiModelProperty(position = 45,value = "有效天数")
    private Integer days;

}
