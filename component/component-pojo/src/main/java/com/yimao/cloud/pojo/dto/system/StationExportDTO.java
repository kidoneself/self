package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xiaozepeng on 2018/7/19.
 * 服务站承包信息导出实体类DTO
 */
@Data
public class StationExportDTO implements Serializable {

    private Integer id;
    private String code;                                //服务站门店编号
    private String stationName;                         //服务站名称
    private String stationCompanyName;                  //服务站公司名称
    private String type;                                //服务站门店类型
    private String province;                            //所在区域（省）
    private String city;                                //所在区域（市）
    private String region;                              //所在区域（区）
    private String address;                             //门店详细地址
    private String serviceProvince;                     //服务区域（省）
    private String serviceCity;                         //服务区域（市）
    private String serviceRegion;                       //服务区域（区）
    private String serviceTypeStr;                      //服务类型
    private String contact;                             //联系人
    private String contactPhone;                        //联系人手机号
    private String contract;                            //承包状态
    private String contractStartTime;                   //承包时间
    private String contractEndTime;                     //承包时间
    private String contractor;                          //承包人姓名
    private String contractorPhone;                     //承包人手机号
    private String contractorIdCard;                    //承包人身份证号
    private String masterName;                          //站长姓名
    private String masterPhone;                         //站长手机号
    private String masterIdCard;                        //站长身份证号
    private String masterAccount;                       //站长账号
    private String createTime;                          //服务站门店成立时间
    private String businessHoursStart;                  //服务站门店营业时间
    private String businessHoursEnd;                    //服务站门店营业时间
    private String employeeNum;                         //员工数量
    private String stationArea;                         //门店规模
    private String online;                              //上线状态
    private String onlineTime;                          //上线时间


}
