package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.CustomerAreaManagerDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户区域经理
 *
 * @author liuhao@yimaokeji.com
 * 2018052018/5/16
 */
@Table(name = "t_customer_area_manager")
@Data
public class CustomerAreaManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String technicalName;
    private String mobile;//手机号
    private String province;
    private String remark;
    private Boolean deleteFlag;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private String updater;


    public CustomerAreaManager() {
    }

    /**
     * 用业务对象CustomerAreaManagerDTO初始化数据库对象CustomerAreaManager。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public CustomerAreaManager(CustomerAreaManagerDTO dto) {
        this.id = dto.getId();
        this.technicalName = dto.getTechnicalName();
        this.mobile = dto.getMobile();
        this.province = dto.getProvince();
        this.remark = dto.getRemark();
        this.deleteFlag = dto.getDeleteFlag();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
        this.updater = dto.getUpdater();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象CustomerAreaManagerDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(CustomerAreaManagerDTO dto) {
        dto.setId(this.id);
        dto.setTechnicalName(this.technicalName);
        dto.setMobile(this.mobile);
        dto.setProvince(this.province);
        dto.setRemark(this.remark);
        dto.setDeleteFlag(this.deleteFlag);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
