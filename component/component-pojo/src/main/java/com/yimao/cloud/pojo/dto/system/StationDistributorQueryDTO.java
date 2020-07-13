package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 查询服务站站长参数
 *
 * @author hhf
 * @date 2019/6/13
 */
@Data
public class StationDistributorQueryDTO {

    @ApiModelProperty(value = "搜索参数")
    private String param;
    @ApiModelProperty(value = "服务区域省集合")
    private List<String> provinces;
    @ApiModelProperty(value = "服务区域市集合")
    private List<String> citys;
    @ApiModelProperty(value = "服务区域区集合")
    private List<String> regions;
}
