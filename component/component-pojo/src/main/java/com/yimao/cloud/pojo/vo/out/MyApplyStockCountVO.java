package com.yimao.cloud.pojo.vo.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class MyApplyStockCountVO {
    private String allotFromId;
    private String allotFromName;
    private String allotToId;
    private String allotToName;
    private String applyStatus;
    private String applyStatusName;
    private String applyTime;
    private String id;
    private String materielType;
    private List<MyApplyStockVO> stockList;
    private Integer remainingTotal;//待处理总数
}
