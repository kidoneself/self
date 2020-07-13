package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserAliasDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author hhf
 * @date 2019/4/20
 */
@Table(name = "user_alias")
@Getter
@Setter
public class UserAlias {

    private Integer userId; //健康大使ID
    private Integer clientId; //客户ID
    private String clientName; //客户别名
    private Integer starNum; //几星
    private Date createTime; //创建时间
    private Date updateTime; //更新时间

    public UserAlias() {
    }

    /**
     * 用业务对象UserAliasDTO初始化数据库对象UserAlias。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserAlias(UserAliasDTO dto) {
        this.userId = dto.getUserId();
        this.clientId = dto.getClientId();
        this.clientName = dto.getClientName();
        this.starNum = dto.getStarNum();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserAliasDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UserAliasDTO dto) {
        dto.setUserId(this.userId);
        dto.setClientId(this.clientId);
        dto.setClientName(this.clientName);
        dto.setStarNum(this.starNum);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
