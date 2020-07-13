package com.yimao.cloud.pojo.dto.order;

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
@Table(name = "t_dealer_sales_like")
public class DealerSalesLikeDTO {
    //点赞的经销商ID
    private Integer distributorId;
    //点赞的服务站统计数据的ID
    private Integer stationSalesId;
    //时间
    private Date createTime;

}
