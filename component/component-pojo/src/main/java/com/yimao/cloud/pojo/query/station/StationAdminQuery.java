package com.yimao.cloud.pojo.query.station;


import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * 服务站用户查询类
 * @author yaoweijun
 *
 */
@Data
public class StationAdminQuery {
    private String roleName;
    private Integer roleId;
    private String realName;
    private String userName;
    private Boolean status;
    private Integer stationCompanyId;
    private String stationCompanyName;
    Set<Integer> stationList;
}
