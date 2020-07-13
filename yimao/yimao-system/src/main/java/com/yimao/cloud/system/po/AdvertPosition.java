package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.AdvertPositionDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 广告位
 * created by liuhao@yimaokeji.com
 */
@Table(name = "t_advert_position")
@Data
public class AdvertPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;       //标题
    private String description; //描述
    private String typeCode;    //标识广告位置   首页轮播图：slideshow
    private Integer conditions; //状态
    private Integer sorts;      //排序
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public AdvertPosition() {
    }

    /**
     * 用业务对象AdvertPositionDTO初始化数据库对象AdvertPosition。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public AdvertPosition(AdvertPositionDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.typeCode = dto.getTypeCode();
        this.conditions = dto.getConditions();
        this.sorts = dto.getSorts();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AdvertPositionDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(AdvertPositionDTO dto) {
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setDescription(this.description);
        dto.setTypeCode(this.typeCode);
        dto.setConditions(this.conditions);
        dto.setSorts(this.sorts);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
