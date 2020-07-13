package com.yimao.cloud.pojo.dto.user;

import lombok.Data;
import java.io.Serializable;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2019-01-11 14:04:59
 **/
@Data
public class UserCountDTO implements Serializable {

    private static final long serialVersionUID = -5788689512335061857L;
    private Integer totalNum;//总用户数量
    private Integer todayAddNum;//今日新增数量
    private Integer monthAddNum;//本月新增数量
    private Integer yearAddNum;//本年新增数量
}
