package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniClassifyDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 主分类 实体
 */
@Table(name = "healthy_classify")
@Data
public class MiniClassify{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String classifyName;//分类名称
    private String classifySign;//标识：1：健康评测 2：症状自查
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;


    public MiniClassify() {
    }

    /**
     * 用业务对象MiniClassifyDTO初始化数据库对象MiniClassify。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniClassify(MiniClassifyDTO dto) {
        this.id = dto.getId();
        this.classifyName = dto.getClassifyName();
        this.classifySign = dto.getClassifySign();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MiniClassifyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniClassifyDTO dto) {
        dto.setId(this.id);
        dto.setClassifyName(this.classifyName);
        dto.setClassifySign(this.classifySign);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
