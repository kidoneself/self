package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.YMAPPPushDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Lizhqiang
 * @date 2019-08-05
 */
@Data
@Table(name = "ym_app_push")
public class YMAPPPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         //id
    private String title;       //标题
    private String content;     //内容
    private Integer account;    //推送id
    private Date pushTime;      //推送时间
    private Boolean status;     //推送状态 0-失败，1-成功
    private Boolean hasRead;       //0-未读，1-已读
    private Boolean deleted;    //0-删除，1-未删除


    public YMAPPPush() {
    }

    /**
     * 用业务对象YMAPPPushDTO初始化数据库对象YMAPPPush。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public YMAPPPush(YMAPPPushDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.account = dto.getAccount();
        this.pushTime = dto.getPushTime();
        this.status = dto.getStatus();
        this.hasRead = dto.getHasRead();
        this.deleted = dto.getDeleted();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象YMAPPPushDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(YMAPPPushDTO dto) {
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setContent(this.content);
        dto.setAccount(this.account);
        dto.setPushTime(this.pushTime);
        dto.setStatus(this.status);
        dto.setHasRead(this.hasRead);
        dto.setDeleted(this.deleted);
    }
}
