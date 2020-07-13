package com.yimao.cloud.out.enums;

public enum Moudles {
    NULL("NULL", "NULL", "COMMON", "2017/10/23"),
    LOG("log", "日志服务", "COMMON", "2017/10/23"),
    COMMON("common", "基础服务", "COMMON", "2017/11/3"),
    SYSTEM("system", "系统管理", "", "2017/11/30"),
    FILE_SERVICE("fileService", "文件服务", "COMMON", "2017/11/30"),
    AD_SERVICE("adService", "广告服务", "SYSTEM", "2017/11/30"),
    USER_CENTER("userCenter", "用户服务", "COMMON", "2017/11/30"),
    MESSAGE("message", "消息服务", "COMMON", "2017/11/30"),
    ASSEMBLY("assembly", "组件服务", "ASSEMBLY", "2017/11/23"),
    ORDER_CENTER("orderCenter", "订单服务", "COMMON", "2017/11/3"),
    GOODS_SYSTEM("goodsSystem", "产品体系服务", "COMMON", "2017/11/30"),
    BUSINESS_HEALTH("businessHealthSystem", "健康产品服务", "COMMON", "2017/11/30"),
    BUSINESS_WATER_DEVICE("businessWaterDevice", "净水体系服务", "COMMON", "2017/11/30");

    String id;
    String moudleName;
    String modsId;
    String createTime;

    private Moudles(String id, String moudleName, String modsId, String createTime) {
        this.id = id;
        this.moudleName = moudleName;
        this.modsId = modsId;
        this.createTime = createTime;
    }

    public String getId() {
        return this.id;
    }

    public String getMoudleName() {
        return this.moudleName;
    }

    public String getModsId() {
        return this.modsId;
    }

    public String getCreateTime() {
        return this.createTime;
    }
}
