package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：安装工查询条件 （服务站站务系统）
 *
 * @Author Liu Long Jie
 * @Date 2020-1-14 14:06:00
 */
@ApiModel(description = "安装工查询条件（服务站站务系统）")
@Getter
@Setter
public class StationEngineerQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -3444456748489154014L;

    @ApiModelProperty(value = "安装工账号")
    private String userName;

    @ApiModelProperty(value = "安装工姓名")
    private String realName;

    @ApiModelProperty(value = "联系方式")
    private String phone;

    @ApiModelProperty(value = "是否禁用：0-否，1-是")
    private Boolean forbidden;

}
