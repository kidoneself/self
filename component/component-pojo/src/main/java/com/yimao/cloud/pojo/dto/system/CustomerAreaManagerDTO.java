package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户区域经理
 *
 * @author liuhao@yimaokeji.com
 *         2018052018/5/16
 */
@Data
@ApiModel(description = "区域经理")
public class CustomerAreaManagerDTO implements Serializable {
    private static final long serialVersionUID = 166735427879295424L;

    @ApiModelProperty("主键")
    private Integer id;
    @ApiModelProperty(position = 1, value = "名称")
    private String technicalName;
    @ApiModelProperty(position = 2, value = "区域职称")
    private String technicalArea;
    @ApiModelProperty(position = 3, value = "手机号")
    private String mobile;
    @ApiModelProperty(position = 4, value = "省")
    private String province;
    @ApiModelProperty(position = 7, value = "备注")
    private String remark;
    @ApiModelProperty(position = 8, value = "是否删除")
    private Boolean deleteFlag;
    @ApiModelProperty(position = 9, value = "创建人")
    protected String creator;
    @ApiModelProperty(position = 10, value = "创建时间")
    protected Date createTime;
    @ApiModelProperty(position = 11, value = "更新人")
    protected String updater;
    @ApiModelProperty(position = 12, value = "更新时间")
    protected Date updateTime;
}
