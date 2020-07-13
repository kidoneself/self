package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 承包服务站公司以及门店信息
 */
@Data
public class ContractStationAndStationCompanyDTO {

    @ApiModelProperty(value = "服务站公司id")
    private Integer stationCompanyId;
    @ApiModelProperty(value = "服务站门店id")
    private Integer stationId;
    @ApiModelProperty(value = "服务站门店下的某个安装工")
    private Integer engineerId;
}
