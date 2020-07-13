package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.FilterSettingDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：滤芯参数配置
 *
 * @Author Zhang Bo
 * @Date 2019/7/16
 */
@Table(name = "filter_setting")
@Getter
@Setter
public class FilterSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String province;
    private String city;
    private String region;
    //设备型号：1601T、1602T、1603T、1601L
    private String deviceModel;
    private Double k;
    private Double t;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public FilterSetting() {
    }

    /**
     * 用业务对象FilterSettingDTO初始化数据库对象FilterSetting。
     *
     * @param dto 业务对象
     */
    public FilterSetting(FilterSettingDTO dto) {
        this.id = dto.getId();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.deviceModel = dto.getDeviceModel();
        this.k = dto.getK();
        this.t = dto.getT();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象FilterSettingDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(FilterSettingDTO dto) {
        dto.setId(this.id);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setDeviceModel(this.deviceModel);
        dto.setK(this.k);
        dto.setT(this.t);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
