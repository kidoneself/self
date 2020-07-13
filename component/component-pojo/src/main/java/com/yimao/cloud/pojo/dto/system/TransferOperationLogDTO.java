package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/****
 * 转让操作日志(服务站转让、安装工转让)
 * @author zhangbaobao
 *
 */
@Data
public class TransferOperationLogDTO implements Serializable {
    private static final long serialVersionUID = -9087730402000583089L;

    private Integer id;
    @ApiModelProperty(value = "转让方")
    private Integer transferorId;
    @ApiModelProperty(value = "接收方")
    private Integer receiverId;
    @ApiModelProperty(value = "操作类型:1.售后承包转让，2.安装工转让")
    private Integer operateType;
    @ApiModelProperty(value = "描述信息:用来存储具体转让的详细信息")
    private String description;
    @ApiModelProperty(value = "操作人")
    private String operator;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //原服务站公司编号
    private String origStationCompanyCode;
    //原服务站公司名称
    private String origStationCompanyName;
    //承包服务站公司编号
    private String destStationCompanyCode;
    //承包服务站公司名称
    private String destStationCompanyName;
}
