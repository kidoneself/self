package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/****
 * 转让操作日志导出
 *
 * @author Liu Long Jie
 * @date 2020-7-13
 */
@Data
public class TransferOperationLogExportDTO implements Serializable {
    private static final long serialVersionUID = -9087730409647883089L;

    private Integer id;
    private String description;
    private String operator;
    private String createTime;
    //原服务站公司编号
    private String origStationCompanyCode;
    //原服务站公司名称
    private String origStationCompanyName;
    //承包服务站公司编号
    private String destStationCompanyCode;
    //承包服务站公司名称
    private String destStationCompanyName;
}
