package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.system.StationServiceAreaDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 服务站门店（原服务站）
 *
 * @author liu long jie
 * @date 2019/12/26
 */
@ApiModel(description = "服务站门店相关信息及其服务区域")
@Getter
@Setter
public class StationVO {

    @ApiModelProperty(value = "服务站门店主键")
    private Integer id;

    @ApiModelProperty(value = "服务站门店名称")
    private String name;
    
    @ApiModelProperty(value = "服务站门店类型")
    private Integer serviceType;

    @ApiModelProperty(value = "服务站服务区域")
    private List<StationServiceAreaDTO> serviceAreas;
    
    @ApiModelProperty(value = "服务站门店是否拥有true-有 false-没有")
    private boolean stationHave;
}
