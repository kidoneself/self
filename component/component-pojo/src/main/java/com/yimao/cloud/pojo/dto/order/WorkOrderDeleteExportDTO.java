package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhilin.he
 * @description 删除工单导出数据
 * @date 2019/4/28 11:58
 **/
@Getter
@Setter
public class WorkOrderDeleteExportDTO {

    private String id;                 //工单号（区分带字母的维修、维护和新老工单）
    private String type;                //工单安装类型
    private String province;           //省
    private String city;               //市
    private String region;             //区县
    private Integer count;             //数量
    private String createTime;         //工单创建时间
    private String stationName;          //服务站名称
    private String deleteTime;             //删除时间
    private String distributorRealName;    //经销商姓名
    private String distributorPhone;     //经销商手机号
    private String userName;             //用户姓名
    private String userPhone;            //用户手机号
    private String userAddress;          //用户手机号
    private String deviceModel;          //设备型号
    private String costName;             //计费方式名称
    private String serviceEngineerName;   //服务工程师姓名
    private String serviceEngineerPhone;  //服务工程师手机号
    private String deleteReason;            //删除原因
    private String deleteRemark;            //删除备注
    private String address;                 //安装地址
    private String subDistributorRealName;   //子经销商姓名
    private String subDistributorName;    //子经销商登录名
    private Long subOrderId;              //子订单号
    private String otherPayType;          //其他支付类型
}
