package com.yimao.cloud.system.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：区县级公司和服务站门店关联
 *
 * @Author Zhang Bo
 * @Date 2019/3/25
 */
@Table(name = "station_company__station")
@Getter
@Setter
public class StationCompanyStation {

    @Id
    private Integer stationCompanyId;
    @Id
    private Integer stationId;

}
