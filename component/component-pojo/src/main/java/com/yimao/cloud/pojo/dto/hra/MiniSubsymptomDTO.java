package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-04-24 11:42:51
 **/
@Data
public class MiniSubsymptomDTO implements Serializable {

    private static final long serialVersionUID = -1962944315089821656L;
    private Integer id;
    private Integer resultId;
    private Integer symptomId;
    private Integer hotFlag;//是否为热门 默认0
    private String subsymptomName;//子症状名称
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

}
