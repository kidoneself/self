package com.yimao.cloud.hra.po;

import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * created by liuhao@yimaokeji.com
 * 2018012018/1/20
 */
@Table(name = "hra_report_record")
@Data
public class HraReportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer reportId;      //评估报告
    private Integer userId;        //用户
    private Boolean deleteStatus;//是否删除  0:未删除   1:删除

}
