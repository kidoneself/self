package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.PlatformDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：广告平台
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "ad_platform")
@Data
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//自有广告位ID
    private String name;//平台名称
    private String appId;//媒体ID，标识资源方，平台生成
    private String apiVersion;//API版本，按照当前接入所参照的API文档版本赋值，影响所有后续逻辑，填写错误会导致拒绝请求。
    private String url;//第三方广告请求地址
    private String adslotIds;//广告位ID，多个以英文逗号进行拼接
    private Integer sort;//排序
    private Boolean deleted;

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public Platform() {
    }

    /**
     * 用业务对象PlatformDTO初始化数据库对象Platform。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Platform(PlatformDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.appId = dto.getAppId();
        this.apiVersion = dto.getApiVersion();
        this.url = dto.getUrl();
        this.adslotIds = dto.getAdslotIds();
        this.sort = dto.getSort();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象PlatformDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(PlatformDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setAppId(this.appId);
        dto.setApiVersion(this.apiVersion);
        dto.setUrl(this.url);
        dto.setAdslotIds(this.adslotIds);
        dto.setSort(this.sort);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
