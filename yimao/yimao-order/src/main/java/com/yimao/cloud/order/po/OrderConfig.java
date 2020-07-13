package com.yimao.cloud.order.po;

import com.yimao.cloud.pojo.dto.order.OrderConfigDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @description: 订单配置
 * @author: yu chunlei
 * @create: 2019-08-08 15:52:50
 **/
@Table(name = "order_config")
@Data
public class OrderConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderTimeOut;//订单超时时间
    private Integer returnDays;//退货天数
    private Integer deliveryDays;//发货天数
    private Date createTime;
    private Date updateTime;


    public OrderConfig() {
    }

    /**
     * 用业务对象OrderConfigDTO初始化数据库对象OrderConfig。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public OrderConfig(OrderConfigDTO dto) {
        this.id = dto.getId();
        this.orderTimeOut = dto.getOrderTimeOut();
        this.returnDays = dto.getReturnDays();
        this.deliveryDays = dto.getDeliveryDays();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象OrderConfigDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(OrderConfigDTO dto) {
        dto.setId(this.id);
        dto.setOrderTimeOut(this.orderTimeOut);
        dto.setReturnDays(this.returnDays);
        dto.setDeliveryDays(this.deliveryDays);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
