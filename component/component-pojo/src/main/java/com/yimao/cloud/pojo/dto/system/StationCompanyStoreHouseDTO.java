package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.util.List;

@Data
public class StationCompanyStoreHouseDTO {

    private Integer id;

    private Integer stationCompanyId;

    private Integer goodsId;

    private Integer stockCount;


    private String stationCompanyName;
    private String province;
    private String city;
    private String region;
    private String stationCount;
    private String firstCategoryName;
    private String twoCategoryName;
    private String goodsName;
    private String adaptationModel;

    private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合

}