package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 服务站公司服务区域承包转让前端传参信息封装类
 *
 */
@Data
public class ServiceAreaContractInfoDTO {

    @ApiModelProperty(value = "被承包服务站公司信息以及服务区域信息")
    private List<StationCompanyServiceAreaDTO> originalStationCompanyServiceAreaList;
    @ApiModelProperty(value = "承包方服务站公司信息以及服务站信息")
    private ContractStationAndStationCompanyDTO contractStationAndStationCompany;
    @ApiModelProperty(value = "转让标识（1-转让方转了所有售后服务区域；2-转让方仅转让部分售后服务区域）",required = true)
    private Integer type;


}
