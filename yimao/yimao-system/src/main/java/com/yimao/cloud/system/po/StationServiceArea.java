package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 服务站服务区域
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@Table(name = "station_service_area")
@Getter
@Setter
public class StationServiceArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer stationId;              //服务站门店id
    private String stationName;             //服务站门店名称
    private Integer areaId;                 //区域id
    private String province;                //省
    private String city;                    //市
    private String region;                  //区
    private Integer serviceType;            //服务类型 0-售前+售后；1-售前；2-售后

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;


    public StationServiceArea() {
    }

    /**
     * 用业务对象StationServiceAreaDTO初始化数据库对象StationServiceArea。
     *
     * @param dto 业务对象
     */
    public StationServiceArea(StationServiceAreaDTO dto) {
        this.id = dto.getId();
        this.stationId = dto.getStationId();
        this.stationName = dto.getStationName();
        this.areaId = dto.getAreaId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.serviceType = dto.getServiceType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.serviceType = dto.getServiceType();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationServiceAreaDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(StationServiceAreaDTO dto) {
        dto.setId(this.id);
        dto.setStationId(this.stationId);
        dto.setStationName(this.stationName);
        dto.setAreaId(this.areaId);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setServiceType(this.serviceType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setServiceType(this.serviceType);
    }
}
