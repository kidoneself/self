package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.CustomerAssistantDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客服问答
 *
 * @author liuhao@yimaokeji.com
 * 2018052018/5/14
 */
@Table(name = "t_customer_assistant")
@Data
public class CustomerAssistant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer typeCode;//类型
    private String questions;//问题
    private String answers;//回答
    private Integer deleteFlag;
    private Integer sorts;
    private Date createTime;
    private Integer recommend;//是否推荐  1-是  0-否
    private Integer publish;//是否发布：1已经发布 0未发布
    private Integer terminal; //端：1-服务站  2-经销商app 3-健康e家
    private Date updateTime;
    private String attachment;//附件

    public CustomerAssistant() {
    }

    /**
     * 用业务对象CustomerAssistantDTO初始化数据库对象CustomerAssistant。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public CustomerAssistant(CustomerAssistantDTO dto) {
        this.id = dto.getId();
        this.typeCode = dto.getTypeCode();
        this.questions = dto.getQuestions();
        this.answers = dto.getAnswers();
        this.deleteFlag = dto.getDeleteFlag();
        this.sorts = dto.getSorts();
        this.createTime = dto.getCreateTime();
        this.recommend = dto.getRecommend();
        this.publish = dto.getPublish();
        this.terminal = dto.getTerminal();
        this.updateTime = dto.getUpdateTime();
        this.attachment=dto.getAttachment();
    }
    

    /**
     * 将数据库实体对象信息拷贝到业务对象CustomerAssistantDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CustomerAssistantDTO dto) {
        dto.setId(this.id);
        dto.setTypeCode(this.typeCode);
        dto.setQuestions(this.questions);
        dto.setAnswers(this.answers);
        dto.setDeleteFlag(this.deleteFlag);
        dto.setSorts(this.sorts);
        dto.setCreateTime(this.createTime);
        dto.setRecommend(this.recommend);
        dto.setPublish(this.publish);
        dto.setTerminal(this.terminal);
        dto.setUpdateTime(this.updateTime);
        dto.setAttachment(this.attachment);
    }
}
