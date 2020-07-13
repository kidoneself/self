package com.yimao.cloud.out.enums;

public enum DeviceScanEnum {

    ALL(1, "所有设备"),
    OWNER(2, "自己所服务的设备");

    private int deviceScope;
    private String desc;

    DeviceScanEnum(int deviceScope, String desc) {
        this.deviceScope = deviceScope;
        this.desc = desc;
    }

    public static DeviceScanEnum findByScope(int scope) {
        if (scope > 0) {
            DeviceScanEnum[] scans = values();
            DeviceScanEnum[] var2 = scans;
            int var3 = scans.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                DeviceScanEnum type = var2[var4];
                if (type.getDeviceScope() == scope) {
                    return type;
                }
            }
        }

        return null;
    }

    public int getDeviceScope() {
        return this.deviceScope;
    }

    public void setDeviceScope(int deviceScope) {
        this.deviceScope = deviceScope;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
