package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraReportDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
@Table(name = "hra_report")
@Data
public class HraReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public HraReport() {
    }

    /**
     * 用业务对象HraReportDTO初始化数据库对象HraReport。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraReport(HraReportDTO dto) {
        this.id = dto.getId();
        this.deviceId = dto.getDeviceId();
        this.ticketNo = dto.getTicketNo();
        this.customerId = dto.getCustomerId();
        this.examDate = dto.getExamDate();
        this.updateTime = dto.getUpdateTime();
        this.reportPdf = dto.getReportPdf();
        this.reportDat = dto.getReportDat();
        this.reportSwf = dto.getReportSwf();
        this.reportPics = dto.getReportPics();
        this.reportJpgs = dto.getReportJpgs();
        this.doctor = dto.getDoctor();
        this.pic1 = dto.getPic1();
        this.pic2 = dto.getPic2();
        this.pic3 = dto.getPic3();
        this.pic4 = dto.getPic4();
        this.pic5 = dto.getPic5();
        this.pic6 = dto.getPic6();
        this.pic7 = dto.getPic7();
        this.pic8 = dto.getPic8();
        this.pic9 = dto.getPic9();
        this.pic10 = dto.getPic10();
        this.remark = dto.getRemark();
        this.showFlag = dto.getShowFlag();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraReportDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraReportDTO dto) {
        dto.setId(this.id);
        dto.setDeviceId(this.deviceId);
        dto.setTicketNo(this.ticketNo);
        dto.setCustomerId(this.customerId);
        dto.setExamDate(this.examDate);
        dto.setUpdateTime(this.updateTime);
        dto.setReportPdf(this.reportPdf);
        dto.setReportDat(this.reportDat);
        dto.setReportSwf(this.reportSwf);
        dto.setReportPics(this.reportPics);
        dto.setReportJpgs(this.reportJpgs);
        dto.setDoctor(this.doctor);
        dto.setPic1(this.pic1);
        dto.setPic2(this.pic2);
        dto.setPic3(this.pic3);
        dto.setPic4(this.pic4);
        dto.setPic5(this.pic5);
        dto.setPic6(this.pic6);
        dto.setPic7(this.pic7);
        dto.setPic8(this.pic8);
        dto.setPic9(this.pic9);
        dto.setPic10(this.pic10);
        dto.setRemark(this.remark);
        dto.setShowFlag(this.showFlag);
    }
}
