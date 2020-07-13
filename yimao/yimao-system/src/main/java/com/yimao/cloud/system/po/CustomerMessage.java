package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.CustomerMessageDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户留言
 *
 * @author liuhao@yimaokeji.com
 * 2018052018/5/16
 */
@Table(name = "t_customer_message")
@Data
public class CustomerMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String customerName;//客户姓名
    private String mobile;
    private String province;
    private String city;
    private String region;
    private Integer joinType;//结盟类型
    private String content;
    private String remark;
    private String openId;
    private Date createTime;
    private Integer terminal;


    public CustomerMessage() {
    }

    /**
     * 用业务对象CustomerMessageDTO初始化数据库对象CustomerMessage。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public CustomerMessage(CustomerMessageDTO dto) {
        this.id = dto.getId();
        this.customerName = dto.getCustomerName();
        this.mobile = dto.getMobile();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.joinType = dto.getJoinType();
        this.content = dto.getContent();
        this.remark = dto.getRemark();
        this.openId = dto.getOpenId();
        this.createTime = dto.getCreateTime();
        this.terminal = dto.getTerminal();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CustomerMessageDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CustomerMessageDTO dto) {
        dto.setId(this.id);
        dto.setCustomerName(this.customerName);
        dto.setMobile(this.mobile);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setJoinType(this.joinType);
        dto.setContent(this.content);
        dto.setRemark(this.remark);
        dto.setOpenId(this.openId);
        dto.setCreateTime(this.createTime);
        dto.setTerminal(this.terminal);
    }
}
