package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 预约评估列表返回参数
 * @author: yu chunlei
 * @create: 2019-02-12 10:39:48
 **/
@Data
@ApiModel(value = "HraTicketResultDTO",description = "评估")
public class HraTicketResultDTO implements Serializable {

    private static final long serialVersionUID = -8770570917585060556L;
    private Integer id;
    @ApiModelProperty(position = 1,value = "体检卡号")
    private String ticketNo;
    @ApiModelProperty(position = 2,value = "体检卡类型")
    private String ticketType;
    @ApiModelProperty(position = 3,value = "体检状态")
    private Integer ticketStatus;
    @ApiModelProperty(position = 4,value = "预约时间")
    private Date reserveTime;
    @ApiModelProperty(position = 5,value = "分配端")
    private Integer reserveFrom;
    @ApiModelProperty(position = 6,value = "客户ID")
    private Integer customerId;
    @ApiModelProperty(position = 7,value = "姓名")
    private String customerUserName;
    @ApiModelProperty(position = 8,value = "性别")
    private String customerSex;
    @ApiModelProperty(position = 9,value = "电话")
    private String customerPhone;
    @ApiModelProperty(position = 10,value = "生日")
    private String customerBirthDate;
    @ApiModelProperty(position = 11,value = "身份证号")
    private String customerIdCard;
    @ApiModelProperty(position = 12,value = "年龄")
    private String customerAge;
    @ApiModelProperty(position = 13,value = "身高")
    private String customerHeight;
    @ApiModelProperty(position = 14,value = "体重")
    private String customerWeight;
    @ApiModelProperty(position = 15,value = "服务站Id")
    private Integer stationId;
    @ApiModelProperty(position = 16,value = "省")
    private String stationProvince;
    @ApiModelProperty(position = 17,value = "市")
    private String stationCity;
    @ApiModelProperty(position = 18,value = "区")
    private String stationRegion;
    @ApiModelProperty(position = 19,value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 20,value = "体检报告ID")
    private Integer reportId;
    @ApiModelProperty(position = 21,value = "体检日期")
    private Date examDate;
    @ApiModelProperty(position = 22,value = "体检报告")
    private String reportPdf;
    @ApiModelProperty(position = 23,value = "用户身份")
    private Integer userType;//用户等级：0-经销商（体验版），1-经销商（微创版），2-经销商（个人版），3-分享用户（有健康大使），4-普通用户（无健康大使），5-企业版经销商（主账号），6-企业版经销商（子账号），7-分销商
    @ApiModelProperty(position = 24,value = "当前e家号")
    private Integer htUserId;
    @ApiModelProperty(position = 25,value = "到期时间")
    private Date validEndTime;
    @ApiModelProperty(position = 26,value = "是否仅该服务站使用")
    private Boolean selfStation;
    @ApiModelProperty(position = 27,value = "创建时e家号")
    private Integer userId;
    @ApiModelProperty(position = 28,value = "有效期")
    private Date validTime;
    @ApiModelProperty(position = 29,value = "订单号")
    private Long orderId;
    @ApiModelProperty(position = 30,value = "预约状态")
    private String reserveStatus;
}
