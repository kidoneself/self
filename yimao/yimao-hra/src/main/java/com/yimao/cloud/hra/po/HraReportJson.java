package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraReportJsonDTO;
import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JSON格式检查报告表
 *
 * @author Zhang Bo
 * @date 2018/4/9.
 */
@Table(name = "hra_report_json")
@Data
public class HraReportJson {

    /**
     * hra_report表主键
     */
    @Id
    private Integer reportId;

    /**
     * JSON格式检查报告
     */
    private String reportJson;

    public HraReportJson() {
    }

    /**
     * 用业务对象HraReportJsonDTO初始化数据库对象HraReportJson。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraReportJson(HraReportJsonDTO dto) {
        this.reportId = dto.getReportId();
        this.reportJson = dto.getReportJson();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraReportJsonDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraReportJsonDTO dto) {
        dto.setReportId(this.reportId);
        dto.setReportJson(this.reportJson);
    }
}
