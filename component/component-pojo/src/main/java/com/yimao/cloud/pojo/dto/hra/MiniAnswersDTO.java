package com.yimao.cloud.pojo.dto.hra;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-21 17:55:14
 **/
@Data
public class MiniAnswersDTO implements Serializable {
    private static final long serialVersionUID = -3113404671552078532L;
    private Integer id;
    private Integer evaluatingId;
    private String answerTitle;//答案标题
    private String content;//答案内容
    private Integer score1;
    private Integer score2;
    private Integer resultScore;
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;
}
