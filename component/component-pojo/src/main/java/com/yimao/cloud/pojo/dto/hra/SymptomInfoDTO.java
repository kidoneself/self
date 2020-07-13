package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 症状信息 数据封装
 * @author: yu chunlei
 * @create: 2018-05-23 13:47:43
 **/
@Data
public class SymptomInfoDTO implements Serializable {

    private static final long serialVersionUID = 5000068619076697456L;

    private Integer symptomId;//症状ID
    private String symptomName;//症状名称
    private List<MiniSymptomDTO> symptomDtoList;
    private List<MiniSicknessDTO> sicknessDtoList;

}
