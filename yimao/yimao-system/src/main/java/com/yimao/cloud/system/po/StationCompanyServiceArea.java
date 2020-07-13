package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 公司服务区域
 *
 * @author Lizhqiang
 * @date 2019/1/17
 */
@Table(name = "station_company_service_area")
@Getter
@Setter
public class StationCompanyServiceArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer stationCompanyId;   //区县级公司id
    private String stationCompanyName;  //区县级公司名称
    private Integer areaId;             //地区id
    private String province;            //公司服务省
    private String city;                //公司服务市
    private String region;              //公司服务区
    private Integer serviceType;        //服务类型 0-售前+售后；1-售前；2-售后

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public StationCompanyServiceArea() {
    }

    /**
     * 用业务对象StationCompanyServiceAreaDTO初始化数据库对象StationCompanyServiceArea。
     *
     * @param dto 业务对象
     */
    public StationCompanyServiceArea(StationCompanyServiceAreaDTO dto) {
        this.id = dto.getId();
        this.stationCompanyId = dto.getStationCompanyId();
        this.stationCompanyName = dto.getStationCompanyName();
        this.areaId = dto.getAreaId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.serviceType = dto.getServiceType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationCompanyServiceAreaDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(StationCompanyServiceAreaDTO dto) {
        dto.setId(this.id);
        dto.setStationCompanyId(this.stationCompanyId);
        dto.setStationCompanyName(this.stationCompanyName);
        dto.setAreaId(this.areaId);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setServiceType(this.serviceType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
