package com.yimao.cloud.system.po;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author Lizhqiang
 * @date 2019/1/18
 */
@Table(name = "t_station_scope")
@Data
public class CompanyRegion {

    private Integer id;
    private String province;//省
    private String city;//市
    private String region;
    private Integer corporationStatus;
}
