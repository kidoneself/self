package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.ServiceStationWaterDeviceDTO;
import com.yimao.cloud.pojo.vo.water.WaterDeviceVO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Chen Hui Yang
 * @date 2019/4/22
 */
@Table(name = "water_device_service_station")
@Data
public class ServiceStationWaterDevice {

    @Id
    private Integer id;
    private String snCode;//sn
    private String province;//省
    private String city;//市
    private String region;//区
    private String address;//具体地址
    private String place;//设备摆放地
    private String deviceModel;//水机型号
    private String simcard;//sim卡号
    private String deviceType;//设备类型
    private String latitude;//纬度
    private String longitude;//经度
    private Boolean online;//是否在线,0-不在线；1-在线
    private Date activeTime;//水机激活时间
    private Date lastOnlineTime;//最后在线时间
    private Integer connectionType;//网络连接类型：1-WIFI；3-3G，null-全部


    public ServiceStationWaterDevice() {
    }

    /**
     * 用业务对象ServiceStationWaterDeviceDTO初始化数据库对象ServiceStationWaterDevice。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public ServiceStationWaterDevice(ServiceStationWaterDeviceDTO dto) {
        this.id = dto.getId();
        this.snCode = dto.getSnCode();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.place = dto.getPlace();
        this.deviceModel = dto.getDeviceModel();
        this.simcard = dto.getSimcard();
        this.deviceType = dto.getDeviceType();
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.online = dto.getOnline();
        this.activeTime = dto.getActiveTime();
        this.lastOnlineTime = dto.getLastOnlineTime();
        this.connectionType = dto.getConnectionType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ServiceStationWaterDeviceDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ServiceStationWaterDeviceDTO dto) {
        dto.setId(this.id);
        dto.setSnCode(this.snCode);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setPlace(this.place);
        dto.setDeviceModel(this.deviceModel);
        dto.setSimcard(this.simcard);
        dto.setDeviceType(this.deviceType);
        dto.setLatitude(this.latitude);
        dto.setLongitude(this.longitude);
        dto.setOnline(this.online);
        dto.setActiveTime(this.activeTime);
        dto.setLastOnlineTime(this.lastOnlineTime);
        dto.setConnectionType(this.connectionType);
    }


    /**
     * 封装数据 DeviceDTO
     * @param dto
     */
    public void convert(WaterDevice device) {
        device.setSn(this.snCode);
        device.setIccid(this.simcard);
        device.setLongitude(this.longitude);
        device.setLatitude(this.latitude);
        device.setProvince(this.province);
        device.setCity(this.city);
        device.setRegion(this.region);
        device.setAddress(this.address);
        device.setSimActivatingTime(this.activeTime);
        device.setOnline(this.online);
        device.setDeviceType(this.deviceType);
        device.setDeviceModel(this.deviceModel);
        device.setLastOnlineTime(this.lastOnlineTime);
        device.setConnectionType(this.connectionType);
        device.setPlace(this.place);
    }

    /**
     * 封装数据 DeviceDTO
     * @param dto
     */
    public void convert(WaterDeviceVO vo) {
        vo.setSn(this.snCode);
        vo.setIccid(this.simcard);
        vo.setProvince(this.province);
        vo.setCity(this.city);
        vo.setRegion(this.region);
        vo.setAddress(this.address);
        vo.setSimActivatingTime(this.activeTime);
        vo.setDeviceType(this.deviceType);
        vo.setDeviceModel(this.deviceModel);
        vo.setLastOnlineTime(this.lastOnlineTime);
        vo.setPlace(this.place);
    }
}
