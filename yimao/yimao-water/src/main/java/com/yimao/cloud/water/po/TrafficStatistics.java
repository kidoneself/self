package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.TrafficStatisticsDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：流量统计
 */
/*@Table(name = "traffic_statistics")*/
@Data
public class TrafficStatistics {
    @Id
    private Integer id;
    private Integer platform;//广告平台：0-翼猫（自有）；1-百度；2-京东；3-科大讯飞；4-简视；5-海大；6-后台交互；10-其它
    private BigDecimal totalTraffic;//总流量
    private Integer requestAmount;//请求次数
    private BigDecimal requestTraffic;// 请求流量(M)
    private Integer downloadsAmount;//下载次数
    private BigDecimal downloadsTraffic;//下载流量(M)
    private Integer callbackAmount;//回调次数
    private BigDecimal callbackTraffic;//回调流量(M)
    private String sn;//sn码
  /*  private String province;//省
    private String city;//市
    private String region;//区
    private String model;//型号
    private Date beginTime;//投放开始时间
    private Date endTime;//投放结束时间*/
    private Integer deviceGroup;//设备组：1-用户组，2-服务站组
    private Date createTime;//创建时间
    private Integer source;//来源：1-3G 2-wifi


    public TrafficStatistics() {
    }

    /**
     * 用业务对象TrafficStatisticsDTO初始化数据库对象TrafficStatistics。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public TrafficStatistics(TrafficStatisticsDTO dto) {
        this.id = dto.getId();
        this.platform = dto.getPlatform();
        this.totalTraffic = dto.getTotalTraffic();
        this.requestAmount = dto.getRequestAmount();
        this.requestTraffic = dto.getRequestTraffic();
        this.downloadsAmount = dto.getDownloadsAmount();
        this.downloadsTraffic = dto.getDownloadsTraffic();
        this.callbackAmount = dto.getCallbackAmount();
        this.callbackTraffic = dto.getCallbackTraffic();
        this.sn = dto.getSn();
        this.deviceGroup = dto.getDeviceGroup();
        this.createTime = dto.getCreateTime();
        this.source = dto.getSource();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象TrafficStatisticsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(TrafficStatisticsDTO dto) {
        dto.setId(this.id);
        dto.setPlatform(this.platform);
        dto.setTotalTraffic(this.totalTraffic);
        dto.setRequestAmount(this.requestAmount);
        dto.setRequestTraffic(this.requestTraffic);
        dto.setDownloadsAmount(this.downloadsAmount);
        dto.setDownloadsTraffic(this.downloadsTraffic);
        dto.setCallbackAmount(this.callbackAmount);
        dto.setCallbackTraffic(this.callbackTraffic);
        dto.setSn(this.sn);
        dto.setDeviceGroup(this.deviceGroup);
        dto.setCreateTime(this.createTime);
        dto.setSource(this.source);
    }
}