package com.yimao.cloud.pojo.vo.out;

import java.io.Serializable;

public class ServiceApplyMaterielVO implements Serializable {
    private static final long serialVersionUID = -3355808109100467245L;
    private String id;
    private String code;
    private String name;
    private String unitName;
    private Integer usableNum;
    private Integer remainingNum;
    private String applyStatus;
    private String applyStatusName;
    private String allotFromId;
    private String allotFromName;
    private String allotToId;
    private String allotToName;
    private String applyTime;
    private String materielType;

    public ServiceApplyMaterielVO() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getUsableNum() {
        return this.usableNum;
    }

    public void setUsableNum(Integer usableNum) {
        this.usableNum = usableNum;
    }

    public String getApplyStatus() {
        return this.applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatusName() {
        return this.applyStatusName;
    }

    public void setApplyStatusName(String applyStatusName) {
        this.applyStatusName = applyStatusName;
    }

    public String getAllotFromId() {
        return this.allotFromId;
    }

    public void setAllotFromId(String allotFromId) {
        this.allotFromId = allotFromId;
    }

    public String getAllotFromName() {
        return this.allotFromName;
    }

    public void setAllotFromName(String allotFromName) {
        this.allotFromName = allotFromName;
    }

    public String getAllotToId() {
        return this.allotToId;
    }

    public void setAllotToId(String allotToId) {
        this.allotToId = allotToId;
    }

    public String getAllotToName() {
        return this.allotToName;
    }

    public void setAllotToName(String allotToName) {
        this.allotToName = allotToName;
    }

    public String getApplyTime() {
        return this.applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getMaterielType() {
        return this.materielType;
    }

    public void setMaterielType(String materielType) {
        this.materielType = materielType;
    }

    public Integer getRemainingNum() {
        return this.remainingNum;
    }

    public void setRemainingNum(Integer remainingNum) {
        this.remainingNum = remainingNum;
    }
}
