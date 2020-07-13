package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评测 实体类
 */
@Data
public class MiniEvaluatingDTO implements Serializable {

    private static final long serialVersionUID = -5934687016217387012L;
    private Integer id;
    private Integer classifyId;//父类别ID
    private Integer secondId;//父类别ID
    private String subImg;//选项图片
    private String startImg;//开始页图片
    private Integer joinNumber;//参加人数
    private String evaluatingTitle;//标题
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;



}
