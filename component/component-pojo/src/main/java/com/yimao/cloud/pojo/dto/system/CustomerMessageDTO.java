package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liuhao@yimaokeji.com
 * @date 2018/5/28
 */
@Data
@ApiModel(description = "留言消息")
public class CustomerMessageDTO implements Serializable {
    private static final long serialVersionUID = -8091399636536265113L;

    @ApiModelProperty(position = 1, value = "留言消息ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "客户姓名")
    private String customerName;
    @ApiModelProperty(position = 3, value = "手机号")
    private String mobile;
    @ApiModelProperty(position = 4, value = "省")
    private String province;
    @ApiModelProperty(position = 5, value = "市")
    private String city;
    @ApiModelProperty(position = 6, value = "区")
    private String region;
    @ApiModelProperty(position = 7, value = "加盟类型 1-区域代理 2-企业合作 3-个人版 4-微创版 5-分享经济")
    private Integer joinType;
    @ApiModelProperty(position = 9, value = "留言内容")
    private String content;
    @ApiModelProperty(position = 10, value = "备注")
    private String remark;
    @ApiModelProperty(position = 11, value = "openid")
    private String openId;
    @ApiModelProperty(position = 12, value = "留言时间")
    private Date createTime;
    @ApiModelProperty(position = 8, value = "咨询来源端 1-翼猫科技物联网平台 2-经销商APP 3-健康e家公众号")
    private Integer terminal;

}
