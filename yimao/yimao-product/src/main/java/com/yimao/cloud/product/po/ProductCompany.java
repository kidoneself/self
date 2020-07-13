package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.ProductCompanyDTO;
import com.yimao.cloud.pojo.vo.product.ProductCompanyVO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 产品公司实体类
 *
 * @author Liu Yi
 * @date 2018/11/26.
 */
@Table(name = "product_company")
@Data
public class ProductCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;                //产品公司名称
    private String code;                //公司编号
    private String shortName;           //公司简称
    private String logo;                //公司logo
    private String profile;             //公司介绍

    private Boolean deleted;            //删除标识：0-未删除；1-已删除

    private String creator;             //创建人
    private Date createTime;            //创建时间
    private String updater;             //更新人
    private Date updateTime;            //更新时间

    public ProductCompany() {
    }

    /**
     * 用业务对象ProductCompanyDTO初始化数据库对象ProductCompany。
     *
     * @param dto 业务对象
     */
    public ProductCompany(ProductCompanyDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.code = dto.getCode();
        this.shortName = dto.getShortName();
        this.logo = dto.getLogo();
        this.profile = dto.getProfile();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ProductCompanyVO上。
     *
     * @param vo 业务对象
     */
    public void convert(ProductCompanyVO vo) {
        vo.setId(this.id);
        vo.setName(this.name);
        vo.setCode(this.code);
        vo.setShortName(this.shortName);
        vo.setLogo(this.logo);
        vo.setProfile(this.profile);
        vo.setDeleted(this.deleted);
        vo.setCreator(this.creator);
        vo.setCreateTime(this.createTime);
        vo.setUpdater(this.updater);
        vo.setUpdateTime(this.updateTime);
    }
}