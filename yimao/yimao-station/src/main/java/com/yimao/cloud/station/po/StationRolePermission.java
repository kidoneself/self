package com.yimao.cloud.station.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;

@Table(name = "station_role__permission")
@Getter
@Setter
public class StationRolePermission {
    private Integer roleId;

    private Integer permissionId;

   
}