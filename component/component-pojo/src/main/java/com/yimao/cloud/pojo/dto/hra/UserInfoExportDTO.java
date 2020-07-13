package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoExportDTO implements Serializable {

    private static final long serialVersionUID = -628360843647693645L;

    private String userId;               //用户id
    private String userNick;             //用户昵称
    private String userName;             //用户姓名
    private String realName;             //真实姓名
    private String userMobile;           //用户手机号
    private String userCard;             //用户身份
    private String userProvince;         //用户所在省
    private String userCity;             //用户所在市
    private String userRegion;           //用户所在区
    private String userBindTime;         //用户绑定时间
    private String userCreateTime;       //用户创建时间
    private String userIncomeTime;       //成为分销
    private String userShareTime;        //成为分享

    private String distributorId;       //经销商id、
    private String distributorNick;     //经销商昵称
    private String distributorName;     //经销商账户、
    private String distributorMobile;   //经销商手机号
    private String distributorCard;     //经销商身份、
    private String distributorProvince; //经销商省
    private String distributorCity;     //经销商所在市
    private String distributorRegion;   //经销商所在区
    private String distributorBindTime; //经销商绑定时间

    private String receiverId;            //领取人id
    private String receiverNick;          //领取人昵称
    private String receiverName;          //领取人名称
    private String receiverMobile;        //领取人手机号
    private String receiverType;          //领取人类型
    private String receiverTime;          //领取时间

}
