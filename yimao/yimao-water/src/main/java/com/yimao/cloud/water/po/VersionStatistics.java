package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.VersionStatisticsDTO;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述：版本统计
 *
 */
@Table(name = "version_statistics")
@Data
public class VersionStatistics {

    @Id
    private Integer id;
    private String version;//水机版本号
    private Integer successCount;//升级成功设备数
    private Integer deviceCount; //设备总数


    public VersionStatistics() {
    }

    /**
     * 用业务对象VersionStatisticsDTO初始化数据库对象VersionStatistics。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public VersionStatistics(VersionStatisticsDTO dto) {
        this.id = dto.getId();
        this.version = dto.getVersion();
        this.successCount = dto.getSuccessCount();
        this.deviceCount = dto.getDeviceCount();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象VersionStatisticsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(VersionStatisticsDTO dto) {
        dto.setId(this.id);
        dto.setVersion(this.version);
        dto.setSuccessCount(this.successCount);
        dto.setDeviceCount(this.deviceCount);
    }
}