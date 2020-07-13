package com.yimao.cloud.pojo.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 收益主体
 *
 * @author Liu Yi
 * @date 2019-1-17
 */
@Data
@ApiModel(description = "收益主体信息")
public class IncomeSubjectDTO  implements Serializable {
    private static final long serialVersionUID = 1868537931627634435L;

    private Integer id;
    @ApiModelProperty(value = "收益主体名称")
    private String incomeSubjectName;
    @ApiModelProperty(value = "收益主体类型code")
    private String incomeSubjectCode;
    @ApiModelProperty(value = "结算主体类型code")
    private String settlementSubjectCode;
    @ApiModelProperty(value = "结算主体名称")
    private String settlementSubjectName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "创建时间",example = "2018-12-28 11:00:00")
    private Date createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "更新时间",example = "2018-12-28 11:00:00")
    private Date updateTime;
    @ApiModelProperty(value = "更新人")
    private String updater;
}