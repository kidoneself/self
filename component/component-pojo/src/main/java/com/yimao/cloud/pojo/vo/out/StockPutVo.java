package com.yimao.cloud.pojo.vo.out;

import java.io.Serializable;

public class StockPutVo implements Serializable {
    private static final long serialVersionUID = 7635285643954664252L;
    private String batchCode;

    public StockPutVo() {
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
