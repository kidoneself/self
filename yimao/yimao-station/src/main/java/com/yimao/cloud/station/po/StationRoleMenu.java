package com.yimao.cloud.station.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Table(name = "station_role__menu")
@Getter
@Setter
public class StationRoleMenu {
    private Integer roleId;

    private Integer menuId;

    
}