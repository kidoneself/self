package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.ReasonDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "reason")
@Getter
@Setter
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;        //原因ID
    private Integer type;      //原因类型：1、中止工单；2、退单；3、删除工单；
    private String reason;     //原因
    private Integer reasonNum; //原因序号
    private Date createTime;   //创建时间

    public Reason() {
    }

    /**
     * 用业务对象ReasonDTO初始化数据库对象Reason。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Reason(ReasonDTO dto) {
        this.id = dto.getId();
        this.type = dto.getType();
        this.reason = dto.getReason();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象ReasonDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(ReasonDTO dto) {
        dto.setId(this.id);
        dto.setType(this.type);
        dto.setReason(this.reason);
        dto.setCreateTime(this.createTime);
    }
}
