package com.yimao.cloud.out.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageVO {

    private String id;
    private String uuid;
    private String title;
    private String content;
    private String workorderId;
    private String workorderType;
    private String workorderEntityId;
    private String createTime;
   // private Integer isRead;
    private String readStatus;
    private String orderType;

}
