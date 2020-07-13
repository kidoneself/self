package com.yimao.cloud.base.enums;

import java.util.Arrays;

/**
 * 代理商级别：1-省代；2-市代；4-区代；3-省代+市代；5-省代+区代；6-市代+区代；7-省代+市代+区代；
 *
 * @auther: liu.lin
 * @date: 2018/12/25
 */
public enum AgentLevel {

    AGENT_P("省代", 1),
    AGENT_C("市代", 2),
    AGENT_R("区代", 4),
    AGENT_PC("省代+市代", 3),
    AGENT_PR("省代+区代", 5),
    AGENT_CR("市代+区代", 6),
    AGENT_PCR("省+市+区代", 7);

    public final String name;
    public final int value;

    AgentLevel(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static AgentLevel find(int value) {
        return Arrays.stream(values()).filter(item -> item.value == value).findFirst().orElse(null);
    }

    /**
     * 判断是否为省代
     *
     * @param value
     */
    public static boolean isProvinceAgent(int value) {
        return value == AGENT_P.value || value == AGENT_PC.value || value == AGENT_PR.value || value == AGENT_PCR.value;
    }

    /**
     * 判断是否为市代
     *
     * @param value
     */
    public static boolean isCityAgent(int value) {
        return value == AGENT_C.value || value == AGENT_PC.value || value == AGENT_CR.value || value == AGENT_PCR.value;
    }

    /**
     * 判断是否为区代
     *
     * @param value
     */
    public static boolean isRegionAgent(int value) {
        return value == AGENT_R.value || value == AGENT_PR.value || value == AGENT_CR.value || value == AGENT_PCR.value;
    }

}
