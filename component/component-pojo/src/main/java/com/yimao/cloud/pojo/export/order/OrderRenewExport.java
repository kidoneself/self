package com.yimao.cloud.pojo.export.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 描述：订单管理-续费列表-导出
 *
 * @Author Zhang Bo
 * @Date 2019/8/23
 */
@Getter
@Setter
public class OrderRenewExport {

    private String id;
    private String workOrderId;
    private String snCode;
    private String deviceModel;
    private String createTime;
    private String payTypeName;
    private String payTime;
    private String tradeNo;
    private String lastCostName;
    private String costName;
    private String costTypeName;
    private BigDecimal amountFee;
    private Integer times;
    private String terminalName;
    private String province;
    private String city;
    private String region;
    private String waterUserName;
    private String waterUserPhone;
    private String distributorName;
    private String distributorPhone;
    private String distributorAccount;
    private String distributorIdCard;
    private String distributorArea;
    private String distributorStationName;
    private String distributorRecommendName;
    private String distributorRecommendArea;
    private String distributorRecommendStationName;
    private String engineerName;
    private String engineerPhone;
    private String engineerStationName;

}
