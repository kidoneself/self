package com.yimao.cloud.pojo.query.system;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志列表筛选条件
 *
 */
@Data
public class StoreHouseOperationLogQuery implements Serializable {

    private static final long serialVersionUID = 3233259578486741245L;

    private Integer operationType; //操作类型:1-后台修改库存
    private Integer objectType; //操作主体:1-总仓 2-服务站门店 3-服务站公司
    private Integer areaId; //所在地区域id
    private Date startTime; //开始时间
    private Date endTime; //结束时间

}
