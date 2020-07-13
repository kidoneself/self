package com.yimao.cloud.water.po;

import com.yimao.cloud.pojo.dto.water.WaterDeviceDynamicCipherRecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：水机设备动态密码
 *
 * @Author Zhang Bo
 * @Date 2019/3/12
 */
@Table(name = "water_device_dynamic_cipher_record")
@Getter
@Setter
public class WaterDeviceDynamicCipherRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sn;                //SN码
    private String password;          //明文密码
    private String passwordDesStr;    //加密密码
    private Integer engineerId;       //安装工程师ID
    private String engineerName;      //安装工程师姓名
    private String engineerPhone;     //安装工程师手机号
    private String validStatus;       //校验状态：Y-可用；N-不可用
    private Date validTime;           //校验截止时间
    private String terminal;          //创建端
    private Date createTime;          //创建时间

    public WaterDeviceDynamicCipherRecord() {
    }

    /**
     * 用业务对象WaterDeviceDynamicCipherRecordDTO初始化数据库对象WaterDeviceDynamicCipherRecord。
     *
     * @param dto 业务对象
     */
    public WaterDeviceDynamicCipherRecord(WaterDeviceDynamicCipherRecordDTO dto) {
        this.id = dto.getId();
        this.sn = dto.getSn();
        this.password = dto.getPassword();
        this.passwordDesStr = dto.getPasswordDesStr();
        this.engineerId = dto.getEngineerId();
        this.engineerName = dto.getEngineerName();
        this.engineerPhone = dto.getEngineerPhone();
        this.validStatus = dto.getValidStatus();
        this.validTime = dto.getValidTime();
        this.terminal = dto.getTerminal();
        this.createTime = dto.getCreateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象WaterDeviceDynamicCipherRecordDTO上。
     *
     * @param dto 业务对象
     */
    public void convert(WaterDeviceDynamicCipherRecordDTO dto) {
        dto.setId(this.id);
        dto.setSn(this.sn);
        dto.setPassword(this.password);
        dto.setPasswordDesStr(this.passwordDesStr);
        dto.setEngineerId(this.engineerId);
        dto.setEngineerName(this.engineerName);
        dto.setEngineerPhone(this.engineerPhone);
        dto.setValidStatus(this.validStatus);
        dto.setValidTime(this.validTime);
        dto.setTerminal(this.terminal);
        dto.setCreateTime(this.createTime);
    }
}
