package com.yimao.cloud.pojo.vo.station;

import com.yimao.cloud.pojo.dto.user.EngineerServiceAreaDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 描述：安装工程师
 *
 * @Author Liu Long Jie
 * @Date 2020-1-16
 */
@ApiModel(description = "安装工VO(站务系统)")
@Getter
@Setter
public class EngineerVO implements Serializable {

    private static final long serialVersionUID = 8425079860412347879L;

    private Integer id;

    // -----------------------  查询列表需要字段 -------------------------------------
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "姓名")
    private String realName;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "创建时间", example = "1970-01-01 00:00:00")
    private Date createTime;
    @ApiModelProperty(value = "账号是否被禁用：0-否，1-是")
    private Boolean forbidden;

    // -----------------------  详情需要字段（部分字段在上面） -------------------------------------
    @ApiModelProperty(value = "性别（1：男，2：女）")
    private Integer sex;
    @ApiModelProperty(value = "身份证号码")
    private String idCard;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "服务站公司名称")
    private String stationCompanyName;
    @ApiModelProperty(value = "服务站门店名称")
    private String stationName;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String region;

    // -----------------------  详情要返回areaId,用作接口安全校验 -------------------------------------
    @ApiModelProperty(value = "服务地区id")
    private Integer areaId;

    @ApiModelProperty(value = "安装工服务区域list")
    private List<EngineerServiceAreaDTO> serviceAreaList;

}
