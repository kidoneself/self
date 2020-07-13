package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 封装
 * @author: yu chunlei
 * @create: 2018-05-07 15:03:01
 **/
@Data
public class SubSymptomDTO implements Serializable {

    private static final long serialVersionUID = -4447536498421597730L;

    private Integer id;
    private Integer resultId;
    private Integer symptomId;
    private String subsymptomName;//子症状名称

    private List<String> subList;//子症状名称
    private MiniSickenResultDTO result;
    private List<MiniSickenResultDTO> sickenResultList;

}
