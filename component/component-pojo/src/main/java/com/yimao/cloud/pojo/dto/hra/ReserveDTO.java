package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author liuhao@yimaokeji.com
 *         2017122017/12/18
 */
@Getter
@Setter
@ApiModel(description = "预约评估对象")
public class ReserveDTO implements Serializable {

    private static final long serialVersionUID = 9206552939072363032L;

    @ApiModelProperty(value = "客户ID", position = 1)
    private Integer customerId;
    @ApiModelProperty(value = "评估卡号", position = 2)
    private String ticketNo;
    @ApiModelProperty(value = "评估人姓名", position = 3)
    private String userName;
    @ApiModelProperty(value = "身份证", position = 4)
    private String idCard;
    @ApiModelProperty(value = "电话", position = 5)
    private String phone;
    @ApiModelProperty(value = "生日", position = 6)
    private String birthDate;
    @ApiModelProperty(value = "性别", position = 7)
    private String sex;
    @ApiModelProperty(value = "年龄", position = 8)
    private String age;
    @ApiModelProperty(value = "身高", position = 9)
    private String height;
    @ApiModelProperty(value = "体重", position = 10)
    private String weight;
    @ApiModelProperty(value = "服务站ID", position = 11)
    private Integer stationId;
    @ApiModelProperty(value = "服务站名称", position = 12)
    private String stationName;
    @ApiModelProperty(value = "预约时间", position = 13)
    private String reserveTime;
    @ApiModelProperty(value = "1-终端用户app，2-微信公众号，3-经销商APP 5-后台管理系统", position = 14)
    private Integer reserveFrom;
}
