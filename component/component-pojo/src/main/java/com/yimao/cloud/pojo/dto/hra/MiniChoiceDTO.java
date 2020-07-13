package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-04-24 11:49:48
 **/
@Data
public class MiniChoiceDTO implements Serializable {

    private Integer id;
    private Integer evalutingId;//评测ID
    private String stem;//题干
    private String type;//选择题类型(单选：s 多选：m)
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;
}
