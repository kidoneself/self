package com.yimao.cloud.pojo.query.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class StationCompanyStoreHouseQuery implements Serializable {

    private static final long serialVersionUID = 3433259578489702345L;

    private Integer stationCompanyId;
    private String province;
    private String city;
    private String region;
    private String stationCompanyName;
    private Integer categoryId;
    private Integer type; //物品类型 1-净水设备 2-物资 3-展示机
    private String adaptationModel; //适配型号

}
