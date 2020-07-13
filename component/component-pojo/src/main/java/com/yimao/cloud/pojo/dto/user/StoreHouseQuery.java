package com.yimao.cloud.pojo.dto.user;

import lombok.Data;

@Data
public class StoreHouseQuery {
    private String province;
    private String city;
    private String region;

    @Override
    public String toString() {
        return "StoreHouseQuery{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
