package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.MessageTemplateDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/***
 * 功能描述:消息模版
 *
 * @auther: liu yi
 * @date: 2019/4/30 10:33
 */
@Table(name = "message_template")
@Data
public class MessageTemplate{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer type;
    private Integer mechanism;
    private Integer pushObject;
    private Integer pushMode;
    private String template;
    private Date createTime;
    private Date updateTime;

    public MessageTemplate() {
    }

    /**
     * 用业务对象MessageTemplateDTO初始化数据库对象MessageTemplate。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MessageTemplate(MessageTemplateDTO dto) {
        this.id = dto.getId();
        this.type = dto.getType();
        this.mechanism = dto.getMechanism();
        this.pushObject = dto.getPushObject();
        this.pushMode = dto.getPushMode();
        this.template = dto.getTemplate();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MessageTemplateDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MessageTemplateDTO dto) {
        dto.setId(this.id);
        dto.setType(this.type);
        dto.setMechanism(this.mechanism);
        dto.setPushObject(this.pushObject);
        dto.setPushMode(this.pushMode);
        dto.setTemplate(this.template);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
