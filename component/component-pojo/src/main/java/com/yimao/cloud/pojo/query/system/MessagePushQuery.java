package com.yimao.cloud.pojo.query.system;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @description   消息查询
 * @author Liu Yi
 * @date 2019/11/26 16:18
 */
@Getter
@Setter
public class MessagePushQuery implements Serializable {
    private static final long serialVersionUID = 2430339047489701758L;
    private String userName;
    private Integer pushType;
    private String content;
    private Integer devices;
    private Date startTime;
    private Date endTime;

}
