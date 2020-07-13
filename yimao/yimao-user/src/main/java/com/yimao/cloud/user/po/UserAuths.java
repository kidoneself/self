package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserAuthsDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户授权信息表
 *
 * @author Zhang Bo
 * @date 2018/5/9.
 */
@Table(name = "user_auths")
@Data
public class UserAuths implements Serializable {

    private static final long serialVersionUID = 5544622154784823510L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;                      //用户ID（e家号）
    private Integer identityType;                //登录类别：1-健康e家公众号、2-健康自测小程序、4-H5分享页面、5-翼猫APP
    private String identifier;                   //第三方识别码
    private String mobile;                       //手机号
    private String identifierUnique;             //第三方唯一识别码
    private String credential;                   //第三方登录凭据
    private Integer state;                       //状态：0-解绑，1-绑定
    private Date createTime;
    private Date updateTime;


    public UserAuths() {
    }

    /**
     * 用业务对象UserAuthsDTO初始化数据库对象UserAuths。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserAuths(UserAuthsDTO dto) {

        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.identityType = dto.getIdentityType();
        this.identifier = dto.getIdentifier();
        this.mobile = dto.getMobile();
        this.identifierUnique = dto.getIdentifierUnique();
        this.credential = dto.getCredential();
        this.state = dto.getState();
        this.createTime = dto.getCreateTime();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserAuthsDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UserAuthsDTO dto) {

        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setIdentityType(this.identityType);
        dto.setIdentifier(this.identifier);
        dto.setMobile(this.mobile);
        dto.setIdentifierUnique(this.identifierUnique);
        dto.setCredential(this.credential);
        dto.setState(this.state);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
    }
}
