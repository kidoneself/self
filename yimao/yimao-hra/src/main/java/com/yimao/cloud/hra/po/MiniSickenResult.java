package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniSickenResultDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description: 患病结果 实体类
 * @author: yu chunlei
 * @create: 2018-04-28 15:33:42
 **/
@Table(name = "healthy_sicken_result")
@Data
public class MiniSickenResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer symptomId;
    private String sickenName; //疾病名称
    private String sickenNameExplain;//疾病介绍
    private String symptom_detail;//症状
    private String pathogeny;//病因
    private String chineseTreatment;//中医治疗
    private String prevention;//预防
    private String nursing;//护理
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public MiniSickenResult() {
    }

    /**
     * 用业务对象HealthySickenResultDTO初始化数据库对象HealthySickenResult。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniSickenResult(MiniSickenResultDTO dto) {
        this.id = dto.getId();
        this.symptomId = dto.getSymptomId();
        this.sickenName = dto.getSickenName();
        this.sickenNameExplain = dto.getSickenNameExplain();
        this.symptom_detail = dto.getSymptom_detail();
        this.pathogeny = dto.getPathogeny();
        this.chineseTreatment = dto.getChineseTreatment();
        this.prevention = dto.getPrevention();
        this.nursing = dto.getNursing();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthySickenResultDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniSickenResultDTO dto) {
        dto.setId(this.id);
        dto.setSymptomId(this.symptomId);
        dto.setSickenName(this.sickenName);
        dto.setSickenNameExplain(this.sickenNameExplain);
        dto.setSymptom_detail(this.symptom_detail);
        dto.setPathogeny(this.pathogeny);
        dto.setChineseTreatment(this.chineseTreatment);
        dto.setPrevention(this.prevention);
        dto.setNursing(this.nursing);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
