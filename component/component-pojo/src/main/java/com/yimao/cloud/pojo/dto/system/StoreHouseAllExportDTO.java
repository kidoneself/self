package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;
import java.util.List;

public class StoreHouseAllExportDTO implements Serializable {

    private static final long serialVersionUID = -1722572359910421779L;


    private Integer id;

    private String firstCategoryName;
    private String twoCategoryName;
    private String goodsName;
    private String adaptationModel;
    private Integer stockCount;

    private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstCategoryName() {
        return firstCategoryName;
    }

    public void setFirstCategoryName(String firstCategoryName) {
        this.firstCategoryName = firstCategoryName;
    }

    public String getTwoCategoryName() {
        return twoCategoryName;
    }

    public void setTwoCategoryName(String twoCategoryName) {
        this.twoCategoryName = twoCategoryName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getAdaptationModel() {
        if (materialsAdaptationList != null && materialsAdaptationList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (MaterialsAdaptationModelDTO materialsAdaptationModelDTO : materialsAdaptationList) {
                sb.append(materialsAdaptationModelDTO.getDeviceModelName() + "、");
            }
            return sb.substring(0, sb.length() - 1);
        }
        return adaptationModel;
    }

    public void setAdaptationModel(String adaptationModel) {
        this.adaptationModel = adaptationModel;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public List<MaterialsAdaptationModelDTO> getMaterialsAdaptationList() {
        return materialsAdaptationList;
    }

    public void setMaterialsAdaptationList(List<MaterialsAdaptationModelDTO> materialsAdaptationList) {
        this.materialsAdaptationList = materialsAdaptationList;
    }
}