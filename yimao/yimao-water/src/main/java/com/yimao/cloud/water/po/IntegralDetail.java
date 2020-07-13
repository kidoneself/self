package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.IntegralDetailDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/***
 * 功能描述:积分详情
 *
 * @auther: liu yi
 * @date: 2019/6/3 10:50
 */
@Table(name = "integral_detail")
@Getter
@Setter
public class IntegralDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //积分规则id
    private Integer integralRuleId;
    //规则名称
    private String integralRuleName;
    //SN码
    private String sn;
    //型号
    private String model;
    //省份
    private String province;
    //城市
    private String city;
    //县市
    private String region;
    //地址
    private String address;
    //积分类型：1-点击广告 2-单日在线时长
    private Integer type;
    //积分数量
    private Integer num;
    //描述
    private String description;
    private Date createTime;

    public IntegralDetail() {
    }

    /**
     * 用业务对象IntegralDetailDTO初始化数据库对象IntegralDetail。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public IntegralDetail(IntegralDetailDTO dto) {
        this.id = dto.getId();
        this.integralRuleId = dto.getIntegralRuleId();
        this.integralRuleName = dto.getIntegralRuleName();
        this.sn = dto.getSn();
        this.model = dto.getModel();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.type = dto.getType();
        this.num = dto.getNum();
        this.description = dto.getDescription();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象IntegralDetailDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(IntegralDetailDTO dto) {
        dto.setId(this.id);
        dto.setIntegralRuleId(this.integralRuleId);
        dto.setIntegralRuleName(this.integralRuleName);
        dto.setSn(this.sn);
        dto.setModel(this.model);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setType(this.type);
        dto.setNum(this.num);
        dto.setDescription(this.description);
        dto.setCreateTime(this.createTime);
    }
}