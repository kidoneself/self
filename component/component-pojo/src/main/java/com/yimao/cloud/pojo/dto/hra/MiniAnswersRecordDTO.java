package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-21 17:33:17
 **/
@Data
public class MiniAnswersRecordDTO implements Serializable {

    private static final long serialVersionUID = 3943226475265918966L;
    private Integer id;
    private Integer userId;//用户ID
    private Integer classifyId;
    private Integer evaluateId;
    private Integer choiceId;
    private Integer optionId;
    private Date createTime;//创建时间
    private String creator;

}
