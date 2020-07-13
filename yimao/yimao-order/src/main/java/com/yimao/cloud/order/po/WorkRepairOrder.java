package com.yimao.cloud.order.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "work_repair_order")
public class WorkRepairOrder {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	//维修工单号
    private String workOrderNo;
    //工单状态 0-待维修 1-挂单 2-维修中 3-已完成
    private Integer status;
    //维修处理中步骤 1-故障确认 2-故障维修 3-确认提交
    private Integer step;
    //设备id
    private Integer deviceId;
    //设备型号
    private String deviceModel;
    //产品类目id（用于查询耗材）
    private Integer productCategoryId;

    private String sn;
    //发起故障类型id
    private Integer faultId;
    //发起故障类型名称
    private String faultName;
    //安装工确认故障类型id
    private Integer confirmFaultId;
    //安装工确认故障类名称
    private String confirmFaultName;
    //水机所属经销商id
    private Integer distributorId;
    //水机所属经销商姓名
    private String distributorName;
    //水机所属经销商手机号
    private String distributorPhone;
    //安装工id
    private Integer engineerId;
    //安装工名称
    private String engineerName;
    //安装工服务站id
    private Integer stationId;
    //安装工服务站名称   
    private String stationName;
    //水机所在省
    private String province;
    //水机所在市
    private String city;
    //水机所在区
    private String region;
    //水机用户id
    private Integer deviceUserId;
    //水机用户用户名
	private String deviceUserName;
	//水机用户手机号
	private String deviceUserPhone;
    //水机所在地址
    private String address;
    //水机所在经度
    private String longitude;
    //水机所在纬度
    private String latitude;
    //维修确认故障正面照片
    private String frontImage;
    //维修确认故障后面照片
    private String backImage;
    //维修确认故障右面照片
    private String rightImage;
    //维修确认故障左面照片
    private String leftImage;
    //是否更换耗材 0-否 1-是
    private Boolean isChangeMaterial;
    //挂单改约理由
    private String hangRemark;
    //备注
    private String remark;
    //来源 0-安装工app创建 1-后台业务系统创建 2-水机故障推送创建
    private Integer sourceType;
    //维修工单发起时间
    private Date launchTime;
    //挂单改约发起时间
    private Date hangTime;
    //改约服务开始时间
    private Date revisionStartTime;
    //改约服务结束时间
    private Date revisionEndTime;
    //服务设置开始时间
    private Date serviceStartTime;
    //服务设置结束时间
    private Date serviceEndTime;
    //安装工服务时间
    private Date engineerServiceTime;
    //安装工服务完成时间
    private Date engineerFinishTime;
    //系统手动创建人
    private String systemCreator;

}