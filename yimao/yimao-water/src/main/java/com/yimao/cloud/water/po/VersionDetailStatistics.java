package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.VersionDetailStatisticsDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：版本详情统计
 *
 */
@Table(name = "version_detail_statistics")
@Data
public class VersionDetailStatistics {

    @Id
    private Integer id;
    private String version;//水机版本号
    private String snCode;//sn编码
    private Integer consumeFlowType;//消耗流量类型：1-WIFI；3-3G；
    private Date updateVersionTime;//设备更新版本时间
    private Integer deviceGroup;//设备组：1-用户组，2-服务站组


    public VersionDetailStatistics() {
    }

    /**
     * 用业务对象VersionDetailStatisticsDTO初始化数据库对象VersionDetailStatistics。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public VersionDetailStatistics(VersionDetailStatisticsDTO dto) {
        this.id = dto.getId();
        this.version = dto.getVersion();
        this.snCode = dto.getSnCode();
        this.consumeFlowType = dto.getConsumeFlowType();
        this.updateVersionTime = dto.getUpdateVersionTime();
        this.deviceGroup = dto.getDeviceGroup();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象VersionDetailStatisticsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(VersionDetailStatisticsDTO dto) {
        dto.setId(this.id);
        dto.setVersion(this.version);
        dto.setSnCode(this.snCode);
        dto.setConsumeFlowType(this.consumeFlowType);
        dto.setUpdateVersionTime(this.updateVersionTime);
        dto.setDeviceGroup(this.deviceGroup);
    }
}