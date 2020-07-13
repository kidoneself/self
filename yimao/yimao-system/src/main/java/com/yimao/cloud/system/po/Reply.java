package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.ReplyDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 站务系统 回复用户的建议反馈
 *
 */
@Table(name = "reply")
@Getter
@Setter
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //意见表主键id
    private Integer suggestId;
    //回复内容
    private String replyContent;
    //附件
    private String accessory;
    //回复人
    private String replier;
    //回复时间
    private Date time;

    public Reply() {
    }

    /**
     * 用业务对象ReplyDTO初始化数据库对象Reply。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Reply(ReplyDTO dto) {
        this.id = dto.getId();
        this.suggestId = dto.getSuggestId();
        this.replyContent = dto.getReplyContent();
        this.accessory = dto.getAccessory();
        this.replier = dto.getReplier();
        this.time = dto.getTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ReplyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ReplyDTO dto) {
        dto.setId(this.id);
        dto.setSuggestId(this.suggestId);
        dto.setReplyContent(this.replyContent);
        dto.setAccessory(this.accessory);
        dto.setReplier(this.replier);
        dto.setTime(this.time);
    }
}
