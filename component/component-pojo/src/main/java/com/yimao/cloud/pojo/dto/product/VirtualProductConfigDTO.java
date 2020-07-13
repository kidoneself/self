package com.yimao.cloud.pojo.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：虚拟产品配置
 *
 * @Author Zhang Bo
 * @Date 2019/3/18
 */
@Getter
@Setter
public class VirtualProductConfigDTO implements Serializable {

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
}
