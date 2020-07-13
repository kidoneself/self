package com.yimao.cloud.pojo.dto.hra;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @description: 评估卡分配-分配数量统计DTO
 * @author: yu chunlei
 * @create: 2019-02-20 14:31:35
 **/
@Data
public class AllotReportDTO implements Serializable {

    private static final long serialVersionUID = 4770485006109488444L;
    @ApiModelProperty(position = 1,value = "地区")
    private String region;
    @ApiModelProperty(position = 2,value = "地区名称")
    private String regionName;
    @ApiModelProperty(position = 3,value = "开始时间")
    private String beginTime;
    @ApiModelProperty(position = 4,value = "结束时间")
    private String endTime;
    @ApiModelProperty(position = 5,value = "分配数量")
    private Integer allotCount;
    @ApiModelProperty(position = 6,value = "使用数量")
    private Integer userCount;
    @ApiModelProperty(position = 7,value = "未使用数量")
    private Integer noUserCount;
    @ApiModelProperty(position = 8,value = "未禁用数量")
    private Integer unForbiddenCount;
    @ApiModelProperty(position = 9,value = "已禁用数量")
    private Integer forbiddenCount;
}
