package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: DTO
 * @author: yu chunlei
 * @create: 2018-04-28 09:45:22
 **/
@Data
public class MiniSecondClassifyDTO implements Serializable {

    private static final long serialVersionUID = -4289360505902670805L;
    private Integer id;
    private Integer pid;
    private Integer classifyId;
    private String secondName;
    protected String creator;
    protected Date createTime;
    protected String updater;
    protected Date updateTime;

    private List<MiniSymptomDTO> symptomList;

}
