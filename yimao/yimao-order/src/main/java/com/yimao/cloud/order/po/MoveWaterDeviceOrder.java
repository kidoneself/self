package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.MoveWaterDeviceOrderDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 移机工单
 *
 * @author Liu Long Jie
 * @date 2020-6-29 10:41:11
 */
@Data
@Table(name = "move_water_device_order")
public class MoveWaterDeviceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    public MoveWaterDeviceOrder() {
    }

    /**
     * 用业务对象MoveWaterDeviceOrderDTO初始化数据库对象MoveWaterDeviceOrder。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MoveWaterDeviceOrder(MoveWaterDeviceOrderDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.sn = dto.getSn();
        this.deviceModel = dto.getDeviceModel();
        this.deviceScope = dto.getDeviceScope();
        this.deviceUserId = dto.getDeviceUserId();
        this.deviceUserName = dto.getDeviceUserName();
        this.deviceUserPhone = dto.getDeviceUserPhone();
        this.distributorId = dto.getDistributorId();
        this.distributorName = dto.getDistributorName();
        this.distributorPhone = dto.getDistributorPhone();
        this.origProvince = dto.getOrigProvince();
        this.origCity = dto.getOrigCity();
        this.origRegion = dto.getOrigRegion();
        this.origAddress = dto.getOrigAddress();
        this.origLongitude = dto.getOrigLongitude();
        this.origLatitude = dto.getOrigLatitude();
        this.isClientDismantle = dto.getIsClientDismantle();
        this.dismantleEngineerId = dto.getDismantleEngineerId();
        this.dismantleEngineerName = dto.getDismantleEngineerName();
        this.dismantleEngineerPhone = dto.getDismantleEngineerPhone();
        this.dismantleEngineerStationId = dto.getDismantleEngineerStationId();
        this.dismantleEngineerStationName = dto.getDismantleEngineerStationName();
        this.startDismantleTime = dto.getStartDismantleTime();
        this.endDismantleTime = dto.getEndDismantleTime();
        this.destProvince = dto.getDestProvince();
        this.destCity = dto.getDestCity();
        this.destRegion = dto.getDestRegion();
        this.destAddress = dto.getDestAddress();
        this.destLongitude = dto.getDestLongitude();
        this.destLatitude = dto.getDestLatitude();
        this.installEngineerId = dto.getInstallEngineerId();
        this.installEngineerName = dto.getInstallEngineerName();
        this.installEngineerPhone = dto.getInstallEngineerPhone();
        this.installEngineerStationId = dto.getInstallEngineerStationId();
        this.installEngineerStationName = dto.getInstallEngineerStationName();
        this.startInstallTime = dto.getStartInstallTime();
        this.endInstallTime = dto.getEndInstallTime();
        this.status = dto.getStatus();
        this.dismantleHangUpStatus = dto.getDismantleHangUpStatus();
        this.dismantleHangUpCause = dto.getDismantleHangUpCause();
        this.installHangUpStatus = dto.getInstallHangUpStatus();
        this.installHangUpCause = dto.getInstallHangUpCause();
        this.realStartDismantleTime = dto.getRealStartDismantleTime();
        this.realEndDismantleTime = dto.getRealEndDismantleTime();
        this.realStartInstallTime = dto.getRealStartInstallTime();
        this.source = dto.getSource();
        this.completeTime = dto.getCompleteTime();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MoveWaterDeviceOrderDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MoveWaterDeviceOrderDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setSn(this.sn);
        dto.setDeviceModel(this.deviceModel);
        dto.setDeviceScope(this.deviceScope);
        dto.setDeviceUserId(this.deviceUserId);
        dto.setDeviceUserName(this.deviceUserName);
        dto.setDeviceUserPhone(this.deviceUserPhone);
        dto.setDistributorId(this.distributorId);
        dto.setDistributorName(this.distributorName);
        dto.setDistributorPhone(this.distributorPhone);
        dto.setOrigProvince(this.origProvince);
        dto.setOrigCity(this.origCity);
        dto.setOrigRegion(this.origRegion);
        dto.setOrigAddress(this.origAddress);
        dto.setOrigLongitude(this.origLongitude);
        dto.setOrigLatitude(this.origLatitude);
        dto.setIsClientDismantle(this.isClientDismantle);
        dto.setDismantleEngineerId(this.dismantleEngineerId);
        dto.setDismantleEngineerName(this.dismantleEngineerName);
        dto.setDismantleEngineerPhone(this.dismantleEngineerPhone);
        dto.setDismantleEngineerStationId(this.dismantleEngineerStationId);
        dto.setDismantleEngineerStationName(this.dismantleEngineerStationName);
        dto.setStartDismantleTime(this.startDismantleTime);
        dto.setEndDismantleTime(this.endDismantleTime);
        dto.setDestProvince(this.destProvince);
        dto.setDestCity(this.destCity);
        dto.setDestRegion(this.destRegion);
        dto.setDestAddress(this.destAddress);
        dto.setDestLongitude(this.destLongitude);
        dto.setDestLatitude(this.destLatitude);
        dto.setInstallEngineerId(this.installEngineerId);
        dto.setInstallEngineerName(this.installEngineerName);
        dto.setInstallEngineerPhone(this.installEngineerPhone);
        dto.setInstallEngineerStationId(this.installEngineerStationId);
        dto.setInstallEngineerStationName(this.installEngineerStationName);
        dto.setStartInstallTime(this.startInstallTime);
        dto.setEndInstallTime(this.endInstallTime);
        dto.setStatus(this.status);
        dto.setDismantleHangUpStatus(this.dismantleHangUpStatus);
        dto.setDismantleHangUpCause(this.dismantleHangUpCause);
        dto.setInstallHangUpStatus(this.installHangUpStatus);
        dto.setInstallHangUpCause(this.installHangUpCause);
        dto.setRealStartDismantleTime(this.realStartDismantleTime);
        dto.setRealEndDismantleTime(this.realEndDismantleTime);
        dto.setRealStartInstallTime(this.realStartInstallTime);
        dto.setSource(this.source);
        dto.setCompleteTime(this.completeTime);
        dto.setCreateTime(this.createTime);
    }
}
