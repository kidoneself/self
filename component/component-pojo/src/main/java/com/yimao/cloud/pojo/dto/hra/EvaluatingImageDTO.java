package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 题目信息类
 * @author: yu chunlei
 * @create: 2018-06-11 11:39:53
 **/
@Data
public class EvaluatingImageDTO implements Serializable {


    private static final long serialVersionUID = -5685567375958145260L;
    private Integer id;
    private String subImg;
    private String startImg;
    private String evaluatingTitle;

}
