package com.yimao.cloud.pojo.query.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class StoreHouseAllQuery implements Serializable {

    private static final long serialVersionUID = 2430339578423470345L;

    private Integer categoryId;
    private Integer type; //物品类型 1-净水设备 2-物资 3-展示机
    private String adaptationModel; //适配型号

}
