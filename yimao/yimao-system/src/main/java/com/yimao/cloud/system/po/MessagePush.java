package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.MessagePushDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "message_push")
@Getter
@Setter
public class MessagePush {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sncode;
    private String deviceId;
    private String workorderId;
    private Integer workorderType;
    private Integer pushType;
    private Integer filterType; //过滤类型：1-余额不足类型推送 2-制水故障类型推送 3-TDS异常故障类型推送 4-PP棉滤芯过期故障类型推送  5-CTO棉滤芯过期故障类型推送 6- UDF棉滤芯过期故障类型推送 7-T33棉滤芯过期故障类型推送
    private String title;
    private String content;
    private Integer clickNotice;
    private String address;
    private Integer devices;
    private Date createTime;
    private Integer receiverId;
    private String receiver;
    private String sender;
    private Integer isDelete;
    private Integer readStatus;
    private Integer app;

    public MessagePush() {
    }

    /**
     * 用业务对象MessagePushDTO初始化数据库对象MessagePush。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public MessagePush(MessagePushDTO dto) {
        this.id = dto.getId();
        this.sncode = dto.getSncode();
        this.deviceId = dto.getDeviceId();
        this.workorderId = dto.getWorkorderId();
        this.workorderType = dto.getWorkorderType();
        this.pushType = dto.getPushType();
        this.filterType = dto.getFilterType();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.clickNotice = dto.getClickNotice();
        this.address = dto.getAddress();
        this.devices = dto.getDevices();
        this.createTime = dto.getCreateTime();
        this.receiverId = dto.getReceiverId();
        this.receiver = dto.getReceiver();
        this.sender = dto.getSender();
        this.isDelete = dto.getIsDelete();
        this.readStatus = dto.getReadStatus();
        this.app = dto.getApp();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象MessagePushDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(MessagePushDTO dto) {
        dto.setId(this.id);
        dto.setSncode(this.sncode);
        dto.setDeviceId(this.deviceId);
        dto.setWorkorderId(this.workorderId);
        dto.setWorkorderType(this.workorderType);
        dto.setPushType(this.pushType);
        dto.setFilterType(this.filterType);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setClickNotice(this.clickNotice);
        dto.setAddress(this.address);
        dto.setDevices(this.devices);
        dto.setCreateTime(this.createTime);
        dto.setReceiverId(this.receiverId);
        dto.setReceiver(this.receiver);
        dto.setSender(this.sender);
        dto.setIsDelete(this.isDelete);
        dto.setReadStatus(this.readStatus);
        dto.setApp(this.app);
    }
}
