package com.yimao.cloud.pojo.vo.out;

import java.util.List;

public class MyInventoryCountVO {
    private String productKindName;
    private String productKindId;
    private List<MyInventoryVO> productList;
    private String materielTypeName;
    private String materielTypeId;
    private int materielCount;
    private List<MyInventoryVO> materielList;

    public MyInventoryCountVO() {
    }

    public String getProductKindName() {
        return this.productKindName;
    }

    public void setProductKindName(String productKindName) {
        this.productKindName = productKindName;
    }

    public String getProductKindId() {
        return this.productKindId;
    }

    public void setProductKindId(String productKindId) {
        this.productKindId = productKindId;
    }

    public List<MyInventoryVO> getProductList() {
        return this.productList;
    }

    public void setProductList(List<MyInventoryVO> productList) {
        this.productList = productList;
    }

    public String getMaterielTypeName() {
        return this.materielTypeName;
    }

    public void setMaterielTypeName(String materielTypeName) {
        this.materielTypeName = materielTypeName;
    }

    public String getMaterielTypeId() {
        return this.materielTypeId;
    }

    public void setMaterielTypeId(String materielTypeId) {
        this.materielTypeId = materielTypeId;
    }

    public int getMaterielCount() {
        return this.materielCount;
    }

    public void setMaterielCount(int materielCount) {
        this.materielCount = materielCount;
    }

    public List<MyInventoryVO> getMaterielList() {
        return this.materielList;
    }

    public void setMaterielList(List<MyInventoryVO> materielList) {
        this.materielList = materielList;
    }
}
