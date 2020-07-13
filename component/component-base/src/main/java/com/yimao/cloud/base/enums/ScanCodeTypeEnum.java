package com.yimao.cloud.base.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum ScanCodeTypeEnum {
    YIMAO(0, "翼猫企业二维码"),
    LIVE(1, "直播二维码"),
    CHILD(2, "企业账号二维码"),
    PERSON(3, "个人二维码"),
    SOFT(4, "软文二维码"),
    MALL(5, "翼猫微商城"),
    DISTRIBUTOR(6, "翼猫APP下单");

    private int status;
    private String describe;

    private ScanCodeTypeEnum(int status, String describe) {
        this.status = status;
        this.describe = describe;
    }

    public int getStatus() {
        return this.status;
    }

    public String getDescribe() {
        return this.describe;
    }

    public static String getDgetDescribe(Integer status) {
        if (status == null) {
            return null;
        } else {
            ScanCodeTypeEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                ScanCodeTypeEnum s = var1[var3];
                if (s.getStatus() == status) {
                    return s.getDescribe();
                }
            }

            return null;
        }
    }

    public static List<ScanCodeTypeEnum> getListNoChild() {
        List<ScanCodeTypeEnum> lists = Lists.newArrayList();
        ScanCodeTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ScanCodeTypeEnum s = var1[var3];
            if (s.getStatus() != CHILD.getStatus()) {
                lists.add(s);
            }
        }

        return lists;
    }

    public static boolean isUserOrder(int status) {
        return LIVE.status == status || PERSON.status == status || SOFT.status == status || MALL.status == status;
    }

    public static boolean isUserOrderText(String describe) {
        return LIVE.describe.equals(describe) || PERSON.describe.equals(describe) || SOFT.describe.equals(describe) || MALL.describe.equals(describe);
    }
}
