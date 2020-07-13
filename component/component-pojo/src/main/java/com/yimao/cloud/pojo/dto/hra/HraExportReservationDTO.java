package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/5/15
 */

@Data
public class HraExportReservationDTO {

    private String stationProvince;     //省
    private String stationCity;         //市
    private String stationRegion;       //区
    private String ticketNo;            //体检卡
    private Integer userId;             //翼家号
    private String customerUserName;    //体检人姓名
    private String customerIdCard;      //体检人证件号
    private String customerPhone;       //体检人手机号
    private String customerSex;         //体检人性別
    private String customerHeight;      //体检人身高
    private String customerWeight;      //体检人体重
    private String customerBirthDate;   //体检人生日
    private String reserveTime;            //预约体检时间
    private String reserveStatus;       //预约状态
    private String reserveFrom;         //用户来源
    private Integer stationId;          //服务站ID
    private String ticketType;          //体检卡型号
    private String ticketStatus;
    private String examDate;

}
