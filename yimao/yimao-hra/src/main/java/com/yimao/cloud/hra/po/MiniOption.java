package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniOptionDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 选项 实体类
 */
@Table(name = "healthy_option")
@Data
public class MiniOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public MiniOption() {
    }

    /**
     * 用业务对象HealthyOptionDTO初始化数据库对象HealthyOption。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniOption(MiniOptionDTO dto) {
        this.id = dto.getId();
        this.choiceId = dto.getChoiceId();
        this.optionContent = dto.getOptionContent();
        this.optionIndex = dto.getOptionIndex();
        this.optionScore = dto.getOptionScore();
        this.state = dto.getState();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthyOptionDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniOptionDTO dto) {
        dto.setId(this.id);
        dto.setChoiceId(this.choiceId);
        dto.setOptionContent(this.optionContent);
        dto.setOptionIndex(this.optionIndex);
        dto.setOptionScore(this.optionScore);
        dto.setState(this.state);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
