package com.yimao.cloud.pojo.vo.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 站务系统-用户-用户概况返回类
 *
 * @author Liu Long Jie
 */
@Data
@ApiModel(description = "用户概况返回类")
public class UserGeneralSituationVO {

    //---- 用户 -----
    @ApiModelProperty(position = 1, required = true, value = "用户总数")
    private Integer userTotalNum;
    @ApiModelProperty(position = 2, required = true, value = "普通用户数量")
    private Integer generalUserNum;
    @ApiModelProperty(position = 3, required = true, value = "会员用户数量")
    private Integer vipUserNum;

    //---- 经销商 ---------
    @ApiModelProperty(position = 4, required = true, value = "经销商总数量")
    private Integer distributorTotalNum;
    @ApiModelProperty(position = 5, required = true, value = "微创版经销商数量")
    private Integer microinvasiveDistributorNum;
    @ApiModelProperty(position = 6, required = true, value = "个人版经销商数量")
    private Integer personageDistributorNum;
    @ApiModelProperty(position = 7, required = true, value = "体验版经销商数量")
    private Integer experienceDistributorNum;
    @ApiModelProperty(position = 8, required = true, value = "企业版经销商(主)数量")
    private Integer enterpriseDistributorNum;
    @ApiModelProperty(position = 9, required = true, value = "企业版经销商(子)数量")
    private Integer enterpriseSonDistributorNum;
    @ApiModelProperty(position = 10, required = true, value = "折机版经销商数量")
    private Integer discountDistributorNum;

    //---- 代理商-----
    @ApiModelProperty(position = 11, required = true, value = "代理商总数量")
    private Integer agentTotalNum;
    @ApiModelProperty(position = 12, required = true, value = "区县级代理商数量")
    private Integer districtAgentNum;

    public UserGeneralSituationVO() {
    }

    public UserGeneralSituationVO(Integer userTotalNum, Integer generalUserNum, Integer vipUserNum, Integer distributorTotalNum, Integer microinvasiveDistributorNum, Integer personageDistributorNum, Integer experienceDistributorNum, Integer enterpriseDistributorNum, Integer enterpriseSonDistributorNum, Integer discountDistributorNum, Integer agentTotalNum, Integer districtAgentNum) {
        this.userTotalNum = userTotalNum;
        this.generalUserNum = generalUserNum;
        this.vipUserNum = vipUserNum;
        this.distributorTotalNum = distributorTotalNum;
        this.microinvasiveDistributorNum = microinvasiveDistributorNum;
        this.personageDistributorNum = personageDistributorNum;
        this.experienceDistributorNum = experienceDistributorNum;
        this.enterpriseDistributorNum = enterpriseDistributorNum;
        this.enterpriseSonDistributorNum = enterpriseSonDistributorNum;
        this.discountDistributorNum = discountDistributorNum;
        this.agentTotalNum = agentTotalNum;
        this.districtAgentNum = districtAgentNum;
    }

    public Integer getUserTotalNum() {
        if (Objects.isNull(generalUserNum)) {
            generalUserNum = 0;
        }
        if (Objects.isNull(vipUserNum)) {
            vipUserNum = 0;
        }
        return generalUserNum + vipUserNum;
    }

    public Integer getDistributorTotalNum() {
        if (Objects.isNull(microinvasiveDistributorNum)) {
            microinvasiveDistributorNum = 0;
        }
        if (Objects.isNull(personageDistributorNum)) {
            personageDistributorNum = 0;
        }
        if (Objects.isNull(experienceDistributorNum)) {
            experienceDistributorNum = 0;
        }
        if (Objects.isNull(enterpriseDistributorNum)) {
            enterpriseDistributorNum = 0;
        }
        if (Objects.isNull(enterpriseSonDistributorNum)) {
            enterpriseSonDistributorNum = 0;
        }
        if (Objects.isNull(discountDistributorNum)) {
            discountDistributorNum = 0;
        }
        return microinvasiveDistributorNum + personageDistributorNum
                + experienceDistributorNum + enterpriseDistributorNum
                + enterpriseSonDistributorNum + discountDistributorNum;
    }

    public Integer getAgentTotalNum() {
        if (Objects.isNull(districtAgentNum)) {
            districtAgentNum = 0;
        }
        return districtAgentNum;
    }

}
