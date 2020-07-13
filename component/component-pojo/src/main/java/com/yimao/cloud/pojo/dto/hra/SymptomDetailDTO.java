package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 症状详情数据封装
 * @author: yu chunlei
 * @create: 2018-05-23 15:43:06
 **/
@Data
public class SymptomDetailDTO implements Serializable {

    private static final long serialVersionUID = -5118818158270133052L;

    private Integer id;
    private String symptomName;//症状名称
    private String symptomIntro;//症状介绍
    private String symptomDetail;//症状详情

}
