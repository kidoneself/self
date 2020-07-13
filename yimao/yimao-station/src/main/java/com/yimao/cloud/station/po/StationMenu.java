package com.yimao.cloud.station.po;

import java.util.Date;

import com.yimao.cloud.pojo.dto.station.StationMenuDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "station_menu")
@Getter
@Setter
public class StationMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private String icon;

    private String url;

    private Integer pid;

    private Integer level;

    private Integer sorts;

    private Integer creator;

    private Date createTime;

    private Integer updater;

    private Date updateTime;


    public StationMenu() {
    }

    /**
     * 用业务对象StationMenuDTO初始化数据库对象StationMenu。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public StationMenu(StationMenuDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.url = dto.getUrl();
        this.pid = dto.getPid();
        this.level = dto.getLevel();
        this.sorts = dto.getSorts();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
        this.icon = dto.getIcon();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象StationMenuDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(StationMenuDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setUrl(this.url);
        dto.setPid(this.pid);
        dto.setLevel(this.level);
        dto.setSorts(this.sorts);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
        dto.setIcon(this.icon);
    }
}