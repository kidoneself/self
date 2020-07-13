package com.yimao.cloud.base.enums;

public enum AppTypeEnum {
    ANDROID("1", "ANDROID"),
    IOS("2", "IOS"),
    OPENAPI("3", "OPENAPI"),
    H5("4", "H5");

    private String id;
    private String type;

    private AppTypeEnum(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public static AppTypeEnum getTypeById(String id) {
        AppTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            AppTypeEnum typeEnum = var1[var3];
            if (typeEnum.id.equals(id)) {
                return typeEnum;
            }
        }

        return null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }
}
