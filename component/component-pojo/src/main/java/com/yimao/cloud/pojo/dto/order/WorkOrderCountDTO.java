package com.yimao.cloud.pojo.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhilin.he
 * @description 工单概况和趋势统计
 * @date 2019/4/28 16:02
 **/
@Getter
@Setter
public class WorkOrderCountDTO {

    private Integer status;            //状态
    private String statusText;         //状态文本
    private Integer count;             //数量
    private String createTime;         //创建时间
}
