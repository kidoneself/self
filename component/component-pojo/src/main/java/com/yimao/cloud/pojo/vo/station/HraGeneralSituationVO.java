package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 站务系统-HRA-HRA概况返回类
 *
 * @author Liu Long Jie
 */
@Data
@ApiModel(description = "HRA概况返回类")
public class HraGeneralSituationVO {

    @ApiModelProperty(value = "今日待评估")
    private Integer todayNeedAssess;

    @ApiModelProperty(value = "总完成评估")
    private Integer totalFinishAssess;

    public HraGeneralSituationVO() {
    }

    public HraGeneralSituationVO(Integer todayNeedAssess, Integer totalFinishAssess) {
        this.todayNeedAssess = todayNeedAssess;
        this.totalFinishAssess = totalFinishAssess;
    }
}
