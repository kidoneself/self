package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：设备TDS值上传记录
 *
 * @Author Zhang Bo
 * @Date 2019/5/10
 */
@ApiModel(description = "设备TDS值上传记录DTO")
@Getter
@Setter
public class TdsUploadRecordDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(position = 1, value = "水机设备SN码")
    private String sn;
    @ApiModelProperty(position = 2, value = "水机设备ID")
    private Integer deviceId;
    @ApiModelProperty(position = 3, value = "安装工ID")
    private Integer engineerId;
    @ApiModelProperty(position = 4, value = "安装工姓名")
    private String engineerName;
    @ApiModelProperty(position = 5, value = "原K")
    private Double k;
    @ApiModelProperty(position = 6, value = "原T")
    private Double t;
    @ApiModelProperty(position = 7, value = "新K")
    private Double currentK;
    @ApiModelProperty(position = 8, value = "新T")
    private Double currentT;
    @ApiModelProperty(position = 9, value = "创建时间")
    private Date createTime;
    @ApiModelProperty(position = 10, value = "操作类型：1-恢复TDS；2-修改TDS")
    private Integer type;
    @ApiModelProperty(position = 11, value = "操作类型名称")
    private String typeName;
    @ApiModelProperty(position = 12, value = "审核状态：Y-已审核；N-未审核；")
    private String verifyStatus;
    @ApiModelProperty(position = 13, value = "审核结果：Y-审核通过；N-审核不通过；")
    private String verifyResult;
    @ApiModelProperty(position = 14, value = "审核原因")
    private String verifyReason;
    @ApiModelProperty(position = 15, value = "审核操作人")
    private String verifyUser;
    @ApiModelProperty(position = 16, value = "审核时间")
    private Date verifyTime;

}
