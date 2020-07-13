package com.yimao.cloud.station.po;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "station_admin")
@Data
public class StationAdmin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private String password;

    private String realName;

    private Integer sex;

    private String phone;

    private Integer stationCompanyId;
    
    private String stationCompanyName;

    private Integer roleId;

    private Boolean status;

    private Integer creator;

    private Date createTime;

    private Integer updater;

    private Date updateTime;

 
}