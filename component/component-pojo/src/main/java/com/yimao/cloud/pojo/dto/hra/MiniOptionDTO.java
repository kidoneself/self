package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-04-24 11:36:27
 **/
@Data
public class MiniOptionDTO {

    private Integer id;
    private Integer choiceId;
    private String optionContent;//备选项内容
    private String optionIndex;//选项:1:A 2:B 3:C 4:D 5:E
    private Integer optionScore;//选项默认分数 ：10
    private String state;//默认是0 0:未选中 1:选中
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;
}
