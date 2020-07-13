package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 描述：手动修改水机配额
 *
 * @Author Zhang Bo
 * @Date 2019/5/7
 */
@ApiModel(description = "手动修改水机配额查询条件")
@Getter
@Setter
public class ManualPadCostQuery implements Serializable {

    private static final long serialVersionUID = 6647891171932497357L;

    @ApiModelProperty(position = 1, value = "SN码")
    private String sn;
    @ApiModelProperty(position = 1, value = "是否开启：0-未开启；1-开启；")
    private Boolean open;
    @ApiModelProperty(position = 1, value = "同步状态：0-未同步；1-同步完成；2-同步失败；")
    private Integer syncStatus;

}