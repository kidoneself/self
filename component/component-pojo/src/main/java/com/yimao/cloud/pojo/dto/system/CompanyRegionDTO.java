package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019/1/18
 */

@Data
public class CompanyRegionDTO {

    @ApiModelProperty(value = "公司服务区的id")
    private Integer id;
    @ApiModelProperty(value = "公司服务省")
    private String province;
    @ApiModelProperty(value = "公司服务市")
    private String city;
    @ApiModelProperty(value = "公司服务区")
    private String region;
    @ApiModelProperty(value = "公司服务区的状态")
    private Integer corporationStatus;


}
