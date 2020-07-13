package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraDeviceDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
@Table(name = "hra_device")
@Getter
@Setter
public class HraDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String deviceId;            //体检设备唯一编号
    private Integer deviceType;         //体检设备类型：1-I型，2-II型
    private Integer deviceStatus;       //设备状态：1可用，2锁定
    private String deviceDesc;          //设备描述
    private String stationName;         //服务站名称，来自hra客户端体检软件提交数据
    private String stationAddress;      //服务站地址，来自hra客户端体检软件提交数据
    private String stationTel;          //服务站联系电话，来自hra客户端体检软件提交数据
    private String remark;              //备注
    private Integer productType;        //产品类型
    private Integer stationId;          //服务站ID
    private Integer stationCompanyId;   //区县级公司ID
    private String stationCompanyName;  //区县级公司名称
    private String province;            //省
    private String city;                //市
    private String region;              //区

    protected String creator;
    protected Date createTime;
    protected String updater;
    protected Date updateTime;

    public HraDevice() {
    }

    /**
     * 用业务对象HraDeviceDTO初始化数据库对象HraDevice。
     *
     * @param dto 业务对象
     */
    public HraDevice(HraDeviceDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.deviceType = dto.getDeviceType();
        this.deviceStatus = dto.getDeviceStatus();
        this.deviceDesc = dto.getDeviceDesc();
        this.stationName = dto.getStationName();
        this.stationAddress = dto.getStationAddress();
        this.stationTel = dto.getStationTel();
        this.remark = dto.getRemark();
        this.productType = dto.getProductType();
        this.stationId = dto.getStationId();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraDeviceDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(HraDeviceDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setDeviceType(this.deviceType);
        dto.setDeviceStatus(this.deviceStatus);
        dto.setDeviceDesc(this.deviceDesc);
        dto.setStationName(this.stationName);
        dto.setStationAddress(this.stationAddress);
        dto.setStationTel(this.stationTel);
        dto.setRemark(this.remark);
        dto.setProductType(this.productType);
        dto.setStationId(this.stationId);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
