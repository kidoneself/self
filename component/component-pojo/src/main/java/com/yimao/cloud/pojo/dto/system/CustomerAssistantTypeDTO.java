package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 客服问答类型
 *
 * @author lizhiqiang
 * @date 2019/1/14
 */
@Getter
@Setter
@ApiModel(description = "客服问答类型")
public class CustomerAssistantTypeDTO implements Serializable {
    
    private static final long serialVersionUID = 7829896680841660595L;

    @ApiModelProperty(position = 1, value = "客服问答类型ID")
    private Integer id;
    @ApiModelProperty(position = 2, value = "类型名称")
    private String typeName;
    @ApiModelProperty(position = 3, value = "类型唯一标识")
    private Integer typeCode;
    @ApiModelProperty(position = 4, value = "是否删除")
    private Boolean deleteFlag;
    @ApiModelProperty(position = 5, value = "展示端")
    private Integer terminal;
    @ApiModelProperty(position = 6, value = "排序")
    private Integer sorts;

    @ApiModelProperty(position = 5, value = "助手问答")
    private List<CustomerAssistantDTO> customerAssistantList;

}
