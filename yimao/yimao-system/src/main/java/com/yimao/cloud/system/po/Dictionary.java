package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.DictionaryDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 字典表
 * 尽量把只有一级的放入字典表中维护
 *
 * @author liuhao@yimaokeji.com
 * 2017112017/11/16
 */
@Table(name = "system_dictionary")
@Data
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;         //字典value
    private String code;         //code值
    private String groupCode;    //分组
    private Integer pid;         //父级编号 默认为0
    private Integer sorts;
    private Boolean deleted;

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public Dictionary() {
    }

    /**
     * 用业务对象DictionaryDTO初始化数据库对象Dictionary。
     *
     * @param dto 业务对象
     */
    public Dictionary(DictionaryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.groupCode = dto.getGroupCode();
        this.pid = dto.getPid();
        this.sorts = dto.getSorts();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象DictionaryDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(DictionaryDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setCode(this.code);
        dto.setGroupCode(this.groupCode);
        dto.setPid(this.pid);
        dto.setSorts(this.sorts);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
