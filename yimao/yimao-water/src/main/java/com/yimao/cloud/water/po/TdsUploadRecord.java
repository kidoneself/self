package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.TdsUploadRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：设备TDS值上传记录
 *
 * @Author Zhang Bo
 * @Date 2019/5/10
 */
@Table(name = "tds_upload_record")
@Getter
@Setter
public class TdsUploadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 水机设备SN码
     */
    private String sn;
    /**
     * 水机设备ID
     */
    private Integer deviceId;
    /**
     * 安装工ID
     */
    private Integer engineerId;
    /**
     * 安装工姓名
     */
    private String engineerName;
    /**
     * 原K
     */
    private Double k;
    /**
     * 原T
     */
    private Double t;
    /**
     * 新K
     */
    private Double currentK;
    /**
     * 新T
     */
    private Double currentT;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 操作类型：1-恢复TDS；2-修改TDS
     */
    private Integer type;
    /**
     * 操作类型名称
     */
    private String typeName;
    /**
     * 审核状态：Y-已审核；N-未审核；
     */
    private String verifyStatus;
    /**
     * 审核结果：Y-审核通过；N-审核不通过；
     */
    private String verifyResult;
    /**
     * 审核原因
     */
    private String verifyReason;
    /**
     * 审核人
     */
    private String verifyUser;
    /**
     * 审核时间
     */
    private Date verifyTime;

    public TdsUploadRecord() {
    }

    /**
     * 用业务对象TdsUploadRecordDTO初始化数据库对象TdsUploadRecord。
     *
     * @param dto 业务对象
     */
    public TdsUploadRecord(TdsUploadRecordDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.deviceId = dto.getDeviceId();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.k = dto.getK();
        this.t = dto.getT();
        this.currentK = dto.getCurrentK();
        this.currentT = dto.getCurrentT();
        this.createTime = dto.getCreateTime();
        this.type = dto.getType();
        this.typeName = dto.getTypeName();
        this.verifyStatus = dto.getVerifyStatus();
        this.verifyResult = dto.getVerifyResult();
        this.verifyReason = dto.getVerifyReason();
        this.verifyUser = dto.getVerifyUser();
        this.verifyTime = dto.getVerifyTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象TdsUploadRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(TdsUploadRecordDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setDeviceId(this.deviceId);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setK(this.k);
        dto.setT(this.t);
        dto.setCurrentK(this.currentK);
        dto.setCurrentT(this.currentT);
        dto.setCreateTime(this.createTime);
        dto.setType(this.type);
        dto.setTypeName(this.typeName);
        dto.setVerifyStatus(this.verifyStatus);
        dto.setVerifyResult(this.verifyResult);
        dto.setVerifyReason(this.verifyReason);
        dto.setVerifyUser(this.verifyUser);
        dto.setVerifyTime(this.verifyTime);
    }
}
