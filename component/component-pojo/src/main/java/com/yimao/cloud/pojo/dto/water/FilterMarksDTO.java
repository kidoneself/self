package com.yimao.cloud.pojo.dto.water;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 描述：滤芯初始工作标记
 *
 * @Author Zhang Bo
 * @Date 2019/5/13
 */
@ApiModel(description = "滤芯初始工作标记DTO")
@Getter
@Setter
public class FilterMarksDTO {

    private Integer id;
    private String sn;
    private Integer deviceId;
    private Integer pp;
    private Integer cto;
    private Integer udf;
    private Integer three;
    private Integer ro;
    private Date createTime;

}
