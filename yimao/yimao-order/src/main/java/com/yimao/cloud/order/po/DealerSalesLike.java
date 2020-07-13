package com.yimao.cloud.order.po;

import java.util.Date;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @description: 经销商业绩数据点赞
 * @author: liulin
 * @create: 2019-02-22 16:21
 **/
@Data
@Table(name = "report_dealer_sales_like")
public class DealerSalesLike {
    //点赞的经销商ID
    private Integer distributorId;
    //点赞的服务站统计数据的ID
    private Integer stationSalesId;
    //时间
    private Date createTime;

}
