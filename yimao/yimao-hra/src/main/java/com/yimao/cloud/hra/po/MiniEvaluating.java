package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniEvaluatingDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 评测 实体类
 */
@Table(name = "healthy_evaluating")
@Data
public class MiniEvaluating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer classifyId;//父类别ID
    private Integer secondId;//父类别ID
    private String subImg;//选项图片
    private String startImg;//开始页图片
    private Integer joinNumber;//参加人数
    private String evaluatingTitle;//标题
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public MiniEvaluating() {
    }

    /**
     * 用业务对象HealthyEvaluatingDTO初始化数据库对象HealthyEvaluating。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniEvaluating(MiniEvaluatingDTO dto) {
        this.id = dto.getId();
        this.classifyId = dto.getClassifyId();
        this.secondId = dto.getSecondId();
        this.subImg = dto.getSubImg();
        this.startImg = dto.getStartImg();
        this.joinNumber = dto.getJoinNumber();
        this.evaluatingTitle = dto.getEvaluatingTitle();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthyEvaluatingDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniEvaluatingDTO dto) {
        dto.setId(this.id);
        dto.setClassifyId(this.classifyId);
        dto.setSecondId(this.secondId);
        dto.setSubImg(this.subImg);
        dto.setStartImg(this.startImg);
        dto.setJoinNumber(this.joinNumber);
        dto.setEvaluatingTitle(this.evaluatingTitle);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
