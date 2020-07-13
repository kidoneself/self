package com.yimao.cloud.user.po;

import com.yimao.cloud.pojo.dto.user.UsreDistributorApplyDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @description   注册经销商申请信息
 * @author Liu Yi
 * @date 2019/8/30 15:55
 */
@Table(name = "user_distributor_apply")
@Getter
@Setter
public class UserDistributorApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long orderId;                //订单id
    private String realName;                //经销商姓名
    private String nickName;                //经销商昵称
    private Integer sex;                    //性别：1-男；2-女；
    private String phone;                   //手机号
    private String email;                    //邮箱
    private String province;                //所在省
    private String city;                    //所在市
    private String region;                  //所在区
    private String address;                 //地址
    private String idCard;                  //身份证
    private Integer recommendId;            //推荐人ID
    private String oldRecommendId;          //老的推荐人Id
    private String recommendName;           //推荐人姓名
    private Integer terminal;               //经销商创建端：1-翼猫业务系统；2-经销商app；
    private Integer roleId;                 //经销商角色ID
    private Integer roleLevel;              //经销商角色等级：-50-折机版；50-体验版；350-微创版；650-个人版；950-企业版（主）；1000-企业版（子）
    private String roleName;                //经销商角色名称
    private String remark;                  //备注
    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;


    public UserDistributorApply() {
    }

    /**
     * 用业务对象UserDistributorApplyDTO初始化数据库对象UserDistributorApply。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public UserDistributorApply(UsreDistributorApplyDTO dto) {
        this.id = dto.getId();
        this.orderId = dto.getOrderId();
        this.realName = dto.getRealName();
        this.oldRecommendId = dto.getOldRecommendId();
        this.sex = dto.getSex();
        this.phone = dto.getPhone();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.region = dto.getRegion();
        this.address = dto.getAddress();
        this.idCard = dto.getIdCard();
        this.recommendId = dto.getRecommendId();
        this.recommendName = dto.getRecommendName();
        this.terminal = dto.getTerminal();
        this.roleId = dto.getRoleId();
        this.roleLevel = dto.getRoleLevel();
        this.roleName = dto.getRoleName();
        this.remark = dto.getRemark();
        this.creator = dto.getCreator();
        this.createTime = dto.getCreateTime();
        this.updater = dto.getUpdater();
        this.updateTime = dto.getUpdateTime();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象UserDistributorApplyDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(UsreDistributorApplyDTO dto) {
        dto.setId(this.id);
        dto.setOrderId(this.orderId);
        dto.setRealName(this.realName);
        dto.setOldRecommendId(this.oldRecommendId);
        dto.setSex(this.sex);
        dto.setPhone(this.phone);
        dto.setProvince(this.province);
        dto.setCity(this.city);
        dto.setRegion(this.region);
        dto.setAddress(this.address);
        dto.setIdCard(this.idCard);
        dto.setRecommendId(this.recommendId);
        dto.setRecommendName(this.recommendName);
        dto.setTerminal(this.terminal);
        dto.setRoleId(this.roleId);
        dto.setRoleLevel(this.roleLevel);
        dto.setRoleName(this.roleName);
        dto.setRemark(this.remark);
        dto.setCreator(this.creator);
        dto.setCreateTime(this.createTime);
        dto.setUpdater(this.updater);
        dto.setUpdateTime(this.updateTime);
    }
}
