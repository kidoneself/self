package com.yimao.cloud.system.po;

import com.yimao.cloud.pojo.dto.system.SuggestDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 站务系统 建议反馈表
 *
 */
@Table(name = "suggest")
@Getter
@Setter
public class Suggest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //建议类型
    private Integer suggestType;
    //反馈内容
    private String content;
    //附件
    private String accessory;
    //展示端(1-净水设备 2-健康e家公众号 3-翼猫APP 4-健康自测小程序 5-站务系统 10-管理后台 ) ps：目前只针对站务系统
    private Integer terminal;
    //服务站id
    private Integer stationId;
    //状态 (0-未回复 1-已回复)
    private Integer status;
    //角色id
    private Integer roleId;
    //角色名称
    private String roleName;
    //反馈者id
    private Integer userId;
    //反馈者姓名
    private String name;
    //反馈时间
    private Date time;

    public Suggest() {
    }

    /**
     * 用业务对象SuggestDTO初始化数据库对象Suggest。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public Suggest(SuggestDTO dto) {
        this.id = dto.getId();
        this.suggestType = dto.getSuggestType();
        this.content = dto.getContent();
        this.accessory = dto.getAccessory();
        this.terminal = dto.getTerminal();
        this.stationId = dto.getStationId();
        this.status = dto.getStatus();
        this.roleId = dto.getRoleId();
        this.roleName = dto.getRoleName();
        this.userId = dto.getUserId();
        this.name = dto.getName();
        this.time = dto.getTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象SuggestDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(SuggestDTO dto) {
        dto.setId(this.id);
        dto.setSuggestType(this.suggestType);
        dto.setContent(this.content);
        dto.setAccessory(this.accessory);
        dto.setTerminal(this.terminal);
        dto.setStationId(this.stationId);
        dto.setStatus(this.status);
        dto.setRoleId(this.roleId);
        dto.setRoleName(this.roleName);
        dto.setUserId(this.userId);
        dto.setName(this.name);
        dto.setTime(this.time);
    }
}
