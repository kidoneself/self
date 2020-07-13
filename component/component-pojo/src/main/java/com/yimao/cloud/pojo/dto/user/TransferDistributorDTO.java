package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zhang Bo
 * @date 2019/4/4
 */
@ApiModel(description = "转让经销商DTO")
@Getter
@Setter
@ToString
public class TransferDistributorDTO implements Serializable {

    @ApiModelProperty(position = 1, value = "是否转让省级代理：0-否；1-是；")
    private Boolean transferProvinceAgent;
    @ApiModelProperty(position = 2, value = "是否转让市级代理：0-否；1-是；")
    private Boolean transferCityAgent;
    @ApiModelProperty(position = 3, value = "是否转让区县级代理：0-否；1-是；")
    private Boolean transferRegionAgent;
    @ApiModelProperty(position = 4, value = "是否转让经销商：0-否；1-是；")
    private Boolean transferDistributor;

    @ApiModelProperty(position = 11, value = "经销商姓名")
    private String realName;
    @ApiModelProperty(position = 12, value = "性别：1-男；2-女；")
    private Integer sex;
    @ApiModelProperty(position = 13, value = "经销商手机号")
    private String phone;
    @ApiModelProperty(position = 14, value = "经销商身份证")
    private String idCard;

    @ApiModelProperty(position = 21, value = "经销商账号创建方式：1-系统自动生成；2-自定义创建；")
    private Integer createAccountType;
    @ApiModelProperty(position = 22, value = "经销商账号")
    private String userName;

    @ApiModelProperty(position = 31, value = "附件")
    private String attachment;
    @ApiModelProperty(position = 32, value = "备注")
    private String remark;

}
