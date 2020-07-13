package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 一级分类二级分类数据封装
 * @author: yu chunlei
 * @create: 2018-05-07 17:27:06
 **/
@Data
public class SymptomAndSubsymDTO implements Serializable {

    private static final long serialVersionUID = 6000853724288242488L;

    private List<MiniSymptomDTO> symptomList;
    private List<MiniSubsymptomDTO> subsymptomList;

}
