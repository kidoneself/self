package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 体检报告
 *
 * @author liuhao@yimaokeji.com
 */
@Getter
@Setter
@ApiModel(description = "体检报告")
public class ReportDTO implements Serializable {
    private static final long serialVersionUID = 2175250531971382116L;

    @ApiModelProperty(position = 1, value = "体检报告ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "体检客户ID")
    private Integer customerId;
    @ApiModelProperty(position = 3, value = "用户姓名")
    private String username;
    @ApiModelProperty(position = 4, value = "性别")
    private String sex;
    @ApiModelProperty(position = 5, value = "体检劵号")
    private String ticketNo;
    @ApiModelProperty(position = 6, value = "手机号")
    private String phone;
    @ApiModelProperty(position = 7, value = "标志：0:模板 1:非模板")
    private Integer flag;
    @ApiModelProperty(position = 8, value = "体检时间")
    private Date examDate;
    @ApiModelProperty(position = 9, value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 10, value = "体检报告PDF")
    private String reportPDF;

}
