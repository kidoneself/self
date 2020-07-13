package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author Liu long jie
 * @description 用户列表查询条件
 * @date 2019-12-26
 **/
@Getter
@Setter
public class UserQuery extends BaseQuery {
    @ApiModelProperty(value = "用户e家号")
    private Integer id;
    @ApiModelProperty(value = "用户姓名")
    private String realName;
    @ApiModelProperty(value = "用户手机号")
    private String phone;
    @ApiModelProperty(value = "用户身份")
    private Integer userType;
    @ApiModelProperty(value = "来源端：1-健康e家公众号 2-经销商APP 3-净水设备")
    private Integer originTerminal;
    @ApiModelProperty(value = "用户来源方式：1-代言卡分享 2-宣传卡分享 3-资讯分享 4-视频分享 5-商品分享 6-自主关注公众号 7-经销商APP添加 8-水机屏推广二维码")
    private Integer origin;
    @ApiModelProperty("经销商e家号")
    private Integer distributorId;
    @ApiModelProperty(value = "经销商账号")
    private String distributorAccount;
    @ApiModelProperty(value = "经销商姓名")
    private String distributorName;
    @ApiModelProperty(value = "创建时间（始）")
    private Date startTime;
    @ApiModelProperty(value = "创建时间（终）")
    private Date endTime;

    @ApiModelProperty(value = "经销商id集合")
    private List<Integer> distributorIds;

    // ---------- 站务系统概况跳转到相关列表需传参数 ---------------
    @ApiModelProperty(value = "是否是普通用户")
    private Boolean isCommonUser;
    @ApiModelProperty(value = "是否是普通用户或者会员用户")
    private Boolean isCommonUserOrVipUser;

}
