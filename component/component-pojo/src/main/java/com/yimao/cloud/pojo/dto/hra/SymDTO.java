package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-05-24 10:55:46
 **/
@Data
public class SymDTO implements Serializable {

    private static final long serialVersionUID = -9001449508303862808L;

    private Integer symptomId;
    private String symptomName;

}
