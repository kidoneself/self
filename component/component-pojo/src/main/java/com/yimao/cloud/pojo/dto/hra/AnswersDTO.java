package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 封装答案数据信息
 * @author: yu chunlei
 * @create: 2018-05-05 14:33:12
 **/
@Data
public class AnswersDTO implements Serializable {

    private static final long serialVersionUID = 7693424474714921934L;

    private String title;
    private String content;
    private Integer returnScore;//返回分数
    private String percentage;//百分比
    private Integer resultScore;//结果分数

}
