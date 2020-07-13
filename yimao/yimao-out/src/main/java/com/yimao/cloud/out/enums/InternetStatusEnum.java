package com.yimao.cloud.out.enums;

public enum InternetStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线");

    private int index;
    private String name;

    InternetStatusEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
