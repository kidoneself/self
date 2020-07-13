package com.yimao.cloud.pojo.dto.station;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Liu long jie
 * @date 2019/12/30.
 */
@Data
public class StationPermissionCacheDTO implements Serializable {

    private static final long serialVersionUID = -6299160703474077480L;

    private Integer id;
    private String url;
    private String method;
    private Integer type;

    private Integer menuId;

}
