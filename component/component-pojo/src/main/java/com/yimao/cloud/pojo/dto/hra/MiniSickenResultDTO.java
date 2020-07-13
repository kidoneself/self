package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 患病结果 实体类
 * @author: yu chunlei
 * @create: 2018-04-28 15:33:42
 **/
@Data
public class MiniSickenResultDTO implements Serializable {

    private static final long serialVersionUID = 966822273376487912L;
    private Integer id;
    private Integer symptomId;
    private String sickenName; //疾病名称
    private String sickenNameExplain;//疾病介绍
    private String symptom_detail;//症状
    private String pathogeny;//病因
    private String chineseTreatment;//中医治疗
    private String prevention;//预防
    private String nursing;//护理
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

}
