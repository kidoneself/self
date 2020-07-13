package com.yimao.cloud.pojo.dto.system;

import lombok.Data;

import java.util.List;

/**
 * 修改门店服务区域信息后同步该门店下的安装工服务区域
 *
 */
@Data
public class UpdateEngineerServiceAreaDataInfo{

    private List<StationServiceAreaDTO> serviceAreaList;
    private Integer stationId;
}
