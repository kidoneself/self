package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniAnswersRecordDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 答案记录 实体
 */
@Table(name = "healthy_answers_record")
@Data
public class MiniAnswersRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;//用户ID
    private Integer classifyId;
    private Integer evaluateId;
    private Integer choiceId;
    private Integer optionId;
    private Date createTime;//创建时间
    private String creator;

    public MiniAnswersRecord() {
    }

    /**
     * 用业务对象HealthyAnswersRecordDTO初始化数据库对象HealthyAnswersRecord。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniAnswersRecord(MiniAnswersRecordDTO dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.classifyId = dto.getClassifyId();
        this.evaluateId = dto.getEvaluateId();
        this.choiceId = dto.getChoiceId();
        this.optionId = dto.getOptionId();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthyAnswersRecordDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniAnswersRecordDTO dto) {
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setClassifyId(this.classifyId);
        dto.setEvaluateId(this.evaluateId);
        dto.setChoiceId(this.choiceId);
        dto.setOptionId(this.optionId);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
    }
}
