package com.yimao.cloud.pojo.query.station;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Set;


/**
 * 描述：工单概况查询条件 （服务站站务系统）
 *
 * @Author Liu Long Jie
 */
@ApiModel(description = "工单概况查询条件（服务站站务系统）")
@Data
public class StationOrderGeneralSituationQuery {

    private Set<Integer> areas;
    private List<Integer> engineerIds;
    private List<Integer> distributorIds;
    private Integer type; //服务权限类型
}
