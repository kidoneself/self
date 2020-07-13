package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.IntegralConfigurationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/***
 * 功能描述:积分配置
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Table(name = "integral_configuration")
@Getter
@Setter
public class IntegralConfiguration{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //积分类型：1-点击广告 2-单日在线时长
    private Integer type;
    //最少在线时间 （单位小时）
    private Integer leastOnLineTime;
    //积分数量
    private Integer num;
    //规则id
    private Integer integralRuleId;


    public IntegralConfiguration() {
    }

    /**
     * 用业务对象IntegralConfigurationDTO初始化数据库对象IntegralConfiguration。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public IntegralConfiguration(IntegralConfigurationDTO dto) {
        this.id = dto.getId();
        this.type = dto.getType();
        this.leastOnLineTime = dto.getLeastOnLineTime();
        this.num = dto.getNum();
        this.integralRuleId = dto.getIntegralRuleId();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IntegralConfigurationDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(IntegralConfigurationDTO dto) {
        dto.setId(this.id);
        dto.setType(this.type);
        dto.setLeastOnLineTime(this.leastOnLineTime);
        dto.setNum(this.num);
        dto.setIntegralRuleId(this.integralRuleId);
    }
}