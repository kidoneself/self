package com.yimao.cloud.pojo.dto.order;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDTO;
import lombok.Data;

import java.util.Date;

/**
 * 移机工单
 *
 * @author Liu Long Jie
 * @date 2020-6-29 10:41:11
 */
@Data
public class MoveWaterDeviceOrderDTO {

    private String id;
    //设备id
    private Integer deviceId;
    //设备sn码
    private String sn;
    //水机设备型号
    private String deviceModel;
    //水机分类 家用/商用
    private String deviceScope;
    //设备用户ID
    private Integer deviceUserId;
    //设备用户姓名
    private String deviceUserName;
    //设备用户手机号
    private String deviceUserPhone;
    //经销商id
    private Integer distributorId;
    //经销商姓名
    private String distributorName;
    //经销商手机号
    private String distributorPhone;
    //设备拆机地省
    private String origProvince;
    //设备拆机地市
    private String origCity;
    //设备拆机地区
    private String origRegion;
    //设备拆机地址
    private String origAddress;
    //拆机地址经度
    private Double origLongitude;
    //拆机地址纬度
    private Double origLatitude;
    //是否客户自主拆机 true-客户自己拆 false-服务人员拆
    private Boolean isClientDismantle;
    //拆机服务人员id
    private Integer dismantleEngineerId;
    //拆机服务人员姓名
    private String dismantleEngineerName;
    //拆机服务人员手机号
    private String dismantleEngineerPhone;
    //拆机服务人员服务站门店id
    private Integer dismantleEngineerStationId;
    //拆机服务人员服务站门店名称
    private String dismantleEngineerStationName;
    //拆机时间（开始）
    private Date startDismantleTime;
    //拆机时间（结束）
    private Date endDismantleTime;
    //移入地省
    private String destProvince;
    //移入地市
    private String destCity;
    //移入地区
    private String destRegion;
    //移入地址
    private String destAddress;
    //移入地址经度
    private Double destLongitude;
    //移入地址纬度
    private Double destLatitude;
    //移入安装人员ID
    private Integer installEngineerId;
    //移入安装人员姓名
    private String installEngineerName;
    //移入安装人员手机号
    private String installEngineerPhone;
    //移入安装人员服务站门店id
    private Integer installEngineerStationId;
    //移入安装人员服务站门店名称
    private String installEngineerStationName;
    //移入时间（开始）
    private Date startInstallTime;
    //移入时间（结束）
    private Date endInstallTime;
    //移机状态 1-待拆机；2-拆机中；3-待移入；4-移入中；5-已完成
    private Integer status;
    //拆机挂单状态 0-未挂单；1-挂单
    private Integer dismantleHangUpStatus;
    //拆机挂单原因
    private String dismantleHangUpCause;
    //装机挂单状态 0-未挂单；1-挂单
    private Integer installHangUpStatus;
    //装机挂单原因
    private String installHangUpCause;
    //真实的拆机开始时间
    private Date realStartDismantleTime;
    //真实的拆机完成时间
    private Date realEndDismantleTime;
    //真实的装机开始时间
    private Date realStartInstallTime;
    //来源方式
    private Integer source;
    //工单完成时间
    private Date completeTime;
    //移机工单创建时间
    private Date createTime;

    private String displayDismantleTime;
    private String displayInstallTime;

    //服务类型 (移出拆机/移入安装)
    private String serviceType;

    private Date createTimeStart;
    private Date createTimeEnd;
    private WaterDeviceDTO waterDeviceDTO;

    //是否跨区 true-是 false-否
    private Boolean isTransRegional;

    //可以更换移出服务人员
    private Boolean mayChangeInstallEngineer;
    //可以更换移入服务人员
    private Boolean mayChangeDismantleEngineer;

    private Integer oldEngineerId;       //更换订单详情的安装工信息用到

}
