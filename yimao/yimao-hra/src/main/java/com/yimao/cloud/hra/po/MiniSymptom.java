package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniSymptomDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 症状实体类
 */
@Table(name = "healthy_symptom")
@Data
public class MiniSymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer parentId;
    private Integer secondId;
    private Integer hotFlag;//0：非热门 1：热门
    private String symptomName;
    private String symptomIntro;//症状介绍
    private String symptomDetail;//症状详情
    private String zhengzhuangIds;//症状集合
    private String jibingIds;//疾病集合
    private String spIcon;//icon路径

    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public MiniSymptom() {
    }

    /**
     * 用业务对象HealthySymptomDTO初始化数据库对象HealthySymptom。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniSymptom(MiniSymptomDTO dto) {
        this.id = dto.getId();
        this.parentId = dto.getParentId();
        this.secondId = dto.getSecondId();
        this.hotFlag = dto.getHotFlag();
        this.symptomName = dto.getSymptomName();
        this.symptomIntro = dto.getSymptomIntro();
        this.symptomDetail = dto.getSymptomDetail();
        this.zhengzhuangIds = dto.getZhengzhuangIds();
        this.jibingIds = dto.getJibingIds();
        this.spIcon = dto.getSpIcon();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthySymptomDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniSymptomDTO dto) {
        dto.setId(this.id);
        dto.setParentId(this.parentId);
        dto.setSecondId(this.secondId);
        dto.setHotFlag(this.hotFlag);
        dto.setSymptomName(this.symptomName);
        dto.setSymptomIntro(this.symptomIntro);
        dto.setSymptomDetail(this.symptomDetail);
        dto.setZhengzhuangIds(this.zhengzhuangIds);
        dto.setJibingIds(this.jibingIds);
        dto.setSpIcon(this.spIcon);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
