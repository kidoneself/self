package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 评估卡
 *
 * @author Zhang Bo
 * @date 2017/11/29.
 */
@ApiModel(description = "HRA CARD")
@Getter
@Setter
public class HraCardDTO implements Serializable {

    private static final long serialVersionUID = -1363696638058860963L;
    @ApiModelProperty(value = "CARD ID")
    private String id;
    @ApiModelProperty(value = "评估卡类型：Y/F/M")
    private String cardType;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "子订单号")
    private Long orderId;
    @ApiModelProperty(value = "主订单号（支付订单号）")
    private Long mainOrderId;
    @ApiModelProperty(value = "产品ID")
    private Integer productId;
    @ApiModelProperty(value = "有效期")
    private Date validTime;
    @ApiModelProperty(value = "订单来源：1-终端app；2-微信公众号；3-经销商APP；4-小程序；10-管理后台")
    private Integer orderFrom;
    @ApiModelProperty(value = "订单来源名称")
    private String orderFromName;
    @ApiModelProperty(value = "价格")
    private BigDecimal cardPrice;
    @ApiModelProperty(value = "生成HRA TICKET的数量")
    private Integer ticketNum;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建者")
    private String creator;
    @ApiModelProperty(value = "是否删除：0-未删除；1-已删除；")
    private Boolean deleteStatus;

    @ApiModelProperty(value = "共有几张券")
    private Integer totalCount;
    @ApiModelProperty(value = "已使用了几张券")
    private Integer useCount;
    @ApiModelProperty(value = "HRA评估券列表")
    private List<HraTicketDTO> ticketList;
    @ApiModelProperty(value = "是否可以赠送")
    private boolean handsel;
    @ApiModelProperty(value = "赠送状态：1-赠送中；2-已被领取；")
    private Integer handselStatus;

}
