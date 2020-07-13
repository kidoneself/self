package com.yimao.cloud.pojo.dto.hra;

import com.yimao.cloud.pojo.dto.system.StationDTO;
import lombok.Data;

import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019/6/1
 */
@Data
public class HraAllotTicketExportDTO {

    private String ticketNo;
    private String stationName;
    private String stationProvince;
    private String stationCity;
    private String stationRegion;
    private String validEndTime;
    private String hasExpire;
    private Integer total;
    private Integer useCount;
    private String selfStation;
    private String createTime;
    private String forbidden;
    private String expired;

    private Integer ticketStatus;
    private Integer stationId;
    private String validBeginTime;
    private HraCardDTO hraCard;
    private StationDTO station;//
    private Integer days;


}
