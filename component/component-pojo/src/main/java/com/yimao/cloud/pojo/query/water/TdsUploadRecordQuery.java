package com.yimao.cloud.pojo.query.water;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：设备TDS值上传记录查询条件
 *
 * @Author Zhang Bo
 * @Date 2019/7/11
 */
@ApiModel(description = "设备TDS值上传记录查询条件")
@Getter
@Setter
public class TdsUploadRecordQuery {

    @ApiModelProperty(position = 1, value = "水机设备SN码")
    private String sn;
    @ApiModelProperty(position = 2, value = "操作类型：1-恢复TDS；2-修改TDS")
    private Integer type;
    @ApiModelProperty(position = 3, value = "审核状态：Y-已审核；N-未审核；")
    private String verifyStatus;
    @ApiModelProperty(position = 4, value = "开始时间")
    private Date startTime;
    @ApiModelProperty(position = 5, value = "结束时间")
    private Date endTime;

}
