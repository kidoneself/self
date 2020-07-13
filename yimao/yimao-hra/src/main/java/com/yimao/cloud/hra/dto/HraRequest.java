package com.yimao.cloud.hra.dto;


import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;

/**
 * @author Zhang Bo
 * @date 2017/12/15.
 */
public class HraRequest {

    private String deviceId;
    private String ticketNo;
    private StationInfo stationInfo;
    private HraCustomerDTO userInfo;
    private String detail;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
    }

    public HraCustomerDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(HraCustomerDTO userInfo) {
        this.userInfo = userInfo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
