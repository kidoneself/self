package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 患病结果数据封装
 * @author: yu chunlei
 * @create: 2018-05-07 10:41:58
 **/
@Data
public class SickenResultDTO implements Serializable {

    private static final long serialVersionUID = 5928838599909576260L;

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
    private List<MiniSubsymptomDTO> subsymptomList;

}
