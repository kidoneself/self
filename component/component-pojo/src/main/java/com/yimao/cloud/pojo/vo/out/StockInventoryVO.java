package com.yimao.cloud.pojo.vo.out;

public class StockInventoryVO {
    private String code;
    private String id;
    private String name;
    private int usableNum;
    private String unitName;
    private String fitTypeName;

    public StockInventoryVO() {
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsableNum() {
        return this.usableNum;
    }

    public void setUsableNum(int usableNum) {
        this.usableNum = usableNum;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getFitTypeName() {
        return this.fitTypeName;
    }

    public void setFitTypeName(String fitTypeName) {
        this.fitTypeName = fitTypeName;
    }
}
