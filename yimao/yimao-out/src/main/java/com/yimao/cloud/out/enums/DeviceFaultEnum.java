package com.yimao.cloud.out.enums;

public enum DeviceFaultEnum {
    DEVICEFAULT_TYPE_MONEY(1, "余额不足"),
    DEVICEFAULT_TYPE_WATER(2, "制水故障"),
    DEVICEFAULT_TYPE_TDS(3, "TDS异常"),
    DEVICEFAULT_TYPE_FILTER(4, "滤芯报警"),
    DEVICEFAULT_TYPE_NOTICE(5, "阀值提醒"),
    DEVICEFAULT_TYPE_OVERDUE(6, "续费超期");

    private int type;
    private String name;

    private DeviceFaultEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getText(int type) {
        DeviceFaultEnum[] enums = values();
        DeviceFaultEnum[] var2 = enums;
        int var3 = enums.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            DeviceFaultEnum d = var2[var4];
            if (d.getType() == type) {
                return d.getName();
            }
        }

        return null;
    }
}
