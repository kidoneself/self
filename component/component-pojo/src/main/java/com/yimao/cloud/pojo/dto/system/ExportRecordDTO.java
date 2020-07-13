package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：导出记录
 *
 * @Author Zhang Bo
 * @Date 2019/11/25
 */
@ApiModel(description = "导出记录")
@Getter
@Setter
public class ExportRecordDTO implements Serializable {

    private static final long serialVersionUID = -1738282310539652266L;

    @ApiModelProperty(position = 1, value = "ID")
    private Integer id;

    @ApiModelProperty(position = 2, value = "管理员ID")
    private Integer adminId;

    @ApiModelProperty(position = 3, value = "管理员姓名")
    private String adminName;

    @ApiModelProperty(position = 4, value = "状态：1-等待导出；2-导出中；3-导出成功；4-导出失败；")
    private Integer status;

    @ApiModelProperty(position = 5, value = "状态：1-等待导出；2-导出中；3-导出成功；4-导出失败；")
    private String statusName;

    @ApiModelProperty(position = 6, value = "导出操作的请求URL")
    private String url;

    @ApiModelProperty(position = 7, value = "导出数据的标题")
    private String title;

    @ApiModelProperty(position = 8, value = "导出文件的下载地址")
    private String downloadLink;

    @ApiModelProperty(position = 9, value = "导出耗时（单位：秒）")
    private Integer duration;

    @ApiModelProperty(position = 10, value = "下载进度（除以100之后为百分比）")
    private Double progress;

    @ApiModelProperty(position = 11, value = "导出失败原因")
    private String reason;

    @ApiModelProperty(position = 100, value = "创建时间")
    private Date createTime;

    @ApiModelProperty(position = 101, value = "更新时间")
    private Date updateTime;

}
