package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniSicknessDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description: 疾病结果实体类
 * @author: yu chunlei
 * @create: 2018-05-22 13:38:53
 **/
@Table(name = "healthy_sickness")
@Data
public class MiniSickness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer secondId;
    private String sickName;
    private String sickIntro;//介绍
    private String sickSymptom;//症状
    private String sickReason;//原因
    private String chineseTreat;//中医治疗
    private String sickPrevent;//护理
    private String sickNurse;//预防
    private Date createTime;//创建时间
    private String creator;

    public MiniSickness() {
    }

    /**
     * 用业务对象HealthySicknessDTO初始化数据库对象HealthySickness。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniSickness(MiniSicknessDTO dto) {
        this.id = dto.getId();
        this.secondId = dto.getSecondId();
        this.sickName = dto.getSickName();
        this.sickIntro = dto.getSickIntro();
        this.sickSymptom = dto.getSickSymptom();
        this.sickReason = dto.getSickReason();
        this.chineseTreat = dto.getChineseTreat();
        this.sickPrevent = dto.getSickPrevent();
        this.sickNurse = dto.getSickNurse();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthySicknessDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniSicknessDTO dto) {
        dto.setId(this.id);
        dto.setSecondId(this.secondId);
        dto.setSickName(this.sickName);
        dto.setSickIntro(this.sickIntro);
        dto.setSickSymptom(this.sickSymptom);
        dto.setSickReason(this.sickReason);
        dto.setChineseTreat(this.chineseTreat);
        dto.setSickPrevent(this.sickPrevent);
        dto.setSickNurse(this.sickNurse);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
    }
}
