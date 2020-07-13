package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 封装子症状以及患病结果数据
 * @author: yu chunlei
 * @create: 2018-04-28 15:23:49
 **/
@Data
public class SubSymptomResultDTO implements Serializable {

    private static final long serialVersionUID = -647051429306482813L;

    private String symptomName;
    private String symptomDetail;
    private String symptomIntro;
    private List<String> subsymptomNameList;
    private List<MiniSickenResultDTO> sickenResultList;
    private List<SubSymptomDTO> symptomDtoList;
    private List<SickenResultDTO> resultDtoList;
}
