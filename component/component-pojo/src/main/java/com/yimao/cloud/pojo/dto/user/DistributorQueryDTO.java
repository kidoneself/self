package com.yimao.cloud.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description 经销商查询
 * @author hhf
 * @date 2018/12/17
 */
@Data
@ApiModel(description = "经销商查询实体")
public class DistributorQueryDTO implements Serializable {

    private static final long serialVersionUID = 6667650171902312098L;

    @ApiModelProperty("经销商ID")
    private Integer id;

    @ApiModelProperty("经销商账号")
    private String account;

    @ApiModelProperty("经销商姓名")
    private String name;

    @ApiModelProperty("联系方式")
    private String phone;

    @ApiModelProperty("上线端 1-翼猫业务系统；2-经销商app")
    private Integer terminal;

    @ApiModelProperty("是否为代理商 0-否 1-是")
    private Boolean isAgent;

    @ApiModelProperty("是否为经销商 0-否 1-是")
    private Boolean isDistributor;

    @ApiModelProperty("经销商类型")
    private Integer type;

    @ApiModelProperty("经销商所在省")
    private String province;

    @ApiModelProperty("经销商所在市")
    private String city;

    @ApiModelProperty("经销商所在区")
    private String region;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("是否为站长 0-否 1-是")
    private Integer stationMaster;

    @ApiModelProperty("是否为发起人 0-否 1-是")
    private Integer originator;

    @ApiModelProperty("推荐人")
    private String referrer;

    @ApiModelProperty("经销商主账号")
    private String mainAccount;

    @ApiModelProperty("主账号经销商姓名")
    private String mainName;

    /*@ApiModelProperty("经销商角色配置ID")
    private Integer distributorConfigId;*/

    @ApiModelProperty("来源方式 1-翼猫企业二维码; 2-翼猫后台上线; 3:经销商app创建账号 4:资讯分享; 5-视频分享；6-权益卡分享; 7:发展经销商二维码")
    private Integer sourceType;

    @ApiModelProperty("是否为子账号 0-否 1-是 (默认为0)")
    private Boolean subAccount;

    @ApiModelProperty(value = "标识",hidden = true)
    private Boolean flag;

    @ApiModelProperty(value = "标识",hidden = true)
    private List<Integer> types;
}