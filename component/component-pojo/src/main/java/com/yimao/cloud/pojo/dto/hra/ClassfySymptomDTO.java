package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 分类名称以及热门症状  数据封装
 * @author: yu chunlei
 * @create: 2018-06-12 14:28:24
 **/
@Data
public class ClassfySymptomDTO implements Serializable {

    private static final long serialVersionUID = -6481831164223111756L;

    private Integer classfyId;//类别ID
    private String classfyName;//分类名称
    private List<MiniSymptomDTO> symptomList;//热门症状列表

}
