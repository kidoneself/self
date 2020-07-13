package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraReportOtherDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description: 他人体检报告
 * @author: yu chunlei
 * @create: 2018-05-10 15:11:30
 **/
@Table(name = "hra_report_other")
@Data
public class HraReportOther{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer shareUserId;//被分享用户ID
    private String ticketNo;
    private String mobile;
    private Date createTime;

    public HraReportOther() {
    }

    /**
     * 用业务对象HraReportOtherDTO初始化数据库对象HraReportOther。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraReportOther(HraReportOtherDTO dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.shareUserId = dto.getShareUserId();
        this.ticketNo = dto.getTicketNo();
        this.mobile = dto.getMobile();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraReportOtherDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraReportOtherDTO dto) {
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setShareUserId(this.shareUserId);
        dto.setTicketNo(this.ticketNo);
        dto.setMobile(this.mobile);
        dto.setCreateTime(this.createTime);
    }
}
