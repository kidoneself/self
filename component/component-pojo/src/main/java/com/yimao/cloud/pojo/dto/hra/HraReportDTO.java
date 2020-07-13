package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
@Data
public class HraReportDTO implements Serializable {

    private static final long serialVersionUID = 3440120745659494665L;
    private Integer id;
    private String deviceId;
    private String ticketNo;
    private Integer customerId;
    private Date examDate;
    private Date updateTime;
    private String reportPdf;
    private String reportDat;
    private String reportSwf;
    private String reportPics;
    private String reportJpgs;
    private String doctor;
    private String pic1;
    private String pic2;
    private String pic3;
    private String pic4;
    private String pic5;
    private String pic6;
    private String pic7;
    private String pic8;
    private String pic9;
    private String pic10;
    private String remark;
    private Integer showFlag; //评估报告是否可见  1 可见 0 隐藏

}
