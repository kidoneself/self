package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.BusinessProfileDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 经营概况
 *
 * @author hhf
 * @date 2019/3/25
 */
@Table(name = "t_business_profile")
@Data
public class BusinessProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 有效销售总额度
     */
    private BigDecimal saleTotal;

    /**
     * 有效订单总数
     */
    private Integer orderTotal;

    /**
     * 经销商总数
     */
    private Integer distributorTotal;

    /**
     * 用户总数
     */
    private Integer userTotal;

    /**
     * 用户访问次数
     */
    private Integer visitTotal;

    /**
     * 用户下单次数
     */
    private Integer buyTotal;

    /**
     * 成交笔数
     */
    private Integer bargainTotal;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 昨日有效销售额
     */
    private BigDecimal yestSaleTotal;

    /**
     * 昨日订单数
     */
    private Integer yestOrderTotal;

    /**
     * 昨日新增经销商数
     */
    private Integer yestDistributorTotal;

    /**
     * 昨日用户数
     */
    private Integer yestUserTotal;

    public BusinessProfile() {
    }

    /**
     * 用业务对象BusinessProfileDTO初始化数据库对象BusinessProfile。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public BusinessProfile(BusinessProfileDTO dto) {
        this.id = dto.getId();
        this.saleTotal = dto.getSaleTotal();
        this.orderTotal = dto.getOrderTotal();
        this.distributorTotal = dto.getDistributorTotal();
        this.userTotal = dto.getUserTotal();
        this.visitTotal = dto.getVisitTotal();
        this.buyTotal = dto.getBuyTotal();
        this.bargainTotal = dto.getBargainTotal();
        this.createTime = dto.getCreateTime();
        this.yestSaleTotal = dto.getYestSaleTotal();
        this.yestOrderTotal = dto.getYestOrderTotal();
        this.yestDistributorTotal = dto.getYestDistributorTotal();
        this.yestUserTotal = dto.getYestUserTotal();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象BusinessProfileDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(BusinessProfileDTO dto) {
        dto.setId(this.id);
        dto.setSaleTotal(this.saleTotal);
        dto.setOrderTotal(this.orderTotal);
        dto.setDistributorTotal(this.distributorTotal);
        dto.setUserTotal(this.userTotal);
        dto.setVisitTotal(this.visitTotal);
        dto.setBuyTotal(this.buyTotal);
        dto.setBargainTotal(this.bargainTotal);
        dto.setCreateTime(this.createTime);
        dto.setYestSaleTotal(this.yestSaleTotal);
        dto.setYestOrderTotal(this.yestOrderTotal);
        dto.setYestDistributorTotal(this.yestDistributorTotal);
        dto.setYestUserTotal(this.yestUserTotal);
    }
}
