package com.yimao.cloud.pojo.dto.cat;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-19 14:25:32
 **/
@Data
public class ActivityHistoryDTO implements Serializable {
    private static final long serialVersionUID = 2393426202503521121L;
    private Integer id;
    private Integer userId;//用户ID
    private Integer ruleId;//规则ID
    private String ruleName;//规则名称
    private Integer tradeNum;//成交数量
    private Integer spellNum;//拼团人数
    private Integer cycleNum;//活动轮数
    private String activityImg;//图片
    private Boolean hasCycle;//是否循环开启活动
    private Boolean hasOpen;//是否一键开启
    private Integer ruleState;//状态：0：已完成 1：已下架 2：已取消
    private Date activityBeginTime;     //活动开始时间
    private Date activityEndTime;       //活动结束时间
    private Date updateTime;
    private String updater;
}
