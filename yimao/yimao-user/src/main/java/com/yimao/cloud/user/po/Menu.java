package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.MenuDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zhang Bo
 * @date 2018/11/2.
 */
@Table(name = "system_menu")
@Getter
@Setter
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String url;
    private String icon;//菜单图标
    private Integer pid;//父级菜单id
    private Integer level;//菜单层级
    private Integer sorts;//排序
    private Integer sysType;//所属系统：2-翼猫业务管理系统；3-净水设备互动广告系统；

    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;

    public Menu() {
    }

    /**
     * 用业务对象MenuDTO初始化数据库对象Menu。
     *
     * @param dto 业务对象
     */
    public Menu(MenuDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.url = dto.getUrl();
        this.icon = dto.getIcon();
        this.pid = dto.getPid();
        this.level = dto.getLevel();
        this.sorts = dto.getSorts();
        this.sysType = dto.getSysType();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MenuDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(MenuDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setUrl(this.url);
        dto.setIcon(this.icon);
        dto.setPid(this.pid);
        dto.setLevel(this.level);
        dto.setSorts(this.sorts);
        dto.setSysType(this.sysType);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
