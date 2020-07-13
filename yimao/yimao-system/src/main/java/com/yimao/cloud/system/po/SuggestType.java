package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.SuggestTypeDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 建议类型
 */
@Table(name = "suggest_type")
@Getter
@Setter
public class SuggestType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //分类名称
    private String name;
    //排序
    private Integer sort;
    //展示端(1-净水设备 2-健康e家公众号 3-翼猫APP 4-健康自测小程序 5-站务系统 10-管理后台 ) ps：目前只针对站务系统
    private Integer terminal;
    //创建人
    private String creator;
    //创建时间
    private Date createTime;
    //修改人
    private String updater;
    //修改时间
    private Date updateTime;


    public SuggestType() {
    }

    /**
     * 用业务对象SuggestTypeDTO初始化数据库对象SuggestType。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public SuggestType(SuggestTypeDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.sort = dto.getSort();
        this.terminal = dto.getTerminal();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SuggestTypeDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(SuggestTypeDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSort(this.sort);
        dto.setTerminal(this.terminal);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
