package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniChoiceDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 题干 实体
 */
@Table(name = "healthy_choice")
@Data
public class MiniChoice implements Serializable {


    private static final long serialVersionUID = 7865317154266326940L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer evalutingId;//评测ID
    private String stem;//题干
    private String type;//选择题类型(单选：s 多选：m)
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;


    public MiniChoice() {
    }

    /**
     * 用业务对象MiniChoiceDTO初始化数据库对象MiniChoice。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniChoice(MiniChoiceDTO dto) {
        this.id = dto.getId();
        this.evalutingId = dto.getEvalutingId();
        this.stem = dto.getStem();
        this.type = dto.getType();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MiniChoiceDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniChoiceDTO dto) {
        dto.setId(this.id);
        dto.setEvalutingId(this.evalutingId);
        dto.setStem(this.stem);
        dto.setType(this.type);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
