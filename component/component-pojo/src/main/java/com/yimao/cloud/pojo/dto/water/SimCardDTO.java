package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述：SIM卡信息DTO
 *
 * @Author Zhang Bo
 * @Date 2019/4/26
 */
@ApiModel(description = "SIM卡信息DTO")
@Getter
@Setter
public class SimCardDTO {

    private String iccid;
    private String status;
    private String activatingTime;
    private String monthDataFlowUsed;
    private String simCompany;

}
