package com.yimao.cloud.pojo.dto.station;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 站务系统-用户统计返回类
 *
 * @author Liu Long Jie
 */
@Data
@ApiModel(description = "用户统计返回类")
public class UserStatisticsDTO {
    @ApiModelProperty(position = 1, required = true, value = "区域id")
    private Integer areaId;
    @ApiModelProperty(position = 2, required = true, value = "流水统计表格日期")
    private String date;
    @ApiModelProperty(position = 3, required = true, value = "服务站名称")
    private String stationName;
    @ApiModelProperty(position = 4, required = true, value = "流水统计图日期")
    private String createTime;

    //---- 用户 -----
    @ApiModelProperty(position = 5, required = true, value = "用户总数")
    private Integer userTotalNum;
    @ApiModelProperty(position = 6, required = true, value = "普通用户数量")
    private Integer generalUserNum;
    @ApiModelProperty(position = 7, required = true, value = "会员用户数量")
    private Integer vipUserNum;

    //---- 经销商 ---------
    @ApiModelProperty(position = 8, required = true, value = "经销商总数量")
    private Integer distributorTotalNum;
    @ApiModelProperty(position = 9, required = true, value = "微创版经销商数量")
    private Integer microinvasiveDistributorNum;
    @ApiModelProperty(position = 10, required = true, value = "个人版经销商数量")
    private Integer personageDistributorNum;
    @ApiModelProperty(position = 11, required = true, value = "体验版经销商数量")
    private Integer experienceDistributorNum;
    @ApiModelProperty(position = 12, required = true, value = "企业版经销商数量")
    private Integer enterpriseDistributorNum;

    //---- 代理商-----
    @ApiModelProperty(position = 13, required = true, value = "代理商总数量")
    private Integer agentTotalNum;
    @ApiModelProperty(position = 14, required = true, value = "区县级代理商数量")
    private Integer districtAgentNum;


    //封装返回order服务统计返回结果
    List<UserStatisticsDTO> userRes;
    List<UserStatisticsDTO> distributorRes;
    List<UserStatisticsDTO> agentRes;
    List<UserStatisticsDTO> userPicRes;
    List<UserStatisticsDTO> distributorPicRes;
    List<UserStatisticsDTO> agentPicRes;


    public Integer getUserTotalNum() {
        if (Objects.isNull(generalUserNum)) {
            generalUserNum = 0;
        }
        if (Objects.isNull(vipUserNum)) {
            vipUserNum = 0;
        }
        return generalUserNum + vipUserNum;
    }

    public Integer getGeneralUserNum() {
        if (Objects.isNull(generalUserNum)) {
            generalUserNum = 0;
        }
        return generalUserNum;
    }

    public Integer getVipUserNum() {
        if (Objects.isNull(vipUserNum)) {
            vipUserNum = 0;
        }
        return vipUserNum;
    }

    public Integer getDistributorTotalNum() {
        if (Objects.isNull(distributorTotalNum)) {
            distributorTotalNum = 0;
        }
        return distributorTotalNum;
    }

    public Integer getMicroinvasiveDistributorNum() {
        if (Objects.isNull(microinvasiveDistributorNum)) {
            microinvasiveDistributorNum = 0;
        }
        return microinvasiveDistributorNum;
    }

    public Integer getPersonageDistributorNum() {
        if (Objects.isNull(personageDistributorNum)) {
            personageDistributorNum = 0;
        }
        return personageDistributorNum;
    }

    public Integer getExperienceDistributorNum() {
        if (Objects.isNull(experienceDistributorNum)) {
            experienceDistributorNum = 0;
        }
        return experienceDistributorNum;
    }

    public Integer getEnterpriseDistributorNum() {
        if (Objects.isNull(enterpriseDistributorNum)) {
            enterpriseDistributorNum = 0;
        }
        return enterpriseDistributorNum;
    }

    public Integer getAgentTotalNum() {
        if (Objects.isNull(agentTotalNum)) {
            agentTotalNum = 0;
        }
        return agentTotalNum;
    }

    public Integer getDistrictAgentNum() {
        if (Objects.isNull(districtAgentNum)) {
            districtAgentNum = 0;
        }
        return districtAgentNum;
    }
}
