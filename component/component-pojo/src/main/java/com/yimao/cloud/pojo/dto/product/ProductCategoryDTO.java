package com.yimao.cloud.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 产品类目
 *
 * @author Zhang Bo
 * @date 2018/11/7.
 */
@ApiModel
@Getter
@Setter
public class ProductCategoryDTO {

    @ApiModelProperty(value = "分类id")
    private Integer id;
    @ApiModelProperty(position = 1, value = "产品类目名称")
    private String name;                    //产品类目名称
    @ApiModelProperty(position = 2, value = "产品类目code")
    private String code;                    //产品类目code
    @ApiModelProperty(position = 3, value = "产品类目图标")
    private String icon;                    //产品类目图标
    @ApiModelProperty(position = 4, value = "前台类目还是后台类目：1-后台类目；2-前台类目；")
    private Integer type;                   //前台类目还是后台类目：1-后台类目；2-前台类目；
    @ApiModelProperty(position = 5, value = "终端：1-健康e家公众号；3-翼猫APP")
    private Integer terminal;               //终端：1-用户终端APP；2-微信公众号；3-经销商APP；4-小猫店小程序；
    @ApiModelProperty(position = 6, value = "父类目id，当id=0时说明是根节点，一级类目")
    private Integer pid;                    //父类目id，当id=0时说明是根节点，一级类目
    @ApiModelProperty(position = 7, value = "产品类目等级：1-一级；2-二级；3-三级；")
    private Integer level;                  //产品类目等级：1-一级；2-二级；3-三级；
    @ApiModelProperty(position = 8, value = "产品公司ID")
    private Integer companyId;              //产品公司ID
    @ApiModelProperty(position = 9, value = "产品公司名称")
    private String companyName;             //产品公司名称
    @ApiModelProperty(position = 10, value = "最小起订量")
    private Integer minMoq;                 //最小起订量
    @ApiModelProperty(position = 11, value = "最大限订量")
    private Integer maxMoq;                 //最大限订量
    @ApiModelProperty(position = 12, value = "描述")
    private String description;             //描述
    @ApiModelProperty(position = 13, value = "排序")
    private Integer sorts;                  //排序
    @ApiModelProperty(position = 14, value = "是否删除：0-未删除；1-已删除")
    private Boolean deleted;                //是否删除

    @ApiModelProperty(position = 100, value = "创建人")
    private String creator;                //创建人
    @ApiModelProperty(position = 101, value = "创建时间")
    private Date createTime;               //创建时间
    @ApiModelProperty(position = 102, value = "更新人")
    private String updater;                //更新人
    @ApiModelProperty(position = 103, value = "更新时间")
    private Date updateTime;               //更新时间
    @ApiModelProperty(position = 104, value = "老的产品类目id")
    private String oldId;
    @ApiModelProperty(position = 105, value = "类目对应的库存物资id")
    private Integer storeGoodsId;

}