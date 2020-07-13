package com.yimao.cloud.pojo.vo.station;

import java.util.Date;
import java.util.List;

import com.yimao.cloud.pojo.dto.system.StationCompanyServiceAreaDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务站公司展示类
 * @author yaoweijun
 *
 */
@ApiModel(description = "服务站公司展示类")
@Getter
@Setter
public class StationCompanyVO {
	 	@ApiModelProperty(value = "ID")
	    private Integer id;

	    @ApiModelProperty(position = 1, value = "区县级公司名称")
	    private String name;
	    
	    @ApiModelProperty(position = 4, value = "省")
	    private String province;

	    @ApiModelProperty(position = 5, value = "市")
	    private String city;

	    @ApiModelProperty(position = 6, value = "区")
	    private String region;
	    
	    @ApiModelProperty(position = 7, value = "0-售前+售后；1-售前 ； 2-售后")
	    private Integer serviceType;
}
