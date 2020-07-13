package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 疾病 数据封装
 * @author: yu chunlei
 * @create: 2018-05-23 14:45:23
 **/
@Data
public class MiniSicknessDTO implements Serializable {

    private static final long serialVersionUID = -8154528971414656252L;
    private Integer id;
    private Integer secondId;
    private String sickName;
    private String sickIntro;//介绍
    private String sickSymptom;//症状
    private String sickReason;//原因
    private String chineseTreat;//中医治疗
    private String sickPrevent;//护理
    private String sickNurse;//预防
    private Date createTime;//创建时间
    private String creator;

}
