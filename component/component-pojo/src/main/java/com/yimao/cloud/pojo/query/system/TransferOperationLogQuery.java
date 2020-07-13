package com.yimao.cloud.pojo.query.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Liu Long Jie
 * @date 2020-7-2
 */
@ApiModel(description = "转让操作记录列表查询条件")
@Getter
@Setter
public class TransferOperationLogQuery {

    //原服务站公司编号
    private String origStationCompanyCode;
    //原服务站公司名称
    private String origStationCompanyName;
    //承包服务站公司编号
    private String destStationCompanyCode;
    //承包服务站公司名称
    private String destStationCompanyName;
    //详情信息（关键字）
    private String description;

}
