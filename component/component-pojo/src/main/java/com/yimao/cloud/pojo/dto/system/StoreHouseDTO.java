package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhilin.he
 * @description 云平台仓库
 * @date 2019/5/5 9:38
 **/
@ApiModel(description = "云平台仓库")
@Getter
@Setter
public class StoreHouseDTO {

    @ApiModelProperty(value = "库存id")
    private Integer id;
    @ApiModelProperty(value = "产库位置：1.总仓，0.分仓")
    private Integer place;   //位置
    @ApiModelProperty(value = "省")
    private String province; //省
    @ApiModelProperty(value = "市")
    private String city;     //市
    @ApiModelProperty(value = "区")
    private String region;   //区
    @ApiModelProperty(value = "库存数组")
    private String stocks;   //库存数组
    @ApiModelProperty(value = "是否特殊库存：0-否，1-是")
    private Integer special; //是否特殊库存：0-否，1-是
    @ApiModelProperty(value = "最大值数组")
    private String maxValues; //最大值数组
    @ApiModelProperty(value = "创建时间")
    private Date createTime; //创建时间
    @ApiModelProperty(value = "修改时间")
    private Date updateTime; //修改时间

}
