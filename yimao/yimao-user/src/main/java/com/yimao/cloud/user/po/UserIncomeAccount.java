package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UserIncomeAccountDTO;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 收益账户(默认经销商)
 *
 * @author liuhao@yimaokeji.com
 * @version v2.0
 * @date 2019/4/12
 */
@Data
@Table(name = "user_income_account")
public class UserIncomeAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId; //用户Id
    private Integer distributorId; //经销商id
    private String account; //经销商账户
    private String creator; //创建人
    private Date createTime;    //创建时间
    private String updater;     //修改人
    private Date updateTime;       // 修改时间

    /**
     * 初始化数据库
     *
     * @param dto 业务对象
     */
    public UserIncomeAccount(UserIncomeAccountDTO dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.distributorId = dto.getUserId();
        this.account = dto.getAccount();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 拷贝到业务对象UserIncomeAccountDTO上
     *
     * @param dto 业务对象
     */
    public void convert(UserIncomeAccountDTO dto) {
        dto.setId(this.id);
        dto.setUserId(this.userId);
        dto.setAccount(this.account);
        dto.setDistributorId(this.distributorId);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdateTime(this.updateTime);
        dto.setUpdater(this.updater);
    }
}
