package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.ExportRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：导出记录
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
@Table(name = "export_record")
@Getter
@Setter
public class ExportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer adminId;        //管理员ID
    private String adminName;       //管理员姓名
    private Integer status;         //状态：1-等待导出；2-导出中；3-导出成功；4-导出失败；
    private String statusName;      //状态：1-等待导出；2-导出中；3-导出成功；4-导出失败；
    private String url;             //导出操作的请求URL
    private String title;           //导出数据的标题
    private String downloadLink;    //导出文件的下载地址
    private Integer duration;       //导出耗时（单位：秒）
    private Double progress;        //下载进度（除以100之后为百分比）
    private String reason;          //导出失败原因
    private Date createTime;
    private Date updateTime;

    public ExportRecord() {
    }

    /**
     * 用业务对象ExportRecordDTO初始化数据库对象ExportRecord。
     *
     * @param dto 业务对象
     */
    public ExportRecord(ExportRecordDTO dto) {
        this.id = dto.getId();
        this.adminId = dto.getAdminId();
        this.adminName = dto.getAdminName();
        this.status = dto.getStatus();
        this.statusName = dto.getStatusName();
        this.url = dto.getUrl();
        this.title = dto.getTitle();
        this.downloadLink = dto.getDownloadLink();
        this.duration = dto.getDuration();
        this.progress = dto.getProgress();
        this.reason = dto.getReason();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ExportRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(ExportRecordDTO dto) {
        dto.setId(this.id);
        dto.setAdminId(this.adminId);
        dto.setAdminName(this.adminName);
        dto.setStatus(this.status);
        dto.setStatusName(this.statusName);
        dto.setUrl(this.url);
        dto.setTitle(this.title);
        dto.setDownloadLink(this.downloadLink);
        dto.setDuration(this.duration);
        dto.setProgress(this.progress);
        dto.setReason(this.reason);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
