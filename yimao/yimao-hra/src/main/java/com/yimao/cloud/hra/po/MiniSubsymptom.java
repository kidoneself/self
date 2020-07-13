package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniSubsymptomDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 子症状 实体
 */
@Table(name = "healthy_subsymptom")
@Data
public class MiniSubsymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer resultId;
    private Integer symptomId;
    private Integer hotFlag;//是否为热门 默认0
    private String subsymptomName;//子症状名称
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public MiniSubsymptom() {
    }

    /**
     * 用业务对象HealthySubsymptomDTO初始化数据库对象HealthySubsymptom。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniSubsymptom(MiniSubsymptomDTO dto) {
        this.id = dto.getId();
        this.resultId = dto.getResultId();
        this.symptomId = dto.getSymptomId();
        this.hotFlag = dto.getHotFlag();
        this.subsymptomName = dto.getSubsymptomName();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthySubsymptomDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniSubsymptomDTO dto) {
        dto.setId(this.id);
        dto.setResultId(this.resultId);
        dto.setSymptomId(this.symptomId);
        dto.setHotFlag(this.hotFlag);
        dto.setSubsymptomName(this.subsymptomName);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
