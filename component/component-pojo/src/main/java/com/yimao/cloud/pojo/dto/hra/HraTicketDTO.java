package com.yimao.cloud.pojo.dto.hra;

import com.yimao.cloud.pojo.dto.system.StationDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/1.
 */
@Getter
@Setter
@ApiModel(description = "体检劵DTO")
public class HraTicketDTO implements Serializable {

    private static final long serialVersionUID = 1788409593405245456L;

    @ApiModelProperty(position = 1, value = "体检劵ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "体检劵号")
    private String ticketNo;
    @ApiModelProperty(position = 3, value = "每张评估券的价格")
    private BigDecimal ticketPrice;
    @ApiModelProperty(position = 4, value = "状态：1未使用，2已用，3禁用，4过期  5已预约")
    private Integer ticketStatus;
    @ApiModelProperty(position = 5, value = "赠送状态：1-赠送中，2-已被领取")
    private Integer handselStatus;
    @ApiModelProperty(position = 6, value = "评估券类型：Y/F/M")
    private String ticketType;
    @ApiModelProperty(position = 7, value = "若已用，检测的设备编号")
    private String deviceId;
    @ApiModelProperty(position = 8, value = "服务站")
    private Integer stationId;
    @ApiModelProperty(position = 8, value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 8, value = "服务站省")
    private String stationProvince;
    @ApiModelProperty(position = 8, value = "服务站市")
    private String stationCity;
    @ApiModelProperty(position = 8, value = "服务站区")
    private String stationRegion;
    @ApiModelProperty(position = 9, value = "预约的服务站")
    private Integer reserveStationId;
    @ApiModelProperty(position = 10, value = "创建者")
    private String creator;
    @ApiModelProperty(position = 11, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 12, value = "修改时间")
    private Date updateTime;
    @ApiModelProperty(position = 13, value = "使用时间")
    private Date useTime;
    @ApiModelProperty(position = 14, value = "有效期开始时间")
    private Date validBeginTime;
    @ApiModelProperty(position = 15, value = "有效期结束时间")
    private Date validEndTime;
    @ApiModelProperty(position = 16, value = "评估卡ID")
    private String cardId;
    @ApiModelProperty(position = 17, value = "体检客户ID")
    private Integer customerId;
    @ApiModelProperty(position = 18, value = "预约操作来源：1终端用户app，2微信公众号，3评估系统，4经销商APP 5后台管理系统")
    private Integer reserveFrom;
    @ApiModelProperty(position = 19, value = "预约操作的时间")
    private Date reserveTime;
    @ApiModelProperty(position = 20, value = "预约评估的开始日期时间")
    private Date reserveBeginTime;
    @ApiModelProperty(position = 21, value = "预约评估的结束日期时间")
    private Date reserveEndTime;
    @ApiModelProperty(position = 22, value = "备注")
    private String remark;
    @ApiModelProperty(position = 23, value = "用户ID")
    private Integer userId;
    @ApiModelProperty(position = 24, value = "是否删除 1：已删除  0：未删除")
    private Boolean deleteStatus;
    @ApiModelProperty(position = 25, value = "是否只有此服务站可用")
    private Boolean selfStation;
    @ApiModelProperty(position = 26, value = "可用次数")
    private Integer useCount;
    @ApiModelProperty(position = 27, value = "总次数")
    private Integer total;
    @ApiModelProperty(position = 28, value = "赠送时间")
    private Date handselTime;
    @ApiModelProperty(position = 29, value = "接收时间")
    private Date receiveTime;
    @ApiModelProperty(position = 30, value = "预约时间")
    private Date bookTime;
    @ApiModelProperty(position = 31, value = "体检卡样式：1-金色，2-紫色")
    private Integer ticketStyle;
    @ApiModelProperty(position = 32, value = "优惠卡显示的内容")
    private String ticketContent;
    @ApiModelProperty(position = 33, value = "背景图")
    private String image;
    @ApiModelProperty(position = 34, value = "背景图（已使用）")
    private String imageUsed;
    @ApiModelProperty(position = 35, value = "赠送标志,用来保存前端传递过来多的值(用于区分是属于哪一次赠送)")
    private Long handselFlag;
    @ApiModelProperty(position = 36, value = "是否过期")
    private Boolean expiredFlag;
    @ApiModelProperty(position = 37, value = "是否可以赠送")
    private Boolean handsel;

    @ApiModelProperty(position = 38, value = "体检客户")
    private HraCustomerDTO hraCustomer;
    @ApiModelProperty(position = 39, value = "体检报告")
    private HraReportDTO hraReport;
    @ApiModelProperty(position = 40, value = "服务站")
    private StationDTO station;
    @ApiModelProperty(position = 41, value = "体检卡")
    private HraCardDTO hraCard;

    @ApiModelProperty(position = 50, value = "产品ID")
    private Integer productId;
    @ApiModelProperty(position = 51, value = "产品ID")
    private Long orderId;

    @ApiModelProperty(position = 52, value = "体检客户名称")
    private String customerName;
    @ApiModelProperty(position = 53, value = "体检客户手机号")
    private String customerPhone;

}
