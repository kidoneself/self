package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.FilterMarksDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：滤芯初始工作标记
 *
 * @Author Zhang Bo
 * @Date 2019/5/13
 */
@Table(name = "filter_marks")
@Getter
@Setter
public class FilterMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sn;
    private Integer deviceId;
    private Integer pp;
    private Integer cto;
    private Integer udf;
    private Integer three;
    private Integer ro;
    private Date createTime;

    public FilterMarks() {
    }

    /**
     * 用业务对象FilterMarksDTO初始化数据库对象FilterMarks。
     *
     * @param dto 业务对象
     */
    public FilterMarks(FilterMarksDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.deviceId = dto.getDeviceId();
        this.pp = dto.getPp();
        this.cto = dto.getCto();
        this.udf = dto.getUdf();
        this.three = dto.getThree();
        this.ro = dto.getRo();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象FilterMarksDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(FilterMarksDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setDeviceId(this.deviceId);
        dto.setPp(this.pp);
        dto.setCto(this.cto);
        dto.setUdf(this.udf);
        dto.setThree(this.three);
        dto.setRo(this.ro);
        dto.setCreateTime(this.createTime);
    }
}
