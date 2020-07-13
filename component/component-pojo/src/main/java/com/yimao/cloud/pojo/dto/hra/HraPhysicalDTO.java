package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 体检列表数据封装
 * @author: yu chunlei
 * @create: 2019-02-26 16:20:51
 **/
@Data
public class HraPhysicalDTO implements Serializable {

    private static final long serialVersionUID = -8884353255993120273L;
    private Integer id;
    @ApiModelProperty(position = 1,value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(position = 2,value = "创建时e家号")
    private Integer createUserId;
    @ApiModelProperty(position = 3,value = "当前e家号")
    private Integer currentUserId;
    @ApiModelProperty(position = 4,value = "用户身份:0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商")
    private Integer userType;
    @ApiModelProperty(position = 5,value = "卡型号：Y/M")
    private String cardType;     //卡型号：Y/M
    @ApiModelProperty(position = 6,value = "体检卡状态：1未使用，2已用，3禁用，4过期  5已预约")
    private Integer ticketStatus;//状态：1未使用，2已用，3禁用，4过期  5已预约
    @ApiModelProperty(position = 7,value = "有效期结束时间")
    private Date validEndTime;    //有效期结束时间
    @ApiModelProperty(position = 8,value = "创建时间")
    private Date createTime;    //创建时间
    @ApiModelProperty(position = 9,value = "1终端app，2微信公众号，3管理后台")
    private Integer orderFrom;
    @ApiModelProperty(position = 10,value = "订单号")
    private Long orderId;   //订单
    @ApiModelProperty(position = 11,value = "可用次数")
    private Integer useCount;   //可用次数
    @ApiModelProperty(position = 12,value = "服务站ID")
    private Integer stationId;//

    @ApiModelProperty(position = 13,value = "服务站名称")
    private String stationName;   //
    @ApiModelProperty(position = 14,value = "身份证号码")
    private String idCard;
    @ApiModelProperty(position = 15,value = "有效期天数")
    private Integer validDays;
    @ApiModelProperty(position = 16,value = "是否限制仅该服务站使用")
    private Boolean selfStation;
    @ApiModelProperty(position = 17,value = "有效期开始时间")
    private Date validBeginTime;    //有效期开始时间
    @ApiModelProperty(position = 18,value = "预约操作来源")
    private Integer reserveFrom;       //预约操作来源：1终端用户app，2微信公众号，3评估系统，4经销商APP 5后台管理系统

    @ApiModelProperty(position = 19,value = "客户ID")
    private Integer customerId;

    private String cardId;






}
