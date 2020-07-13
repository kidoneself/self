package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 数据封装
 * @author: yu chunlei
 * @create: 2018-05-04 17:35:30
 **/
@Data
public class ChoiceOptionDTO implements Serializable {
    private static final long serialVersionUID = 4285828001893661200L;

    private Integer id;
    private Integer evalutingId;//评测ID
    private String stem;//题干
    private Date createTime;//创建时间

    private List<MiniOptionDTO> optionList;

}
