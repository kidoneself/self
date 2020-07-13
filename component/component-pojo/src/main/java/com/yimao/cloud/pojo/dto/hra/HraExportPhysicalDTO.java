package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/5/16
 */
@Data
public class HraExportPhysicalDTO {

    private Integer id;
    private String ticketNo;
    private Integer createUserId;
    private Integer currentUserId;
    private Integer userType;
    private String cardType;
    private String ticketStatus;
    private Date validEndTime;
    private Date createTime;
    private Integer orderFrom;
    private Long orderId;
    private Integer useCount;
    private Integer stationId;
    private String stationName;
    private String idCard;
    private Integer validDays;
    private Boolean selfStation;
    private Date validBeginTime;
    private String reserveFrom;
    private String stationProvince;
    private String stationCity;
    private String stationRegion;
    private String endTime;
    private String setTime;
    private String channel;
    private String userMold;
    private String pay;
    private String payTime;

}
