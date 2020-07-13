package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Liu long jie
 * @description 经销商/代理商列表查询条件
 * @date 2019-12-26
 **/
@Getter
@Setter
public class DistributorQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = 8216140968986787917L;

    @ApiModelProperty(value = "经销商类型")
    private Integer type;
    @ApiModelProperty(value = "经销商姓名")
    private String name;
    @ApiModelProperty(value = "经销商账号")
    private String account;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "是否为代理商")
    private Boolean isAgent;
    @ApiModelProperty(value = "是否为站长")
    private Integer stationMaster;
    @ApiModelProperty(value = "是否为发起人")
    private Integer sponsor;
    @ApiModelProperty(value = "推荐人id")
    private Integer recommendId;
    @ApiModelProperty(value = "来源端：1-翼猫业务系统；2-经销商app；")
    private Integer terminal;
    @ApiModelProperty(value = "来源方式：1-翼猫企业二维码;2-翼猫后台上线; 3:经销商app创建账号; 4-资讯分享 ; 5-视频分享; 6-权益卡分享 ; 7-发展经销商二维码")
    private Integer sourceType;
    @ApiModelProperty("经销商主账号")
    private String mainAccount;
    @ApiModelProperty("主账号经销商姓名")
    private String mainName;

    @ApiModelProperty(value = "是否是子账号")
    private Boolean subAccount;

    @ApiModelProperty(value = "标识",hidden = true)
    private Boolean flag;

    @ApiModelProperty(value = "标识",hidden = true)
    private List<Integer> types;

    // ---------- 站务系统概况跳转到相关列表需传参数 ---------------
    @ApiModelProperty(value = "是否是经销商")
    private Boolean isDistributor;

}
