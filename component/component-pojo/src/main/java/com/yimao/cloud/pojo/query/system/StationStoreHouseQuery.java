package com.yimao.cloud.pojo.query.system;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class StationStoreHouseQuery implements Serializable {

    private static final long serialVersionUID = 2430339578489702345L;

    private Set<Integer> stationIds;
    private String province;
    private String city;
    private String region;
    private String stationName;
    private String stationCompanyName;
    private Integer categoryId;
    private Integer type; //物品类型 1-净水设备 2-物资 3-展示机
    private String adaptationModel; //适配型号
    
    private Boolean isDefective;//是否不良品： true-是不良品 false-不是不良品 用于展示门店不良品大于0的库存物资

}
