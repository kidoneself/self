package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.MessageRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 消息记录
 */
@Setter
@Getter
@Table(name = "t_message_record")
public class MessageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phone;   //手机号
    private String openid;  //接收消息的openid
    private Integer userId;
    private Long contentId;
    private Integer type;   //类型 1-手机短信 2-推送消息
    private Long orderId;
    private Date createTime;

    public MessageRecord() {
    }

    public MessageRecord(MessageRecordDTO dto) {
        this.id = dto.getId();
        this.phone = dto.getPhone();
        this.openid = dto.getOpenid();
        this.userId = dto.getUserId();
        this.contentId = dto.getContentId();
        this.type = dto.getType();
        this.orderId = dto.getOrderId();
        this.createTime = dto.getCreateTime();
    }

    public void convert(MessageRecordDTO dto) {
        dto.setId(this.id);
        dto.setPhone(this.phone);
        dto.setOpenid(this.openid);
        dto.setUserId(this.userId);
        dto.setContentId(this.contentId);
        dto.setType(this.type);
        dto.setOrderId(this.orderId);
        dto.setCreateTime(this.createTime);
    }
}
