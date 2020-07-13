package com.yimao.cloud.pojo.vo.out;

import java.util.List;

public class StockOtherEngineerInventoryVO {
    private String engineerId;
    private String engineerName;
    private String engineerPhone;
    private List<MyInventoryCountVO> engineerStock;

    public StockOtherEngineerInventoryVO() {
    }

    public String getEngineerId() {
        return this.engineerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }

    public String getEngineerName() {
        return this.engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getEngineerPhone() {
        return this.engineerPhone;
    }

    public void setEngineerPhone(String engineerPhone) {
        this.engineerPhone = engineerPhone;
    }

    public List<MyInventoryCountVO> getEngineerStock() {
        return this.engineerStock;
    }

    public void setEngineerStock(List<MyInventoryCountVO> engineerStock) {
        this.engineerStock = engineerStock;
    }
}
