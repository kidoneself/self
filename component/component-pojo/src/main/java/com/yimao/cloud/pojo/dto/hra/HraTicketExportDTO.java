package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/5/15
 */
@Data
public class HraTicketExportDTO {

    private String stationProvince;
    private String stationCity;
    private String stationRegion;
    private String ticketNo;
    private Integer userId;
    private String customerUserName;
    private String customerIdCard;
    private String customerPhone;
    private String customerSex;
    private String customerHeight;
    private String customerWeight;
    private String customerBirthDate;
    private Date examDate;
    private Integer ticketStatus;
    private Integer reserveFrom;
    private Integer stationId;


}
