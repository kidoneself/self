package com.yimao.cloud.user.dto;

import com.yimao.cloud.pojo.dto.user.UserCompanyDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 用户经销商信息
 * @author: yu chunlei
 * @create: 2018-12-26 10:20:42
 **/
@Data
public class UserDistributorDTO implements Serializable {

    private static final long serialVersionUID = 7115872992796920278L;
    @ApiModelProperty(value = "头像")
    private String headImage;//头像
    @ApiModelProperty(value = "账户名")
    private String accountName;//账户名
    @ApiModelProperty(value = "姓名")
    private String realName;//姓名
    @ApiModelProperty(value = "性别")
    private Integer sex;//性别
    @ApiModelProperty(value = "联系方式")
    private String phone;//联系方式
    @ApiModelProperty(value = "地址")
    private String address;//地址
    @ApiModelProperty(value = "身份证号")
    private String idCard;//身份证号
    @ApiModelProperty(value = "邮箱")
    private String email;//邮箱

    @ApiModelProperty(value = "企业信息")
    private UserCompanyDTO companyDTO;

    @ApiModelProperty(value = "是否企业版经销商,0--否 1--是")
    private Boolean isCompanyInfo;//是否企业版经销商

}
