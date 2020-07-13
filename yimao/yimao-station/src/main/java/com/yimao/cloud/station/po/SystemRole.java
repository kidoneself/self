package com.yimao.cloud.station.po;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "system_role")
@Getter
@Setter
public class SystemRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer stationId;

    private String areaIds;

    private Boolean status;

    private Integer adminId;

    private Integer creator;

    private Date createTime;

    private Integer updater;

    private Date updateTime;


}