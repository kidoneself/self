package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.util.List;

@Data
public class StoreHouseAllDTO {

    private Integer id;

    private Integer goodsId;

    private Integer stockCount;

    private String firstCategoryName;
    private String twoCategoryName;
    private String goodsName;
    private String adaptationModel;

    private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合
}