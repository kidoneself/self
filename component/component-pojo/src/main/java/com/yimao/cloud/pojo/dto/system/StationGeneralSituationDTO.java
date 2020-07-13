package com.yimao.cloud.pojo.dto.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lizhqiang
 * @date 2019/2/13
 */
@Data
public class StationGeneralSituationDTO {

    @ApiModelProperty(value = "服务站公司数量")
    private Integer StationCompanyNum;

    @ApiModelProperty(value = "服务站门店数量")
    private Integer stationNum;

    @ApiModelProperty(value = "已承包数量")
    private Integer contractNum;

    @ApiModelProperty(value = "站务系统开通数量")
    private Integer openNum;

}
