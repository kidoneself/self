package com.yimao.cloud.pojo.dto.user;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * @description   红包账户
 * @author Liu Yi
 * @date 2019/9/9 11:31
 */
@Getter
@Setter
public class RedAccountDTO {
    private static final long serialVersionUID = 8424210036922497445L;

    private Integer id;
    private String oldId;

    private String yimaoOldSystemId;
    private String idStatus;
    private String isMasterAccount;
    private String userName;
    private String roleIds;
    private String roleNames;
    private Integer accountId;
    private String oldAccountId;
    private String accountFatherId;
    private int currentMoney;
    private int lockedMoney;
    private int totalMoney;
    private String allowCollectMoney;
    private String allowPayMoney;
    private String bindAlipayAccount;
    private String bindAlipayRealName;
    private String addrProvinceId;
    private String addrProvinceName;
    private String addrCityId;
    private String addrCityName;
    private String addrRegionId;
    private String addrRegionName;

    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String delStatus;
    private Date deleteTime;
}
