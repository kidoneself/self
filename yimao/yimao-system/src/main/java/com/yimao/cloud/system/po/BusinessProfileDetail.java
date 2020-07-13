package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.BusinessProfileDetailDTO;
import lombok.Data;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 经营概况明细
 *
 * @author hhf
 * @date 2019/3/26
 */
@Table(name = "t_business_profile_detail")
@Data
public class BusinessProfileDetail {

    private Integer id;

    /**
     * 经营概况id
     */
    private Integer businessProfileId;

    /**
     * 服务ID
     */
    private Integer serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务销售额
     */
    private BigDecimal salesTotal;

    /**
     * 服务销量
     */
    private Integer serviceCount;

    /**
     * 创建时间
     */
    private Date createTime;


    public BusinessProfileDetail() {
    }

    /**
     * 用业务对象BusinessProfileDetailDTO初始化数据库对象BusinessProfileDetail。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public BusinessProfileDetail(BusinessProfileDetailDTO dto) {
        this.id = dto.getId();
        this.businessProfileId = dto.getBusinessProfileId();
        this.serviceId = dto.getServiceId();
        this.serviceName = dto.getServiceName();
        this.salesTotal = dto.getSalesTotal();
        this.serviceCount = dto.getServiceCount();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象BusinessProfileDetailDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(BusinessProfileDetailDTO dto) {
        dto.setId(this.id);
        dto.setBusinessProfileId(this.businessProfileId);
        dto.setServiceId(this.serviceId);
        dto.setServiceName(this.serviceName);
        dto.setSalesTotal(this.salesTotal);
        dto.setServiceCount(this.serviceCount);
        dto.setCreateTime(this.createTime);
    }
}
