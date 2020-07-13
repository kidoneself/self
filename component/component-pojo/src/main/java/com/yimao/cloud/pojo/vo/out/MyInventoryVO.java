package com.yimao.cloud.pojo.vo.out;

import java.io.Serializable;

public class MyInventoryVO implements Serializable {
    private static final long serialVersionUID = -4142469253308700141L;
    private String code;
    private String id;
    private String name;
    private int usableNum;
    private String unitName;
    private int status;
    private String isFrozen;
    private String materielIdentify;
    private String materielTypeName;
    private String materielTypeId;
    private String fitTypeName;
    private int materielNumber;

    public MyInventoryVO() {
    }

    public int getMaterielNumber() {
        return this.materielNumber;
    }

    public void setMaterielNumber(int materielNumber) {
        this.materielNumber = materielNumber;
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

    public String getIsFrozen() {
        return this.isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getMaterielIdentify() {
        return this.materielIdentify;
    }

    public void setMaterielIdentify(String materielIdentify) {
        this.materielIdentify = materielIdentify;
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

    public String getFitTypeName() {
        return this.fitTypeName;
    }

    public void setFitTypeName(String fitTypeName) {
        this.fitTypeName = fitTypeName;
    }
}
