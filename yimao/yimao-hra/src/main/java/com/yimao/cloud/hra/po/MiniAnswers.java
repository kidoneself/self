package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniAnswersDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 答案 实体类
 */
@Table(name = "healthy_answers")
@Data
public class MiniAnswers{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public MiniAnswers() {
    }

    /**
     * 用业务对象HealthyAnswersDTO初始化数据库对象HealthyAnswers。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniAnswers(MiniAnswersDTO dto) {
        this.id = dto.getId();
        this.evaluatingId = dto.getEvaluatingId();
        this.answerTitle = dto.getAnswerTitle();
        this.content = dto.getContent();
        this.score1 = dto.getScore1();
        this.score2 = dto.getScore2();
        this.resultScore = dto.getResultScore();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthyAnswersDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniAnswersDTO dto) {
        dto.setId(this.id);
        dto.setEvaluatingId(this.evaluatingId);
        dto.setAnswerTitle(this.answerTitle);
        dto.setContent(this.content);
        dto.setScore1(this.score1);
        dto.setScore2(this.score2);
        dto.setResultScore(this.resultScore);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
