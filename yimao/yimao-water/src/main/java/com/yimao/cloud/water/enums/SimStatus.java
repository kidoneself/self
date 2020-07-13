package com.yimao.cloud.water.enums;

import java.util.Arrays;
import java.util.Objects;

public enum SimStatus {
    TEST("TEST_READY_NAME", "可测试"),
    INVENTORY("INVENTORY_NAME", "库存"),
    ACTIVATION("ACTIVATION_READY_NAME", "可激活"),
    ACTIVATED("ACTIVATED_NAME", "已激活"),
    DEACTIVATED("DEACTIVATED_NAME", "已停用"),
    RETIRED("RETIRED_NAME", "已失效");

    private String name;
    private String value;

    SimStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String getValue(String name) {
        return Arrays.stream(values()).filter(sim -> Objects.equals(sim.getName(), name)).findFirst().map(SimStatus::getValue).orElse("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
