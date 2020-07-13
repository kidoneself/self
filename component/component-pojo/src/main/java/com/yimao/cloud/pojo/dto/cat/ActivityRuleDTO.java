package com.yimao.cloud.pojo.dto.cat;

import com.yimao.cloud.pojo.dto.product.ProductDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yu chunlei
 * @create: 2018-11-17 09:44:35
 **/
@Data
public class ActivityRuleDTO implements Serializable {

    private static final long serialVersionUID = 2758793901409182902L;
    private Integer id;
    private String ruleName;            //规则名称
    private Integer ruleType;           //规则类型  1-阶段拼团  2-限时免单  3-砍价购
    private Integer applicationType;    //支持的端  1-公众号 2-小程序
    private String applyUserType;       //支持的用户类型，多个以逗号隔开
    private String productIds;             //产品ids集合，多个以逗号隔开
    private BigDecimal basePrice;       //保底价
    private Integer num;                //拼团人数
    private Date activityBeginTime;     //活动开始时间
    private Date activityEndTime;       //活动结束时间
    private Integer activityTime;       //活动时间
    private Boolean hasCycle;           //是否循环开启活动
    private Boolean hasOpen;           //是否一键开启
    private String activityName;        //活动名称
    private Integer startStopTime;       //起止时间
    private String activityTitle;       //活动标题
    private String activityImg;         //活动图
    private Integer inventoryNum;       //库存
    private Integer ruleStatus;         //规则状态 1-已上架 2-待发布
    private Integer canCrowdSetting;    //限制能拼砍的设置  0-不限制  1、老用户(有e家号的用户)  2、新用户(没有e家号的用户)
    private String ruleContext;//规则内容
    private List<ProductDTO> products;     //产品对象集合
    private List<ActivityRuleDetailDTO> activityRuleDetails;//规则详情列表

}
