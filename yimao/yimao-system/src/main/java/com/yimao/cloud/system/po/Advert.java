package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.AdvertDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 广告信息
 * created by liuhao@yimaokeji.com
 */
@Table(name = "t_advert")
@Data
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;  //名称
    private Integer apId;      //广告位编号
    private String title;   //标题
    private String content; //内容
    private String url;     //url
    private String adImg;   //图片
    private Integer conditions;//状态
    private Integer sorts;  //排序
    private Date createTime;//创建时间
    private String creator;
    private Date updateTime;//创建时间
    private String updater;

    public Advert() {
    }

    /**
     * 用业务对象AdvertDTO初始化数据库对象Advert。
     *
     * @param dto 业务对象
     */
    public Advert(AdvertDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.apId = dto.getApId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.url = dto.getUrl();
        this.adImg = dto.getAdImg();
        this.conditions = dto.getConditions();
        this.sorts = dto.getSorts();
        this.createTime = dto.getCreateTime();
        this.creator = dto.getCreator();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象AdvertDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(AdvertDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setApId(this.apId);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setUrl(this.url);
        dto.setAdImg(this.adImg);
        dto.setConditions(this.conditions);
        dto.setSorts(this.sorts);
        dto.setCreateTime(this.createTime);
        dto.setCreator(this.creator);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
