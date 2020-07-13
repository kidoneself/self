//package com.yimao.cloud.hra.dto;
//
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * 预约评估列表返回参数
// *
// * @author hhf
// * @date 2019/1/12
// */
//@Data
//public class HraTicketResultDTO implements Serializable {
//
//    private Integer id;
//
//    @ApiModelProperty(value = "体检卡号")
//    private String ticketNo;
//
//    @ApiModelProperty(value = "体检卡类型")
//    private String ticketType;
//
//    @ApiModelProperty(value = "体检状态")
//    private Integer ticketStatus;
//
//    @ApiModelProperty(value = "预约时间")
//    private Date bookTime;
//
//    @ApiModelProperty(value = "来源 ")
//    private Integer reserveFrom;
//
//    @ApiModelProperty(value = "客户ID")
//    private Integer customerId;
//
//    @ApiModelProperty(value = "姓名")
//    private String cUserName;
//
//    @ApiModelProperty(value = "性别")
//    private String cSex;
//
//    @ApiModelProperty(value = "电话")
//    private String cPhone;
//
//    @ApiModelProperty(value = "生日")
//    private String cBirthDate;
//
//    @ApiModelProperty(value = "身份证号")
//    private String cIdCard;
//
//    @ApiModelProperty(value = "年龄")
//    private String cAge;
//
//    @ApiModelProperty(value = "身高")
//    private String cHeight;
//
//    @ApiModelProperty(value = "体重")
//    private String cWeight;
//
//    @ApiModelProperty(value = "服务站Id")
//    private Integer stationId;
//
//    @ApiModelProperty(value = "省")
//    private String sProvince;
//
//    @ApiModelProperty(value = "市")
//    private String sCity;
//
//    @ApiModelProperty(value = "区")
//    private String sRegion;
//
//    @ApiModelProperty(value = "服务站名称")
//    private String sName;
//
//    @ApiModelProperty(value = "体检报告ID")
//    private Integer reportId;
//
//    @ApiModelProperty(value = "体检日期")
//    private Date examDate;
//
//    @ApiModelProperty(value = "体检报告")
//    private String reportPdf;
//
//}
