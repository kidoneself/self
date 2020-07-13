package com.yimao.cloud.pojo.dto.station;

import java.util.List;

import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "帮助中心默认列表展示列及其热门前三问题")
public class StationHelperDataDTO {
	@ApiModelProperty(value = "客服问答ID")
    private Integer id;
    @ApiModelProperty(value = "类型")
    private Integer typeCode;
    @ApiModelProperty(value = "排序")
    private Integer sorts;
    @ApiModelProperty(value = "类型名称")
    private String typeName;
    @ApiModelProperty(value = "问答列表")
    private List<CustomerAssistantDTO> recommendQuestionList;
}
