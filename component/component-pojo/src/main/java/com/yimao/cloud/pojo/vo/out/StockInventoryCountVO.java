package com.yimao.cloud.pojo.vo.out;

import java.util.List;

public class StockInventoryCountVO {
    private String productKindId;
    private String productKindName;
    private List<StockInventoryVO> productList;
    private String materielTypeId;
    private String materielTypeName;
    private int materielCount;
    private List<StockInventoryVO> materielList;

    public StockInventoryCountVO() {
    }

    public String getProductKindId() {
        return this.productKindId;
    }

    public void setProductKindId(String productKindId) {
        this.productKindId = productKindId;
    }

    public String getProductKindName() {
        return this.productKindName;
    }

    public void setProductKindName(String productKindName) {
        this.productKindName = productKindName;
    }

    public List<StockInventoryVO> getProductList() {
        return this.productList;
    }

    public void setProductList(List<StockInventoryVO> productList) {
        this.productList = productList;
    }

    public String getMaterielTypeId() {
        return this.materielTypeId;
    }

    public void setMaterielTypeId(String materielTypeId) {
        this.materielTypeId = materielTypeId;
    }

    public String getMaterielTypeName() {
        return this.materielTypeName;
    }

    public void setMaterielTypeName(String materielTypeName) {
        this.materielTypeName = materielTypeName;
    }

    public int getMaterielCount() {
        return this.materielCount;
    }

    public void setMaterielCount(int materielCount) {
        this.materielCount = materielCount;
    }

    public List<StockInventoryVO> getMaterielList() {
        return this.materielList;
    }

    public void setMaterielList(List<StockInventoryVO> materielList) {
        this.materielList = materielList;
    }
}
