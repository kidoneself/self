package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherConfigDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备动态密码配置
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@Table(name = "water_device_dynamic_cipher_config")
@Getter
@Setter
public class WaterDeviceDynamicCipherConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer typeCode;     //类型code
    private String typeValue;     //类型值
    private Integer validMinute;  //有效期长度

    private Boolean deleted;      //删除状态：0-未删除；1-删除
    private String creator;       //创建人
    private Date createTime;      //创建时间
    private String updater;       //更新人
    private Date updateTime;      //更新时间

    public WaterDeviceDynamicCipherConfig() {
    }

    /**
     * 用业务对象WaterDeviceDynamicCipherConfigDTO初始化数据库对象WaterDeviceDynamicCipherConfig。
     *
     * @param dto 业务对象
     */
    public WaterDeviceDynamicCipherConfig(WaterDeviceDynamicCipherConfigDTO dto) {
        this.id = dto.getId();
        this.typeCode = dto.getTypeCode();
        this.typeValue = dto.getTypeValue();
        this.validMinute = dto.getValidMinute();
        this.deleted = dto.getDeleted();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceDynamicCipherConfigDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceDynamicCipherConfigDTO dto) {
        dto.setId(this.id);
        dto.setTypeCode(this.typeCode);
        dto.setTypeValue(this.typeValue);
        dto.setValidMinute(this.validMinute);
        dto.setDeleted(this.deleted);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
