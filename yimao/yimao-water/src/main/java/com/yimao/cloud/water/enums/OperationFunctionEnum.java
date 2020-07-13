package com.yimao.cloud.water.enums;

/**
 * 区域选择过滤。
 *
 * @author Zhang Bo
 * @date 2019/2/15.
 */
public enum OperationFunctionEnum {

    ADSLOT("广告位", "1"),
    ADSLOT_ONE("新增广告位", "11"),
    ADSLOT_TWO("禁用广告位", "12"),
    ADSLOT_THREE("修改广告位", "13"),

    ADVERTISING("广告投放", "2"),
    ADVERTISING_ONE("添加第三方平台", "21"),
    ADVERTISING_TWO("新增条件投放配置", "22"),
    ADVERTISING_THREE("新增精准投放配置", "23"),
    ADVERTISING_FOUR("编辑条件投放配置", "24"),
    ADVERTISING_FIVE("投放配置下架", "25"),
    ADVERTISING_SIX("投放配置删除", "25"),


    MATERIEL("物料管理", "3"),
    MATERIEL_ONE("新增物料", "31"),
    MATERIEL_TWO("物料规格审核", "32"),
    MATERIEL_THREE("物料支付审核", "33"),
    MATERIEL_FOUR("物料内容审核", "34"),
    MATERIEL_FIVE("物料删除", "35"),

    PLATFORM("第三方平台", "4"),
    PLATFORM_ONE("新增第三方平台", "41"),
    PLATFORM_TWO("第三方平台删除", "42"),
    PLATFORM_THREE("第三方平台更新", "43");


    public final String name;
    public final String value;

    OperationFunctionEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
