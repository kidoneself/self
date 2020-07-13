package com.yimao.cloud.pojo.vo.out;

import java.util.Date;

public class StockStateQueryVO {
    Integer status;
    String batchCode;
    Date stationPutDate;
    Date productPutDate;
    Date engineerPutDate;
    String snCode;
    String materielCode;
    String materielTypeName;
    String materielName;
    String isFrozen;
    String fitTypeName;
    String stockOwnFlagName;
    String stockOwnId;
    String stockOwnName;
    String id;
    String isOnTheWay;
    Integer stockOwnFlag;
    Integer materielType;

    public StockStateQueryVO() {
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Date getStationPutDate() {
        return this.stationPutDate;
    }

    public void setStationPutDate(Date stationPutDate) {
        this.stationPutDate = stationPutDate;
    }

    public Date getProductPutDate() {
        return this.productPutDate;
    }

    public void setProductPutDate(Date productPutDate) {
        this.productPutDate = productPutDate;
    }

    public Date getEngineerPutDate() {
        return this.engineerPutDate;
    }

    public void setEngineerPutDate(Date engineerPutDate) {
        this.engineerPutDate = engineerPutDate;
    }

    public String getSnCode() {
        return this.snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public String getMaterielCode() {
        return this.materielCode;
    }

    public void setMaterielCode(String materielCode) {
        this.materielCode = materielCode;
    }

    public String getMaterielTypeName() {
        return this.materielTypeName;
    }

    public void setMaterielTypeName(String materielTypeName) {
        this.materielTypeName = materielTypeName;
    }

    public String getMaterielName() {
        return this.materielName;
    }

    public void setMaterielName(String materielName) {
        this.materielName = materielName;
    }

    public String getIsFrozen() {
        return this.isFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getFitTypeName() {
        return this.fitTypeName;
    }

    public void setFitTypeName(String fitTypeName) {
        this.fitTypeName = fitTypeName;
    }

    public String getStockOwnFlagName() {
        return this.stockOwnFlagName;
    }

    public void setStockOwnFlagName(String stockOwnFlagName) {
        this.stockOwnFlagName = stockOwnFlagName;
    }

    public String getStockOwnId() {
        return this.stockOwnId;
    }

    public void setStockOwnId(String stockOwnId) {
        this.stockOwnId = stockOwnId;
    }

    public String getStockOwnName() {
        return this.stockOwnName;
    }

    public void setStockOwnName(String stockOwnName) {
        this.stockOwnName = stockOwnName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsOnTheWay() {
        return this.isOnTheWay;
    }

    public void setIsOnTheWay(String isOnTheWay) {
        this.isOnTheWay = isOnTheWay;
    }

    public Integer getStockOwnFlag() {
        return this.stockOwnFlag;
    }

    public void setStockOwnFlag(Integer stockOwnFlag) {
        this.stockOwnFlag = stockOwnFlag;
    }

    public Integer getMaterielType() {
        return this.materielType;
    }

    public void setMaterielType(Integer materielType) {
        this.materielType = materielType;
    }
}
