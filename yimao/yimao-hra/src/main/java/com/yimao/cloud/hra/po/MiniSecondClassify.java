package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.MiniSecondClassifyDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Description:
 * @author ycl
*/
@Table(name = "healthy_second_classify")
@Data
public class MiniSecondClassify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * @Description: 父分类编号
     * @author ycl
    */
    private Integer pid;
    /**
     * @Description: 主分类ID
     * @author ycl
    */
    private Integer classifyId;
    /**
     * @Description: 身体部位名称
     * @author ycl
    */
    private String secondName;
    private Date createTime;
    private String creator;
    private Date updateTime;
    private String updater;

    public MiniSecondClassify() {
    }

    /**
     * 用业务对象HealthySecondClassifyDTO初始化数据库对象HealthySecondClassify。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MiniSecondClassify(MiniSecondClassifyDTO dto) {
        this.id = dto.getId();
        this.pid = dto.getPid();
        this.classifyId = dto.getClassifyId();
        this.secondName = dto.getSecondName();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HealthySecondClassifyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MiniSecondClassifyDTO dto) {
        dto.setId(this.id);
        dto.setPid(this.pid);
        dto.setClassifyId(this.classifyId);
        dto.setSecondName(this.secondName);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
