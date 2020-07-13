package com.yimao.cloud.base.enums;

/**
 * 用户身份变化枚举
 *
 * @author hhf
 * @date 2019/1/10
 */
public enum UserChangeRecordEnum {

    CREATE_ACCOUNT_EVENT("创建账号",1),
    UPGRADE_SHARE_EVENT("升级分享",2),
    UPGRADE_SALE_EVENT("升级分销用户",3),
    REGISTERED_EVENT("注册",4),
    RENEWAL_EVENT("续费",5),
    UPGRADE_EVENT("升级",6),
    FIRST_ATTENTION_EVENT("首次关注公众号",7),
    CANCEL_LOGIN_EVENT("首次登录小程序",8),
    CANCEL_ATTENTION_EVENT("取消关注公众号",9),
    TRANSFER_EVENT("转让",10),
    EDIT("编辑",11);

    public final String name;
    public final int value;

    UserChangeRecordEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static UserChangeRecordEnum getUserChangeRecord(Integer index) {
        for (UserChangeRecordEnum changeRecordEnum : UserChangeRecordEnum.values()) {
            if (changeRecordEnum.value == (index)) {
                return changeRecordEnum;
            }
        }
        return null;
    }
}
