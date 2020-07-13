package com.yimao.cloud.pojo.dto.system;

import java.io.Serializable;
import java.util.List;

public class StationCompanyStoreHouseExportDTO implements Serializable {

    private static final long serialVersionUID = -1722572359945131779L;
    private Integer id;

    private String stationCompanyName;
    private String address;
    private String stationCount;
    private String firstCategoryName;
    private String twoCategoryName;
    private String goodsName;
    private String adaptationModel;
    private Integer stockCount;

    private List<MaterialsAdaptationModelDTO> materialsAdaptationList;//库存耗材列表展示型号集合

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStationCompanyName() {
        return stationCompanyName;
    }

    public void setStationCompanyName(String stationCompanyName) {
        this.stationCompanyName = stationCompanyName;
    }

    public String getStationCount() {
        return stationCount;
    }

    public void setStationCount(String stationCount) {
        this.stationCount = stationCount;
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