package com.yimao.cloud.pojo.vo.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/***
 * 功能描述:产品
 *
 * @auther: liu yi
 * @date: 2019/4/22 10:22
 */
@Getter
@Setter
public class ProductModalVO implements Serializable {
    private static final long serialVersionUID = 4727835418645950791L;
    public static final String OPERATION_TYPE_YUN = "YIMAOYUN";
    public static final String OPERATION_TYPE_MIC = "YIMAOMIC";
    @ApiModelProperty(value = "产品ID")
    private String id;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "创建人")
    private String createUser;
    @ApiModelProperty(value = "更新人")
    private String updateUser;
    @ApiModelProperty(value = "是否删除")
    private String delStatus;
    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;
    private String yimaoOldSystemId;
    @ApiModelProperty(value = "id状态")
    private String idStatus;
    @ApiModelProperty(value = "套餐id")
    private String payAccountId;
    @ApiModelProperty(value = "套餐名称")
    private String payAccountName;
    @ApiModelProperty(value = "产品")
    private String goodsSystemId;
    @ApiModelProperty(value = "产品id")
    private String productId;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "二级分类id")
    private String kindId;
    @ApiModelProperty(value = "二级分类名臣")
    private String kindName;
    @ApiModelProperty(value = "三级分类名称")
    private String modalName;
    @ApiModelProperty(value = "产品描述")
    private String modalDescription;
    //private Integer productPrice;
    @ApiModelProperty(value = "产品价格")
    private BigDecimal productPrice;
    @ApiModelProperty(value = "产品图片")
    private String img;
    @ApiModelProperty(value = "产品计费方式")
    private String costs;
    @ApiModelProperty(value = "产品续费套餐")
    private String renews;

}
