package com.yimao.cloud.pojo.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 移机工单客户端返回类
 *
 * @author Liu Long Jie
 * @date 2020-6-29 10:41:11
 */
@Data
public class MoveWaterDeviceOrderVO {

    private String id;
    //水机设备型号
    private String deviceModel;
    //设备用户姓名
    private String deviceUserName;
    //设备用户手机号
    private String deviceUserPhone;
    //经销商姓名
    private String distributorName;
    //经销商手机号
    private String distributorPhone;
    //服务地址
    private String serviceAddress;
    //移出地址
    private String origAddress;
    //移入地址
    private String destAddress;
    //服务地址经度
    private Double longitude;
    //服务地址纬度
    private Double latitude;
    //服务时间（开始）
    private Date serviceStartTime;
    //服务时间（结束）
    private Date serviceEndTime;
    private String displayServiceTime;
    //移机工单创建时间
    private Date createTime;
    //工单完成时间
    private Date completeTime;
    //服务类型 (移出拆机/移入安装)
    private String serviceType;
    //挂单原因
    private String hangUpCause;

    //距离
    private Double distance;

    //拆机服务人员姓名
    private String dismantleEngineerName;
    //拆机服务人员联系方式
    private String dismantleEngineerPhone;
    //拆机服务人员所属服务站门店名称
    private String dismantleEngineerStationName;
    //移入安装人员姓名
    private String installEngineerName;
    //移入安装人员联系方式
    private String installEngineerPhone;
    //移入安装人员所属服务站门店名称
    private String installEngineerStationName;

    //设备sn码
    private String sn;
    //生产批次码
    private String logisticsCode;
    //运营商ICCID：ICCID号码段+号码
    private String iccid;
    //当前设备的计费方式 1-流量计费；2-包年计费；
    private String currentCostName;
    //设备余额
    private BigDecimal money;

    //拆机挂单原因
    private String dismantleHangUpCause;
    //拆机时间（开始）
    private Date startDismantleTime;
    //拆机时间（结束）
    private Date endDismantleTime;
    private String displayDismantleTime;
    //装机挂单原因
    private String installHangUpCause;
    //移入时间（开始）
    private Date startInstallTime;
    //移入时间（结束）
    private Date endInstallTime;
    private String displayInstallTime;

    //拆机服务按钮可点标识 true 为可点
    private Boolean mayDismantleServiceButton;
    //继续拆机服务按钮可点标识 true 为可点
    private Boolean mayContinueDismantleServiceButton;
    //等待拆机按钮可点标识 true 为可点
    private Boolean mayWaitDismantleServiceButton;
    //移入安装按钮可点标识 true 为可点
    private Boolean mayInstallServiceButton;
    //继续移入安装按钮可点标识 true 为可点
    private Boolean mayContinueInstallServiceButton;
    //等待移入地处理按钮可点标识 true 为可点
    private Boolean mayWaitInstallServiceButton;

}
