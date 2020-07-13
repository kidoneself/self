package com.yimao.cloud.product.po;

import com.yimao.cloud.pojo.dto.product.VirtualProductConfigDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：虚拟产品配置
 *
 * @Author Zhang Bo
 * @Date 2019/3/18
 */
@Table(name = "virtual_product_config")
@Getter
@Setter
public class VirtualProductConfig {

    @Id
    private Integer productId;          //产品ID
    private Date effectiveStartTime;    //商品使用有效期开始时间
    private Date effectiveEndTime;      //商品使用有效期结束时间
    private Integer refundType;         //退款设置：1-不支持退款；2-未过期前可退款；3-过期和未过期都可退款
    private Integer count;              //创建评估卡券的数量
    private Integer usageCount;         //使用次数

    /**
     * 生效类型：1-无限期；2-指定时间内可用；3-自定义时间段
     */
    private Integer effectiveType;
    /**
     * 有效期类型 0-无限期 3-年 2-月 1-日
     */
    private Integer usefulType;
    private Integer usefulNum;

    public VirtualProductConfig() {
    }

    /**
     * 用业务对象VirtualProductConfigDTO初始化数据库对象VirtualProductConfig。
     *
     * @param dto 业务对象
     */
    public VirtualProductConfig(VirtualProductConfigDTO dto) {
        this.productId = dto.getProductId();
        this.effectiveStartTime = dto.getEffectiveStartTime();
        this.effectiveEndTime = dto.getEffectiveEndTime();
        this.refundType = dto.getRefundType();
        this.count = dto.getCount();
        this.usageCount = dto.getUsageCount();
        this.effectiveType =dto.getEffectiveType();
        this.usefulType =dto.getUsefulType();
        this.usefulNum = dto.getUsefulNum();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象VirtualProductConfigDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(VirtualProductConfigDTO dto) {
        dto.setProductId(this.productId);
        dto.setEffectiveStartTime(this.effectiveStartTime);
        dto.setEffectiveEndTime(this.effectiveEndTime);
        dto.setRefundType(this.refundType);
        dto.setCount(this.count);
        dto.setUsageCount(this.usageCount);
        dto.setEffectiveType(this.effectiveType);
        dto.setUsefulType(this.usefulType);
        dto.setUsefulNum(this.usefulNum);

    }
}
