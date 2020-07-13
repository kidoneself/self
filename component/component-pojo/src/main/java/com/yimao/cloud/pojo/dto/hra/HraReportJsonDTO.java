package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * JSON格式检查报告表
 *
 * @author Zhang Bo
 * @date 2018/4/9.
 */
@Data
public class HraReportJsonDTO implements Serializable {

    private static final long serialVersionUID = -2258420062427150299L;
    /**
     * hra_report表主键
     */
    private Integer reportId;

    /**
     * JSON格式检查报告
     */
    private String reportJson;

}
