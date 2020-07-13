package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.MaterialsDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：自有广告物料
 *
 * @Author Zhang Bo
 * @Date 2019/1/29 17:41
 * @Version 1.0
 */
@Table(name = "ad_materials")
@Data
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//物料ID
    private String name;//物料名称
    private Integer size;//物料文件大小（单位：KB）
    private Integer duration;//广告时长（单位：秒）
    private Integer screenLocation;//屏幕位置：1-大屏；2-小屏
    private Integer materielType;//物料类型：1-视频；2-图片;3-链接
    private String image;//关联图
    private Integer imageSize;//关联图大小（单位：KB）
    private String advertisers;//广告主名称
    private String url;//物料地址
    private String description;//物料说明
    private Integer payAudit;//支付审核(0-未审核，1-审核通过，2-审核不通过)
    private Integer contentAudit;//内容审核(0-未审核，1-审核通过，2-审核不通过)
    private Integer specificationAudit;//规格审核  (0-未审核，1-审核通过，2-审核不通过)
    private String payAuditReason;//支付审核不通过原因
    private String contentAuditReason;//内容审核不通过原因
    private String specificationAuditReason;//规格审核不通过原因
    private Boolean deleted;//删除状态：0-未删除；1-删除

    private String creator;//创建人
    private Date createTime;//创建时间
    private String updater;//更新人
    private Date updateTime;//更新时间


    public Materials() {
    }

    /**
     * 用业务对象MaterialsDTO初始化数据库对象Materials。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Materials(MaterialsDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.size = dto.getSize();
        this.duration = dto.getDuration();
        this.screenLocation = dto.getScreenLocation();
        this.materielType = dto.getMaterielType();
        this.image = dto.getImage();
        this.imageSize = dto.getImageSize();
        this.advertisers = dto.getAdvertisers();
        this.url = dto.getUrl();
        this.description = dto.getDescription();
        this.payAudit = dto.getPayAudit();
        this.contentAudit = dto.getContentAudit();
        this.specificationAudit = dto.getSpecificationAudit();
        this.payAuditReason = dto.getPayAuditReason();
        this.contentAuditReason = dto.getContentAuditReason();
        this.specificationAuditReason = dto.getSpecificationAuditReason();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MaterialsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MaterialsDTO dto) {
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSize(this.size);
        dto.setDuration(this.duration);
        dto.setScreenLocation(this.screenLocation);
        dto.setMaterielType(this.materielType);
        dto.setImage(this.image);
        dto.setImageSize(this.imageSize);
        dto.setAdvertisers(this.advertisers);
        dto.setUrl(this.url);
        dto.setDescription(this.description);
        dto.setPayAudit(this.payAudit);
        dto.setContentAudit(this.contentAudit);
        dto.setSpecificationAudit(this.specificationAudit);
        dto.setPayAuditReason(this.payAuditReason);
        dto.setContentAuditReason(this.contentAuditReason);
        dto.setSpecificationAuditReason(this.specificationAuditReason);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
